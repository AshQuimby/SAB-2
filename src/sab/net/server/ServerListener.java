package sab.net.server;

import sab.net.packet.Packet;

public interface ServerListener {
    void connected(int connection);
    void disconnected(int connection);
    void received(int connection, Packet packet);
}
