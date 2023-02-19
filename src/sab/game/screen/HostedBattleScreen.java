package sab.game.screen;

import java.io.IOException;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Battle;
import sab.game.Player;
import sab.game.particle.Particle;
import sab.net.*;
import sab.net.packet.*;
import sab.net.server.Server;
import sab.net.server.ServerListener;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class HostedBattleScreen extends ScreenAdapter {
    private Server server;
    private volatile int remoteClient;
    private long lastStateBroadcast;
    private volatile SabError error;

    private Battle battle;

    public HostedBattleScreen(Server server, int remoteClient) {
        this.server = server;
        this.remoteClient = remoteClient;

        server.addServerListener(new ServerListener() {
            @Override
            public void connected(int connection) {
                server.send(connection, new JoinedGamePacket((byte) 0xff));
            }

            @Override
            public void disconnected(int connection) {
                // TODO: Player 2 disconnected, accept another one
            }

            @Override
            public void received(int connection, Packet packet) {
                if (packet instanceof KickPacket kickPacket) {
                    // Nice try!
                    server.send(connection, new KickPacket(kickPacket.message));
                    server.disconnect(connection);
                }

                if (packet instanceof KeyEventPacket keyEventPacket) {
                    if (!Keys.isValidKey(keyEventPacket.key)) {
                        server.send(connection, new KickPacket("Invalid input"));
                        server.disconnect(connection);
                    }

                    // The remote connection is player 2, the host is player 1
                    if (keyEventPacket.state) {
                        battle.getPlayer(1).keys.press(keyEventPacket.key);
                    } else {
                        battle.getPlayer(1).keys.release(keyEventPacket.key);
                    }
                }
            }
        });

        battle = new Battle() {
            @Override
            public void onSpawnParticle(Particle particle) {
                server.sendToAll(new SpawnParticlePacket(particle));
            }

        };

        // TODO: Ask wilson to explain this
//        battle.onSpawnParticle(
//                (Particle particle) -> server.sendToAll(new SpawnParticlePacket(particle))
//        );

        lastStateBroadcast = System.currentTimeMillis();
    }

    private void pressKey(byte key) {
        battle.getPlayer(0).keys.press(key);
//        try {
//            packets.sendPacket(localConnection, new KeyEventPacket(key, true));
//        } catch (IOException e) {
//            e.printStackTrace();
//            error = new SabError("Disconnected", "Lost connection to server");
//        }
    }

    private void releaseKey(byte key) {
        battle.getPlayer(0).keys.release(key);
//        try {
//            packets.sendPacket(localConnection, new KeyEventPacket(key, false));
//        } catch (IOException e) {
//            e.printStackTrace();
//            error = new SabError("Disconnected", "Lost connection to server");
//        }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W || keyCode == Input.Keys.UP) {
            pressKey(Keys.UP);
        }
        if (keyCode == Input.Keys.A || keyCode == Input.Keys.LEFT) {
            pressKey(Keys.LEFT);
        }
        if (keyCode == Input.Keys.S || keyCode == Input.Keys.DOWN) {
            pressKey(Keys.DOWN);
        }
        if (keyCode == Input.Keys.D || keyCode == Input.Keys.RIGHT) {
            pressKey(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F || keyCode == Input.Keys.M) {
            pressKey(Keys.ATTACK);
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.W || keyCode == Input.Keys.UP) {
            releaseKey(Keys.UP);
        }
        if (keyCode == Input.Keys.A || keyCode == Input.Keys.LEFT) {
            releaseKey(Keys.LEFT);
        }
        if (keyCode == Input.Keys.S || keyCode == Input.Keys.DOWN) {
            releaseKey(Keys.DOWN);
        }
        if (keyCode == Input.Keys.D || keyCode == Input.Keys.RIGHT) {
            releaseKey(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F || keyCode == Input.Keys.M) {
            releaseKey(Keys.ATTACK);
        }

        return this;
    }

    @Override
    public Screen update() {
        if (error != null) {
            return new ErrorScreen(error);
        }

        battle.update();

        if (System.currentTimeMillis() - lastStateBroadcast > 50) {
            for (byte i = 0; i < 2; i++) {
                Player player = battle.getPlayer(i);
                server.sendToAll(new PlayerStatePacket(player));
            }

            lastStateBroadcast = System.currentTimeMillis();
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        // If the server could not be created, the battle will be null
        if (battle != null) {
            battle.render(g);
        }
    }

    @Override
    public void close() {
        server.sendToAll(new KickPacket("Server closed"));

        try {
            server.close();
        } catch (IOException ignored) {
        }
    }
}