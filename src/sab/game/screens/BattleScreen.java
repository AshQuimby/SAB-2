package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.Hittable;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.DamageSource;
import sab.game.stages.LastLocation;
import sab.game.stages.Platform;
import sab.game.stages.StageObjectBehaviour;
import sab.game.stages.Stage;
import sab.net.Connection;
import sab.net.Keys;
import sab.net.Packets;
import sab.net.Server;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class BattleScreen extends ScreenAdapter {
    private Player player1;
    private Player player2;
    private Platform platform = null;

    private List<GameObject> gameObjects;
    private List<Player> players;
    private List<Hittable> hittables;
    private List<DamageSource> damageSources;

    private Server server;
    private Connection client;
    private Connection toClient;

    public BattleScreen() {
        // player1 = new Player(new Marvin(), this);
        // player2 = new Player(new Chain(), this);

        platform = new Platform(-320, -128, 640, 64, "last_location.png", new Stage(new LastLocation()));
        platform.addBehavior(new StageObjectBehaviour() {
            public void update(Platform platform) {
                // ownerPlatform.velocity.x = (float) Math.sin(Game.game.window.getTick() / 16f) * 2;
                // ownerPlatform.velocity.y = (float) Math.sin(Game.game.window.getTick() / 8f) * 4;
            }
        });

        SABSounds.playMusic("last_location.mp3", true);

        gameObjects = new ArrayList<>();
        players = new ArrayList<>();
        hittables = new ArrayList<>();
        damageSources = new ArrayList<>();

        addGameObject(player1);
        addGameObject(player2);

        server = new Server(25565);
        client = new Connection("localhost", 25565);
        toClient = server.accept();

        new Thread(() -> {
            while (true) {
                byte header = toClient.readByte();

                if (header == Packets.KEY_PRESS) {
                    byte key = toClient.readByte();

                    switch(key) {
                        case Keys.UP -> player1.keys.press(Keys.UP);
                        case Keys.DOWN -> player1.keys.press(Keys.DOWN);
                        case Keys.LEFT -> player1.keys.press(Keys.LEFT);
                        case Keys.RIGHT -> player1.keys.press(Keys.RIGHT);
                    }
                }

                if (header == Packets.KEY_RELEASE) {
                    byte key = toClient.readByte();

                    switch (key) {
                        case Keys.UP -> player1.keys.release(Keys.UP);
                        case Keys.DOWN -> player1.keys.release(Keys.DOWN);
                        case Keys.LEFT -> player1.keys.release(Keys.LEFT);
                        case Keys.RIGHT -> player1.keys.release(Keys.RIGHT);
                    }
                }
            }
        }).start();

        // Connection c2 = server.accept();

        // new Thread(() -> {
        //     while (true) {
        //         byte header = c2.readByte();

        //         if (header == Packets.KEY_PRESS) {
        //             byte key = c2.readByte();

        //             switch (key) {
        //                 case Keys.UP -> player1.keys.press(Keys.UP);
        //                 case Keys.DOWN -> player1.keys.press(Keys.DOWN);
        //                 case Keys.LEFT -> player1.keys.press(Keys.LEFT);
        //                 case Keys.RIGHT -> player1.keys.press(Keys.RIGHT);
        //             }
        //         }

        //         if (header == Packets.KEY_RELEASE) {
        //             byte key = c2.readByte();

        //             switch (key) {
        //                 case Keys.UP -> player1.keys.release(Keys.UP);
        //                 case Keys.DOWN -> player1.keys.release(Keys.DOWN);
        //                 case Keys.LEFT -> player1.keys.release(Keys.LEFT);
        //                 case Keys.RIGHT -> player1.keys.release(Keys.RIGHT);
        //             }
        //         }
        //     }
        // }).start();
    }

    private void addGameObject(GameObject gameObject) {
        gameObjects.add(gameObject);

        if (gameObject instanceof DamageSource) {
            damageSources.add((DamageSource) gameObject);
        }
        if (gameObject instanceof Hittable) {
            hittables.add((Hittable) gameObject);
        }
        if (gameObject instanceof Player) {
            players.add((Player) gameObject);
        }
    }

    

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W) {
            Packets.sendKeyPress(client, Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            Packets.sendKeyPress(client, Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            Packets.sendKeyPress(client, Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            Packets.sendKeyPress(client, Keys.RIGHT);
        }

        if (keyCode == Input.Keys.UP) {
            player2.keys.press(Keys.UP);
        }
        if (keyCode == Input.Keys.LEFT) {
            player2.keys.press(Keys.LEFT);
        }
        if (keyCode == Input.Keys.DOWN) {
            player2.keys.press(Keys.DOWN);
        }
        if (keyCode == Input.Keys.RIGHT) {
            player2.keys.press(Keys.RIGHT);
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.W) {
            player1.keys.release(Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            player1.keys.release(Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            player1.keys.release(Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            player1.keys.release(Keys.RIGHT);
        }

        if (keyCode == Input.Keys.UP) {
            player2.keys.release(Keys.UP);
        }
        if (keyCode == Input.Keys.LEFT) {
            player2.keys.release(Keys.LEFT);
        }
        if (keyCode == Input.Keys.DOWN) {
            player2.keys.release(Keys.DOWN);
        }
        if (keyCode == Input.Keys.RIGHT) {
            player2.keys.release(Keys.RIGHT);
        }

        return this;
    }

    @Override
    public Screen update() {
        for (GameObject gameObject : gameObjects) {
            gameObject.preUpdate();
        }

        platform.preUpdate();

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("background.png"), -1152 / 2, -704 / 2, 1152, 704);

        platform.render(g);

        player1.render(g);
        player2.render(g);
    }

    @Override
    public void close() {
        server.close();
        toClient.close();
        client.close();
    }
}
