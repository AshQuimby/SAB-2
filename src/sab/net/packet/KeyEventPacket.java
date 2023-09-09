package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class KeyEventPacket implements Packet {
    public byte key;
    public boolean state;
    // Whether this event is an acknowledgement giving the client the go ahead to press/release the key
    public boolean acknowledgement;

    public KeyEventPacket(byte key, boolean state) {
        this.key = key;
        this.state = state;
        this.acknowledgement = false;
    }

    public KeyEventPacket(byte key, boolean state, boolean acknowledgement) {
        this.key = key;
        this.state = state;
        this.acknowledgement = acknowledgement;
    }

    public KeyEventPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(key);
        connection.writeBoolean(state);
        connection.writeBoolean(acknowledgement);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        key = connection.readByte();
        state = connection.readBoolean();
        acknowledgement = connection.readBoolean();
    }
}
