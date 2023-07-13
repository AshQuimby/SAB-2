package sab.net.client;

import sab.error.SabError;
import sab.game.Game;
import sab.modloader.Mod;
import sab.net.Connection;
import sab.net.JoinGameException;
import sab.net.Protocol;
import sab.net.packet.Packet;
import sab.net.packet.PacketManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private final int id;
    private final Connection connection;
    private final PacketManager packetManager;
    private ClientListener listener;

    public Client(String host, int port, PacketManager packetManager) throws IOException, JoinGameException {
        SabError error = null;
        connection = new Connection(host, port);
        int protocolVersion = connection.readInt();
        if (protocolVersion > Protocol.PROTOCOL_VERSION) {
            error = new SabError("Client Out of Date", "You are running an outdated version of the game");
        } else if (protocolVersion < Protocol.PROTOCOL_VERSION) {
            error = new SabError("Server Out of Date", "The server is outdated");
        }
        int modCount = connection.readInt();
        if (modCount < 0) {
            error = new SabError("Server Error", "The server decided to say it has a negative number of mods...");
        }
        List<String> modErrors = new ArrayList<>(modCount);
        for (int i = 0; i < modCount; i++) {
            String namespace = connection.readUTF();
            String version = connection.readUTF();
            Mod mod = Game.game.mods.get(namespace);

            if (mod == null) {
                modErrors.add(String.format("Missing mod: %sv%s", namespace, version));
            } else {
                String localVersion = mod.version;
                if (!localVersion.equals(version)) {
                    modErrors.add(String.format("Expected %sv%s, found %sv%s", namespace, version, namespace, localVersion));
                }
            }
        }
        if (modErrors.size() > 0) {
            error = new SabError("Mod Error", String.join("\n", modErrors));
        }

        if (error != null) {
            throw new JoinGameException(error);
        }

        id = connection.readInt();
        this.packetManager = packetManager;

        Thread receiver = new Thread(
                () -> {
                    while (true) {
                        try {
                            byte header = connection.readByte();
                            Packet packet = packetManager.getPacket(header);

                            // Invalid packet type or error creating packet
                            if (packet == null) {
                                disconnect();
                                break;
                            }

                            packet.receive(connection);
                            receive(packet);
                        } catch (IOException e) {
                            disconnect();
                            break;
                        }
                    }
                }
        );

        receiver.setName("Packet Handler");
        receiver.setDaemon(true);
        receiver.start();
    }

    public void send(Packet packet) {
        try {
            packetManager.sendPacket(connection, packet);
        } catch (IOException e) {
            e.printStackTrace();
            disconnect();
        }
    }

    public void close() throws IOException {
        connection.close();
    }

    public int getId() {
        return id;
    }

    public void setClientListener(ClientListener listener) {
        this.listener = listener;
    }

    private void receive(Packet packet) {
        if (listener != null) {
            listener.received(packet);
        }
    }

    private void disconnect() {
        if (listener != null) {
            listener.disconnected();
        }
    }
}
