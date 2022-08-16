package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.net.Connection;
import sab.net.Packets;
import sab.net.Server;
import sab.net.Keys;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class HostedBattleScreen extends ScreenAdapter {
    private static class ClientHandler implements Runnable {
        private Battle battle;
        private Connection client;
        private int id;

        public ClientHandler(Battle battle, Connection client, int id) {
            this.battle = battle;
            this.client = client;
            this.id = id;
        }

		@Override
		public void run() {
			while (true) {
                byte header = client.readByte();

                if (header == Packets.KEY_PRESS) {
                    byte key = client.readByte();

                    if (!Keys.isValidKey(key)) {
                        Packets.sendKick(client, "Invalid input");
                        break;
                    }

                    battle.getPlayer(id).keys.press(key);
                }

                if (header == Packets.KEY_RELEASE) {
                    byte key = client.readByte();

                    if (!Keys.isValidKey(key)) {
                        Packets.sendKick(client, "Invalid input");
                        break;
                    }

                    battle.getPlayer(id).keys.release(key);
                }
            }
		}


    }

    private Battle battle;
    private Server server;

    private Connection localClient;
    private List<Connection> connections;

    public HostedBattleScreen(int port) {
        server = new Server(port);
        battle = new Battle();
        connections = new ArrayList<>();

        localClient = new Connection("localhost", 25565);
        Connection client = server.accept();
        connections.add(client);
        new Thread(new ClientHandler(battle, client, 0)).start();

        new Thread(() -> {
            Connection connection = server.accept();
            System.out.println("New connection");
            connections.add(connection);
            new Thread(new ClientHandler(battle, connection, 1)).start();
        }).start();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W) {
            Packets.sendKeyPress(localClient, Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            Packets.sendKeyPress(localClient, Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            Packets.sendKeyPress(localClient, Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            Packets.sendKeyPress(localClient, Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F) {
            Packets.sendKeyPress(localClient, Keys.ATTACK);
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.W) {
            Packets.sendKeyRelease(localClient, Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            Packets.sendKeyRelease(localClient, Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            Packets.sendKeyRelease(localClient, Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            Packets.sendKeyRelease(localClient, Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F) {
            Packets.sendKeyRelease(localClient, Keys.ATTACK);
        }

        return this;
    }

    @Override
    public Screen update() {
        battle.update();
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }

    @Override
    public void close() {
        localClient.close();
        for (Connection client : connections) {
            client.close();
        }

        server.close();
    }
}