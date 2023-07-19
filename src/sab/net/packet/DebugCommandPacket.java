package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class DebugCommandPacket implements Packet {
    public static final byte GRANT = 0;
    public static final byte SPAWN = 1;

    public byte command;

    public DebugCommandPacket(byte command) {
        this.command = command;
    }

    public DebugCommandPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(command);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        command = connection.readByte();
    }
}
