package sab.game.screens;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.Player;
import sab.net.Connection;
import sab.net.Keys;
import sab.net.Packets;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class JoinGameScreen extends ScreenAdapter {
    private Battle battle;
    private Connection connection;

    public JoinGameScreen() {
        connection = new Connection("localhost", 25565);
        battle = new Battle();

        new Thread(() -> {
            while (true) {
                byte header = connection.readByte();

                if (header == Packets.KICK) {
                    String message = connection.readUTF();
                    System.out.println(message);
                }

                if (header == Packets.PLAYER_STATE) {
                    Player player = battle.getPlayer(connection.readByte());
                    player.hitbox.setPosition(Packets.readVector(connection));
                    player.velocity.set(Packets.readVector(connection));
                    player.frame = connection.readByte();
                }
            }
        }).start();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W) {
            Packets.sendKeyPress(connection, Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            Packets.sendKeyPress(connection, Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            Packets.sendKeyPress(connection, Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            Packets.sendKeyPress(connection, Keys.RIGHT);
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.W) {
            Packets.sendKeyRelease(connection, Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            Packets.sendKeyRelease(connection, Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            Packets.sendKeyRelease(connection, Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            Packets.sendKeyRelease(connection, Keys.RIGHT);
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }

    @Override
    public Screen update() {
        if (System.currentTimeMillis() % 500 == 0) battle.update();
        return this;
    }

    @Override
    public void close() {
        connection.close();
    }
}