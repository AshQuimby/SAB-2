package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.sab_format.*;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Game;
import sab.game.SabSounds;
import sab.game.settings.Settings;
import sab.game.screen.battle_adjacent.CharacterSelectScreen;
import sab.game.screen.error.ErrorScreen;
import sab.net.JoinGameException;
import sab.net.client.Client;
import sab.net.packet.*;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JoinGameScreen extends ScreenAdapter {
    private static final int TIMEOUT_THRESHOLD = 3000;

    private String hostIp;
    private String hostPort;
    private boolean joining;
    private int selection;

    private volatile Client client;
    private volatile SabError error;
    private long timestamp;

    private int cursorFlashTimer;

    public JoinGameScreen() {
        Path path = Paths.get("../saves/servers.sab");
        try {
            Files.createDirectories(path.getParent());
        } catch (IOException ignored) {
            // Directory already exists
        }

        File file = path.toFile();
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        file = new File("../saves/servers.sab");
        SabData servers;
        try {
            servers = SabReader.read(file);
        } catch (SabParsingException e) {
            return;
        }

        SabValue mostRecent = servers.getValue("most_recent");
        if (mostRecent != null) {
            hostIp = mostRecent.getRawValue();
            hostPort = servers.getValue(mostRecent.getRawValue()).getRawValue();
        } else {
            hostIp = "localhost";
            hostPort = Integer.toString(Settings.localSettings.hostingPort);
        }
    }

    private void join() {
        File serversFile = new File("../saves/servers.sab");
        SabData servers;
        try {
            servers = SabReader.read(serversFile);
        } catch (SabParsingException e) {
            return;
        }
        servers.insertValue("most_recent", new SabValue(hostIp));
        servers.insertValue(hostIp, new SabValue(hostPort));
        try {
            SabWriter.write(serversFile, servers);
        } catch (IOException ignored) {
        }

        Thread connect = new Thread(
                () -> {
                    try {
                        client = new Client(hostIp, Integer.parseInt(hostPort), new SabPacketManager());
                    } catch (IOException ignored) {
                        error = new SabError("Connection Failed", "Failed to connect");
                    } catch (JoinGameException e) {
                        error = e.error;
                    }
                }
        );

        connect.setName("Client Connection");
        connect.setDaemon(true);
        connect.start();

        timestamp = System.currentTimeMillis();
        joining = true;
    }

    @Override
    public Screen keyPressed(int keyCode) {
        cursorFlashTimer = -60;
        if (keyCode == Input.Keys.DOWN) {
            cursorFlashTimer = 0;
            selection = Utils.loop(selection, 1, 2, 0);
            SabSounds.playSound(SabSounds.BLIP);
        } else if (keyCode == Input.Keys.UP) {
            cursorFlashTimer = 0;
            selection = Utils.loop(selection, -1, 2, 0);
            SabSounds.playSound(SabSounds.BLIP);
        } else if (keyCode == Input.Keys.BACKSPACE) {
            if (selection == 0 && hostIp.length() > 0) hostIp = hostIp.substring(0, hostIp.length() - 1);
            if (selection == 1 && hostPort.length() > 0) hostPort = hostPort.substring(0, hostPort.length() - 1);
            if (selection == 0 || selection == 1) SabSounds.playSound(SabSounds.BLIP);
        } else if (Input.Keys.toString(keyCode).length() == 1) {
            char key = Input.Keys.toString(keyCode).toCharArray()[0];
            if (selection == 0) {
                hostIp += Character.toLowerCase(key);
                SabSounds.playSound(SabSounds.BLIP);
            } else if (selection == 1 && hostPort.length() < 5) {
                if (Character.isDigit(key)) {
                    hostPort += key;
                    SabSounds.playSound(SabSounds.BLIP);
                }
            }
        } else if (keyCode == Input.Keys.ESCAPE) {
            if (joining) {
                if (client != null) {
                    try {
                        client.close();
                    } catch (IOException ignored) {

                    }
                }
                joining = false;
            } else {
                return new TitleScreen(false);
            }
        } else if (keyCode == Input.Keys.ENTER && !joining) {
            SabSounds.playSound(SabSounds.SELECT);
            join();
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        if (joining) {
            g.drawText("Connecting to server...", Game.getDefaultFont(), 0, 0, Game.getDefaultFontScale(), Color.WHITE, 0);
            return;
        }

        boolean cursorVisible = cursorFlashTimer >= 0 && (cursorFlashTimer / 30) % 2 == 0;
        g.drawText("Server Address: " + hostIp + (cursorVisible && selection == 0 ? "_" : ""), Game.getDefaultFont(), -200, 0, Game.getDefaultFontScale(), Color.WHITE, -1);
        g.drawText("Port: " + hostPort + (cursorVisible && selection == 1 ? "_" : ""), Game.getDefaultFont(), -200, -56, Game.getDefaultFontScale(), Color.WHITE, -1);
    }

    @Override
    public Screen update() {
        cursorFlashTimer++;

        if (error != null) {
            if (client != null) {
                try {
                    client.close();
                } catch (IOException ignored) {
                }
            }

            return new ErrorScreen(error);
        }

        if (joining) {
            if (System.currentTimeMillis() - timestamp < TIMEOUT_THRESHOLD) {
                if (client != null) {
                    return new CharacterSelectScreen(client);
                }
            } else {
                error = new SabError("Connection Failed", "Timed out");
            }
        }

        return this;
    }

    @Override
    public void close() {
        if (client != null) {
            try {
                client.close();
            } catch (IOException ignored) {
            }
        }
    }
}