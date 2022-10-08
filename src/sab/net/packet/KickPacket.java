package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class KickPacket implements Packet {
    public String message;

    public KickPacket(String message) {
        this.message = message;
    }

    public KickPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeUTF(message);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        message = connection.readUTF();
    }
}
