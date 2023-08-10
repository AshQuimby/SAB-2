package sab.net.packet;

import sab.game.Player;
import sab.net.Connection;

import java.io.IOException;

public class UpdatePacket implements Packet {
    public boolean[] player1Pressed;
    public boolean[] player1Released;

    public boolean[] player2Pressed;
    public boolean[] player2Released;

    public UpdatePacket(Player player1, Player player2) {
        this();

        for (int i = 0; i < 6; i++) {
            if (player1.keys.isJustPressed(i)) player1Pressed[i] = true;
            if (player1.keys.isJustReleased(i)) player1Released[i] = true;

            if (player2.keys.isJustPressed(i)) player2Pressed[i] = true;
            if (player2.keys.isJustReleased(i)) player2Released[i] = true;
        }
    }

    public UpdatePacket() {
        player1Pressed = new boolean[6];
        player1Released = new boolean[6];

        player2Pressed = new boolean[6];
        player2Released = new boolean[6];
    }

    @Override
    public void send(Connection connection) throws IOException {
        for (boolean b : player1Pressed) {
            connection.writeBoolean(b);
        }
        for (boolean b : player1Released) {
            connection.writeBoolean(b);
        }
        for (boolean b : player2Pressed) {
            connection.writeBoolean(b);
        }
        for (boolean b : player2Released) {
            connection.writeBoolean(b);
        }
    }

    @Override
    public void receive(Connection connection) throws IOException {
        for (int i = 0; i < player1Pressed.length; i++) {
            player1Pressed[i] = connection.readBoolean();
        }
        for (int i = 0; i < player1Released.length; i++) {
            player1Released[i] = connection.readBoolean();
        }
        for (int i = 0; i < player2Pressed.length; i++) {
            player2Pressed[i] = connection.readBoolean();
        }
        for (int i = 0; i < player2Released.length; i++) {
            player2Released[i] = connection.readBoolean();
        }
    }
}
