package sab.game.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Battle;
import sab.game.Player;
import sab.game.particle.Particle;
import sab.net.*;
import sab.net.packet.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class HostedBattleScreen extends ScreenAdapter {
    private static class ClientHandler implements Runnable {
        private HostedBattleScreen owner;
        private Battle battle;
        private Connection client;
        private byte id;

        public ClientHandler(HostedBattleScreen owner, Battle battle, Connection client, byte id) {
            this.owner = owner;
            this.battle = battle;
            this.client = client;
            this.id = id;
        }

        @Override
        public void run() {
            try {
                client.writeByte(id);
            } catch (IOException e) {
                return;
            }
            while (true) {
                try {
                    byte header = client.readByte();
                    Packet packet = owner.packets.getPacket(header);

                    if (packet == null) { // Invalid packet type or error creating packet
                        owner.packets.sendPacket(client, new KickPacket("Invalid packet type: " + Byte.toUnsignedInt(header)));
                        break;
                    } else {
                        packet.receive(client);
                    }

                    if (packet instanceof KickPacket kickPacket) {
                        // Nice try!
                        owner.packets.sendPacket(client, new KickPacket(kickPacket.message));
                        break;
                    }

                    if (packet instanceof KeyEventPacket keyEventPacket) {
                        if (!Keys.isValidKey(keyEventPacket.key)) {
                            owner.packets.sendPacket(client, new KickPacket("Invalid input"));
                            break;
                        }

                        if (keyEventPacket.state) {
                            battle.getPlayer(id).keys.press(keyEventPacket.key);
                        } else {
                            battle.getPlayer(id).keys.release(keyEventPacket.key);
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }


    }

    private Server server;
    private Connection localConnection;
    private volatile List<Connection> connections;
    private PacketManager packets;
    private long lastStateBroadcast;
    private volatile SabError error;

    private Battle battle;

    public HostedBattleScreen(int port) {
        try {
            server = new Server(port);
        } catch (IOException e) {
            error = new SabError("Failed to start server", "The server could not be started");
            return;
        }

        packets = new PacketManager();
        packets.register(KeyEventPacket.class);
        packets.register(KickPacket.class);
        packets.register(PlayerStatePacket.class);
        packets.register(SpawnParticlePacket.class);

        battle = new Battle();
        battle.onSpawnParticle(
                (Particle particle) -> {
                    List<Connection> disconnected = new ArrayList<>();

                    Packet spawnParticlePacket = new SpawnParticlePacket(particle);
                    for (Connection connection : connections) {
                        try {
                            packets.sendPacket(connection, spawnParticlePacket);
                        } catch (IOException e) {
                            disconnected.add(connection);
                        }
                    }

                    for (Connection connection : disconnected) {
                        try {
                            connection.close();
                        } catch (IOException ignored) {

                        }
                        connections.remove(connection);
                    }
                }
        );

//        battle.onAddGameObject(
//                (GameObject gameObject) -> {
//                    List<Connection> disconnected = new ArrayList<>();
//
//                    for (Connection connection : connections) {
//                        try {
//                            if (gameObject instanceof Attack attack) {
//                                Class<? extends AttackType> type = attack.type.getClass();
//                                Packets.sendSpawnAttack(connection, attack.owner.id, type);
//                            }
//                        } catch (RuntimeException e) {
//                            disconnected.add(connection);
//                        }
//                    }
//
//                    for (Connection connection : disconnected) {
//                        connection.close();
//                        connections.remove(connection);
//                    }
//                }
//        );

        connections = new ArrayList<>();

        try {
            localConnection = new Connection("localhost", 25565);
        } catch (IOException e) {
            e.printStackTrace();
            error = new SabError("Connection refused", "Failed to connect to server");
            return;
        }

        try {
            Connection client = server.accept();
            connections.add(client);
            new Thread(new ClientHandler(this, battle, client, (byte) 0)).start();
        } catch (IOException e) {
            e.printStackTrace();
            error = new SabError("Server Error", "Error accepting connection");
            return;
        }

        new Thread(() -> {
            outer:
            while (true) {
                if (connections.size() < 2) {
                    Connection newConnection = null;

                    while (newConnection == null) {
                        if (connections.size() == 0) {
                            break outer;
                        }

                        try {
                            newConnection = server.accept();
                        } catch (IOException e) { // This occurs when the server is closed
                            break outer;
                        }
                    }

                    System.out.println("New connection");
                    connections.add(newConnection);
                    new Thread(new ClientHandler(this, battle, newConnection, (byte) 1)).start();
                }
            }
        }).start();

        lastStateBroadcast = System.currentTimeMillis();
    }

    private void pressKey(byte key) {
        try {
            packets.sendPacket(localConnection, new KeyEventPacket(key, true));
        } catch (IOException e) {
            e.printStackTrace();
            error = new SabError("Disconnected", "Lost connection to server");
        }
    }

    private void releaseKey(byte key) {
        try {
            packets.sendPacket(localConnection, new KeyEventPacket(key, false));
        } catch (IOException e) {
            e.printStackTrace();
            error = new SabError("Disconnected", "Lost connection to server");
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
    public Screen update() {
        if (error != null) {
            for (Connection connection : connections) {
                try {
                    connection.close();
                } catch (IOException ignored) {

                }
            }

            connections.clear();
            return new ErrorScreen(error);
        }

        battle.update();

        if (System.currentTimeMillis() - lastStateBroadcast > 50) {
            for (byte i = 0; i < 2; i++) {
                Player player = battle.getPlayer(i);

                List<Connection> disconnected = new ArrayList<>();
                for (Connection connection : connections) {
                    try {
                        packets.sendPacket(connection, new PlayerStatePacket(player));
                    } catch (IOException e) {
                        disconnected.add(connection);
                    }
                }

                for (Connection connection : disconnected) {
                    try {
                        connection.close();
                    } catch (IOException ignored) {

                    }
                    connections.remove(connection);
                }
            }

            lastStateBroadcast = System.currentTimeMillis();
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }

    @Override
    public void close() {
        for (Connection connection : connections) {
            try {
                packets.sendPacket(connection, new KickPacket("Server closed"));
                connection.close();
            } catch (IOException ignored) {

            }
        }
        connections.clear();

        try {
            localConnection.close();
            server.close();
        } catch (IOException ignored) {

        }
    }
}