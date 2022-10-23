package sab.net.client;

import sab.net.packet.Packet;

public interface ClientListener {
    void received(Packet packet);
    void disconnected();
}
