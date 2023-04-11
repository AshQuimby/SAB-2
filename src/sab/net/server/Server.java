package sab.net.server;
import sab.net.Connection;
import sab.net.packet.Packet;
import sab.net.packet.PacketManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    private final ServerSocket serverSocket;
    private final PacketManager packetManager;
    private final List<Connection> connections;
    private ServerListener listener;

    private final Map<Connection, Integer> idsByConnection;
    private final Map<Integer, Connection> connectionsById;

    private int nextId;

    public Server(PacketManager packetManager, int port) throws IOException {
        serverSocket = new ServerSocket(port);
        this.packetManager = packetManager;
        connections = new ArrayList<>();

        idsByConnection = new HashMap<>();
        connectionsById = new HashMap<>();

        nextId = 0;
    }

    private Connection getConnection(int id) {
        return connectionsById.get(id);
    }

    private int getId(Connection connection) {
        return idsByConnection.get(connection);
    }

    public void setServerListener(ServerListener listener) {
        this.listener = listener;
    }

    public void send(int id, Packet packet) {
        Connection connection = getConnection(id);

        try {
            packetManager.sendPacket(connection, packet);
        } catch (IOException e) {
            disconnect(connection);
        }
    }

    public void sendToAll(Packet packet) {
        List<Connection> disconnected = new ArrayList<>();
        for (Connection connection : connections) {
            try {
                packetManager.sendPacket(connection, packet);
            } catch (IOException e) {
                disconnected.add(connection);
            }
        }

        for (Connection connection : disconnected) {
            disconnect(connection);
        }
    }

    public void disconnect(int connection) {
        disconnect(getConnection(connection));
    }

    private void connected(Connection connection) {
        int id = getId(connection);

        if (listener != null) {
            listener.connected(id);
        }
    }

    private void disconnected(Connection connection) {
        int id = getId(connection);

        if (listener != null) {
            listener.disconnected(id);
        }
    }

    private void receive(Connection connection, Packet packet) {
        int id = getId(connection);

        if (listener != null) {
            listener.received(id, packet);
        }
    }

    private void disconnect(Connection connection) {
        if (!connections.contains(connection)) return;

        disconnected(connection);

        int id = getId(connection);
        connectionsById.remove(id);
        idsByConnection.remove(connection);
        connections.remove(connection);

        try {
            connection.close();
        } catch (IOException ignored) {
        }
    }

    public int accept() throws IOException {
        Socket socket = serverSocket.accept();
        Connection connection = new Connection(socket);
        connection.writeInt(nextId);

        connections.add(connection);
        connectionsById.put(nextId, connection);
        idsByConnection.put(connection, nextId);
        connected(connection);

        Thread receiver = new Thread(
                () -> {
                    while (true) {
                        try {
                            byte header = connection.readByte();
                            Packet packet = packetManager.getPacket(header);

                            // Invalid packet type or error creating packet
                            if (packet == null) {
                                disconnect(connection);
                                break;
                            }

                            packet.receive(connection);
                            receive(connection, packet);
                        } catch (IOException e) {
                            disconnect(connection);
                            break;
                        }
                    }
                }
        );

        receiver.setName("Connection Handler");
        receiver.setDaemon(true);
        receiver.start();

        return nextId++;
    }

    public void close() throws IOException {
        for (Connection connection : connections) {
            connection.close();
        }

        serverSocket.close();
    }
}