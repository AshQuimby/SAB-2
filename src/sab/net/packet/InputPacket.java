package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class InputPacket implements Packet {
    public boolean[] pressed;
    public boolean[] released;

    public InputPacket() {
        pressed = new boolean[6];
        released = new boolean[6];
    }

    public void press(int i) {
        pressed[i] = true;
    }

    public void release(int i) {
        released[i] = true;
    }

    @Override
    public void send(Connection connection) throws IOException {
        for (int i = 0; i < 6; i++) {
            connection.writeBoolean(pressed[i]);
        }
        for (int i = 0; i < 6; i++) {
            connection.writeBoolean(released[i]);
        }
    }

    @Override
    public void receive(Connection connection) throws IOException {
        for (int i = 0; i < 6; i++) {
            pressed[i] = connection.readBoolean();
        }
        for (int i = 0; i < 6; i++) {
            released[i] = connection.readBoolean();
        }
    }
}
