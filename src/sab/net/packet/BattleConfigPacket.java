package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;

public class BattleConfigPacket implements Packet {
    public static final byte DAMAGE = 0;
    public static final byte HEALTH = 1;

    public byte gameMode;
    public long seed;
    public int lives;
    public boolean spawnAssBalls;
    public boolean stageHazards;

    public BattleConfigPacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(gameMode);
        connection.writeLong(seed);
        connection.writeInt(lives);
        connection.writeBoolean(spawnAssBalls);
        connection.writeBoolean(stageHazards);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        gameMode = connection.readByte();
        seed = connection.readLong();
        lives = connection.readInt();
        spawnAssBalls = connection.readBoolean();
        stageHazards = connection.readBoolean();
    }
}
