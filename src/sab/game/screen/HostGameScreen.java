package sab.game.screen;

import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;
import sab.error.SabError;
import sab.game.Game;
import sab.net.packet.Packet;
import sab.net.packet.SabPacketManager;
import sab.net.server.Server;
import sab.net.server.ServerListener;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

import java.io.IOException;

public class HostGameScreen extends ScreenAdapter {
    private Server server;
    private boolean serverStarted;
    private volatile boolean hasRemoteConnection;
    private volatile boolean errorAcceptingConnection;
    private int remoteClient;

    public HostGameScreen() {
        try {
            server = new Server(new SabPacketManager(), 25565);
            serverStarted = true;
        } catch (IOException ignored) {
            return;
        }

        Thread connectionReceiver = new Thread(
                () -> {
                    try {
                        remoteClient = server.accept();
                        hasRemoteConnection = true;
                    } catch (IOException e) {
                        errorAcceptingConnection = true;
                    }
                }
        );

        connectionReceiver.setName("Connection Receiver");
        connectionReceiver.setDaemon(true);
        connectionReceiver.start();
    }
    
    @Override
    public Screen update() {
        // Failed to create server
        if (server == null) {
            return new ErrorScreen(new SabError("Failed to start server", "The server could not be started"));
        }

        if (!serverStarted) {
            try {
                server.close();
            } catch (IOException ignored) {
            }

            return new ErrorScreen(new SabError("Failed to start server", "The server could not be started"));
        }

        if (errorAcceptingConnection) {
            try {
                server.close();
            } catch (IOException ignored) {
            }

            return new ErrorScreen(new SabError("Server Error", "Error accepting connection"));
        }

        if (hasRemoteConnection) {
            return new HostedCharacterSelectScreen(server, remoteClient);
            //return new HostedBattleScreen(server, remoteClient);
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        g.drawText("Waiting for players...", g.imageProvider.getFont("SAB_font"), 0, 0, 1, Color.WHITE, 0);
    }

    @Override
    public void close() {
        try {
            server.close();
        } catch (IOException ignored) {
        }
    }
}