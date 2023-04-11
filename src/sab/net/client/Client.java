package sab.net.client;

import sab.net.Connection;
import sab.net.packet.Packet;
import sab.net.packet.PacketManager;

import java.io.IOException;

public class Client {
    private final int id;
    private final Connection connection;
    private final PacketManager packetManager;
    private ClientListener listener;

    public Client(String host, int port, PacketManager packetManager) throws IOException {
        connection = new Connection(host, port);
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
