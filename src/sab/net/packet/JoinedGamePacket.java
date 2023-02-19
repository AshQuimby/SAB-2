package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class JoinedGamePacket implements Packet {
    public byte id;

    public JoinedGamePacket(byte id) {
        this.id = id;
    }

    public JoinedGamePacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(id);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        id = connection.readByte();
    }
}
