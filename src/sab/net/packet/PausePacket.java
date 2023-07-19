package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class PausePacket implements Packet {
    public boolean paused;

    public PausePacket(boolean paused) {
        this.paused = paused;
    }

    public PausePacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeBoolean(paused);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        paused = connection.readBoolean();
    }
}
