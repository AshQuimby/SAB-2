package sab.game.screen;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.error.SabError;
import sab.game.Settings;
import sab.net.Keys;
import sab.net.client.Client;
import sab.net.client.ClientListener;
import sab.net.packet.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

import java.io.IOException;

public class JoinGameScreen extends ScreenAdapter {
    private volatile Client client;
    private volatile SabError error;

    private Battle battle;

    public JoinGameScreen() {
        Thread connect = new Thread(
                () -> {
                    try {
                        client = new Client("localhost", Settings.getHostingPort(), new SabPacketManager());
                    } catch (IOException ignored) {
                    }
                }
        );

        connect.setName("Client Connection");
        connect.setDaemon(true);
        connect.start();

        long timestamp = System.currentTimeMillis();
        while (System.currentTimeMillis() - timestamp < 3000) {
            if (client != null) break;
        }

//        if (client != null) {
//            client.addClientListener(new ClientListener() {
//                @Override
//                public void received(Packet packet) {
//                    if (packet instanceof KickPacket kickPacket) {
//                        error = new SabError("Kicked", kickPacket.message);
//                    }
//
//                    if (packet instanceof PlayerStatePacket playerStatePacket) {
//                        playerStatePacket.syncPlayer(battle.getPlayer(playerStatePacket.playerId));
//                    }
//
//                    if (packet instanceof SpawnParticlePacket particlePacket) {
//                        battle.addParticle(particlePacket.particle);
//                    }
//                }
//
//                @Override
//                public void disconnected() {
//                    error = new SabError("Disconnected", "Lost connection to the server");
//                }
//            });
//        } else {
//            error = new SabError("Connection refused", "Failed to connect to server");
//            return;
//        }

        battle = new Battle();
    }

    private void pressKey(byte key) {
        battle.getPlayer(1).keys.press(key);
        client.send(new KeyEventPacket(key, true));
    }

    private void releaseKey(byte key) {
        battle.getPlayer(1).keys.release(key);
        client.send(new KeyEventPacket(key, false));
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
    public void render(Seagraphics g) {
        battle.render(g);
    }

    @Override
    public Screen update() {
        if (error != null) {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ignored) {
                }
            }

            return new ErrorScreen(error);
        }

        battle.update();
        return new JoinedCharacterSelectScreen(client);
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException ignored) {
        }
    }
}