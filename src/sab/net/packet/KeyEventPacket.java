package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class KeyEventPacket implements Packet {
    public byte key;
    public boolean state;

    public KeyEventPacket(byte key, boolean state) {
        this.key = key;
        this.state = state;
    }

    public KeyEventPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(key);
        connection.writeBoolean(state);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        key = connection.readByte();
        state = connection.readBoolean();
    }
}
