package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class StageSelectPacket implements Packet {
    public int stage;

    public StageSelectPacket() {
    }

    public StageSelectPacket(int stage) {
        this.stage = stage;
    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeInt(stage);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        stage = connection.readInt();
    }
}
