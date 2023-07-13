package sab.net.packet;

import sab.game.stage.BattleConfig;
import sab.net.Connection;

import java.io.IOException;

public class BattleConfigPacket implements Packet {
    public long seed;
    public BattleConfig config;

    public BattleConfigPacket(long seed, BattleConfig config) {
        this.seed = seed;
        this.config = config;
    }

    public BattleConfigPacket() {
        config = new BattleConfig();
    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeLong(seed);

        connection.writeInt(config.gameMode.ordinal());

        connection.writeInt(config.player1Index);
        connection.writeInt(config.player1Costume);

        connection.writeInt(config.player2Index);
        connection.writeInt(config.player2Costume);

        connection.writeInt(config.stageIndex);

        connection.writeInt(config.lives);

        connection.writeBoolean(config.spawnAssBalls);
        connection.writeBoolean(config.stageHazards);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        seed = connection.readLong();

        config.gameMode = BattleConfig.GameMode.values()[connection.readInt()];

        config.player1Index = connection.readInt();
        config.player1Costume = connection.readInt();

        config.player2Index = connection.readInt();
        config.player2Costume = connection.readInt();

        config.stageIndex = connection.readInt();

        config.lives = connection.readInt();

        config.spawnAssBalls = connection.readBoolean();
        config.stageHazards = connection.readBoolean();
    }
}
