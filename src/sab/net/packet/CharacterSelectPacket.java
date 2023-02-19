package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class CharacterSelectPacket implements Packet {
    public int character;
    public int costume;

    public CharacterSelectPacket(int character, int costume) {
        this.character = character;
        this.costume = costume;
    }

    public CharacterSelectPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeInt(character);
        connection.writeInt(costume);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        character = connection.readInt();
        costume = connection.readInt();
    }
}
