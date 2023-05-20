package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Game;
import sab.game.Settings;
import sab.game.screen.error.ErrorScreen;
import sab.net.client.Client;
import sab.net.packet.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

import java.io.IOException;

public class JoinGameScreen extends ScreenAdapter {
    private volatile Client client;
    private volatile SabError error;
    private final long timestamp;

    private static final int TIMEOUT_THRESHOLD = 3000;

    public JoinGameScreen() {
        Thread connect = new Thread(
                () -> {
                    try {
                        // TODO: User specified host address
                        client = new Client("localhost", Settings.getHostingPort(), new SabPacketManager());
                    } catch (IOException ignored) {
                        error = new SabError("Connection Failed", "Failed to connect");
                    }
                }
        );

        connect.setName("Client Connection");
        connect.setDaemon(true);
        connect.start();

        timestamp = System.currentTimeMillis();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            try {
                client.close();
            } catch (IOException ignored) {

            }

            return new TitleScreen(false);
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        g.drawText("Connecting to server...", Game.getDefaultFont(), 0, 0, Game.getDefaultFontScale(), Color.WHITE, 0);
    }

    @Override
    public Screen update() {
        if (error != null) {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ignored) {
                }
            }

            return new ErrorScreen(error);
        }

        if (System.currentTimeMillis() - timestamp < TIMEOUT_THRESHOLD) {
            if (client != null) {
                return new CharacterSelectScreen(client);
            }
        } else {
            error = new SabError("Connection Failed", "Timed out");
        }

        return this;
    }

    @Override
    public void close() {
        try {
            client.close();
        } catch (IOException ignored) {
        }
    }
}