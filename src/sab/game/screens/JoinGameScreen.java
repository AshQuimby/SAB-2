package sab.game.screens;

import com.badlogic.gdx.Input;

import sab.net.Connection;
import sab.net.Keys;
import sab.net.Packets;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class JoinGameScreen extends ScreenAdapter {
    private Connection connection;

    public JoinGameScreen() {
        connection = new Connection("localhost", 25565);
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
    public void close() {
        connection.close();
    }
}