package sab.game.screen;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.net.Connection;
import sab.error.SabError;
import sab.net.Keys;
import sab.net.packet.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

import java.io.IOException;

public class JoinGameScreen extends ScreenAdapter {
    private Battle battle;
    private volatile byte id;
    private Connection connection;
    private PacketManager packets;

    private SabError error;

    public JoinGameScreen() {
        id = -1;

        try {
            connection = new Connection("localhost", 25565);

            new Thread(() -> {
                try {
                    id = connection.readByte();
                } catch (IOException ignored) {

                }
            }).start();

            long timestamp = System.currentTimeMillis();
            while (System.currentTimeMillis() - timestamp < 3000) {
                if (id != -1) {
                    break;
                }
            }

            if (id == -1) {
                error = new SabError("Connection refused", "Failed to connect to server");
                connection.close();
            }
        } catch(IOException e) {
            error = new SabError("Connection refused", "Failed to connect to server");
        }
        if (error != null) return;

        packets = new PacketManager();
        packets.register(KeyEventPacket.class);
        packets.register(KickPacket.class);
        packets.register(PlayerStatePacket.class);
        packets.register(SpawnParticlePacket.class);

        battle = new Battle();

        new Thread(() -> {
            while (true) {
                try {
                    byte header = connection.readByte();
                    Packet packet = packets.getPacket(header);

                    if (packet == null) { // Invalid packet type or error creating packet
                        error = new SabError("Invalid server packet", "Received invalid packet type: " + Byte.toUnsignedInt(header));
                        break;
                    } else {
                        packet.receive(connection);
                    }

                    if (packet instanceof KickPacket kickPacket) {
                        error = new SabError("Kicked", kickPacket.message);
                        break;
                    }

                    if (packet instanceof PlayerStatePacket playerStatePacket) {
                        playerStatePacket.syncPlayer(battle.getPlayer(playerStatePacket.playerId));
                    }

                    if (packet instanceof SpawnParticlePacket particlePacket) {
                        battle.addParticle(particlePacket.particle);
                    }
                } catch (IOException e) {
                    error = new SabError("Disconnected", "Lost connection to the server");
                    break;
                }
            }
        }).start();
    }

    private void pressKey(byte key) {
        battle.getPlayer(1).keys.press(key);

        try {
            packets.sendPacket(connection, new KeyEventPacket(key, true));
        } catch (IOException e) {
            error = new SabError("Disconnected", "Lost connection to the server");
        }
    }

    private void releaseKey(byte key) {
        battle.getPlayer(1).keys.release(key);

        try {
            packets.sendPacket(connection, new KeyEventPacket(key, false));
        } catch (IOException e) {
            error = new SabError("Disconnected", "Lost connection to the server");
        }
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
            if (connection != null) {
                try {
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return new ErrorScreen(error);
        }

        battle.update();
        return this;
    }

    @Override
    public void close() {
        try {
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}