package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public interface Packet {
    void send(Connection connection) throws IOException;
    void receive(Connection connection) throws IOException;
}
