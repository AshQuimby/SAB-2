package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.sab_format.SabData;
import com.sab_format.SabParsingException;
import com.sab_format.SabReader;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.*;
import sab.game.fighter.Fighter;
import sab.game.screen.battle_adjacent.CharacterSelectScreen;
import sab.game.screen.battle_adjacent.VictoryScreen;
import sab.game.screen.error.ErrorScreen;
import sab.game.settings.Settings;
import sab.game.stage.BattleConfig;
import sab.game.stage.LastLocation;
import sab.game.stage.Stage;
import sab.net.Keys;
import sab.net.client.Client;
import sab.net.packet.*;
import sab.net.server.Server;
import sab.replay.Replay;
import sab.screen.*;
import sab.util.Utils;

import java.io.File;

public class BattleScreen extends NetScreen {
    private final static boolean LOG_NETWORK_EVENTS = false;

    public Battle battle;
    private Replay currentReplay;
    private Replay playingReplay;

    private int numInputs;

    private boolean disconnected;
    private volatile boolean initialized;

    // For replays
    public BattleScreen(File replay) throws SabParsingException {
        super();

        SabData data = SabReader.read(replay);

        long seed = data.getValue("seed").asInt(); // FIXME: SAB files need longs
        Fighter player1 = new Fighter(Game.game.fighterFromString(data.getValue("player1Fighter").getRawValue()));
        Fighter player2 = new Fighter(Game.game.fighterFromString(data.getValue("player2Fighter").getRawValue()));

        int[] costumes = new int[2];
        costumes[0] = data.getValue("player1Costume").asInt();
        costumes[1] = data.getValue("player2Costume").asInt();

        int lives = data.getValue("lives").asInt();
//        int player1Type = Integer.parseInt(SABReader.readProperty("player1AI", replay));
//        int player2Type = Integer.parseInt(SABReader.readProperty("player2AI", replay));
//        if (player2Type == 0) player2Type = -1;
//        if (player1Type == 0) player1Type = -1;
        int player1Type = -1;
        int player2Type = -1;

        boolean assBalls = data.getValue("assBalls").asBool();
        boolean stageHazards = data.getValue("stageHazards").asBool();
        battle = new Battle(seed,
                player1,
                costumes[0],
                player1Type,
                player2,
                costumes[1],
                player2Type,
                new Stage(new LastLocation()),
                BattleConfig.GameMode.DAMAGE,
                lives,
                assBalls,
                stageHazards);
        battle.start();
        playingReplay = new Replay();
        playingReplay.fromSabData(data);
        playingReplay.tickReplay(battle, 0);

        initialized = true;
    }

    public BattleScreen(long seed, BattleConfig config) {
        super();
        battle = new Battle(seed, config);
        battle.start();
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), config.player1Type, config.player2Type, battle.getStage(), config.lives, config.spawnAssBalls, config.stageHazards);

        initialized = true;
    }

    public BattleScreen(Server server, long seed, BattleConfig config) {
        super(server);
        battle = new Battle(seed, config);
        battle.start();
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), 0, 0, battle.getStage(), config.lives, config.spawnAssBalls, config.stageHazards);
    }

    public BattleScreen(Client client, long seed, BattleConfig config) {
        super(client);
        battle = new Battle(seed, config);
        battle.start();
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), 0, 0, battle.getStage(), config.lives, config.spawnAssBalls, config.stageHazards);

        initialized = true;
        client.send(new ReadyPacket());
    }

    @Override
    protected void receive(Packet p) {
        // Server is sus, disconnect
        if (!initialized) {
            disconnected = true;
            return;
        }

        if (p instanceof UpdatePacket) {
            battle.update();
            battle.postUpdate();
        } else if (p instanceof KeyEventPacket kep) {
            if (kep.acknowledgement) {
                if (kep.state) {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Host acknowledged key %s pressed on tick %s\n", kep.key, battle.getBattleTick());
                    battle.getPlayer(1).keys.press(kep.key);
                } else {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Host acknowledged key %s released on tick %s\n", kep.key, battle.getBattleTick());
                    battle.getPlayer(1).keys.release(kep.key);
                }
            } else {
                if (kep.state) {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Host pressed %s on tick %s\n", kep.key, battle.getBattleTick());
                    battle.getPlayer(0).keys.press(kep.key);
                } else {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Host released %s on tick %s\n", kep.key, battle.getBattleTick());
                    battle.getPlayer(0).keys.release(kep.key);
                }
            }
        } else if (p instanceof PausePacket pp) {
            if (LOG_NETWORK_EVENTS) System.out.printf("%s on tick %s\n", pp.paused ? "Pause" : "Unpause", battle.getBattleTick());
            if (pp.paused) battle.pause();
            else battle.unpause();
        } else if (p instanceof DebugCommandPacket dcp) {
            if (LOG_NETWORK_EVENTS) System.out.printf("Debug command %s on tick %s\n", dcp.command, battle.getBattleTick());
            if (dcp.command == DebugCommandPacket.SPAWN) {
                battle.spawnAssBall();
            } else if (dcp.command == DebugCommandPacket.GRANT) {
                for (Player player : battle.getPlayers()) {
                    player.grantFinalAss();
                }
            }
        } else if (p instanceof EndGamePacket) {
            if (LOG_NETWORK_EVENTS) System.out.printf("Game ended on tick %s\n", battle.getBattleTick());
            battle.pause();
            battle.pauseMenuIndex = 2;
            battle.onPressEnter();
        }
    }

    @Override
    protected void receive(int connection, Packet p) {
        if (p instanceof ReadyPacket) {
            initialized = true;
            if (LOG_NETWORK_EVENTS) System.out.println("Ready");
        }
        if (!initialized) return;

        if (p instanceof KeyEventPacket kep) {
            if (kep.state) {
                if (LOG_NETWORK_EVENTS) System.out.printf("Client pressed %s on tick %s\n", kep.key, battle.getBattleTick());
                battle.getPlayer(1).keys.press(kep.key);
                server.send(0, new KeyEventPacket(kep.key, kep.state, true));
            } else {
                if (LOG_NETWORK_EVENTS) System.out.printf("Client released %s on tick %s\n", kep.key, battle.getBattleTick());
                battle.getPlayer(1).keys.release(kep.key);
                server.send(0, new KeyEventPacket(kep.key, kep.state, true));
            }
        }
    }

    @Override
    protected void disconnected() {
        disconnected = true;
    }

    @Override
    protected void disconnected(int connection) {
        disconnected = true;
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (host) {
            if (keyCode == Input.Keys.LEFT) keyCode = Input.Keys.A;
            else if (keyCode == Input.Keys.RIGHT) keyCode = Input.Keys.D;
            else if (keyCode == Input.Keys.DOWN) keyCode = Input.Keys.S;
            else if (keyCode == Input.Keys.UP) keyCode = Input.Keys.W;
            else if (keyCode == Input.Keys.M) keyCode = Input.Keys.F;
            else if (keyCode == Input.Keys.N) keyCode = Input.Keys.T;
        } else if (!local) {
            if (keyCode == Input.Keys.A) keyCode = Input.Keys.LEFT;
            else if (keyCode == Input.Keys.D) keyCode = Input.Keys.RIGHT;
            else if (keyCode == Input.Keys.S) keyCode = Input.Keys.DOWN;
            else if (keyCode == Input.Keys.W) keyCode = Input.Keys.UP;
            else if (keyCode == Input.Keys.F) keyCode = Input.Keys.M;
            else if (keyCode == Input.Keys.T) keyCode = Input.Keys.N;
        }

        if (Settings.localSettings.debugMode.value) {
            if (keyCode == Input.Keys.V && (local || host)) {
                // SPAWN MASSIVE BALLS
                battle.spawnAssBall();
                if (host) {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Debug command %s on tick %s\n", DebugCommandPacket.SPAWN, battle.getBattleTick());
                    server.send(0, new DebugCommandPacket(DebugCommandPacket.SPAWN));
                }
            } else if (keyCode == Input.Keys.H) {
                battle.drawHitboxes = !battle.drawHitboxes;
            } else if (keyCode == Input.Keys.P) {
                battle.drawPathfindingGraph = !battle.drawPathfindingGraph;
            } else if (keyCode == Input.Keys.SPACE && battle.isPaused() && (local || host)) {
                if (host) server.send(0, new PausePacket(false));
                battle.unpause();
                if (host) server.send(0, new UpdatePacket());
                battle.update();
                battle.postUpdate();
                battle.pause();
                if (host) server.send(0, new PausePacket(true));
            } else if (keyCode == Input.Keys.B && (host || local)) {
                for (Player player : battle.getPlayers()) {
                    player.grantFinalAss();
                }
                if (host) {
                    if (LOG_NETWORK_EVENTS) System.out.printf("Debug command %s on tick %s\n", DebugCommandPacket.GRANT, battle.getBattleTick());
                    server.send(0, new DebugCommandPacket(DebugCommandPacket.GRANT));
                }
            }
        }

        if (!battle.gameOver() && (local || host)) {
            if (battle.isPaused()) {
                if (keyCode == Input.Keys.W || keyCode == Input.Keys.UP) {
                    battle.pauseMenuIndex = Utils.loop(battle.pauseMenuIndex, -1, 3, 0);
                } else if (keyCode == Input.Keys.S || keyCode == Input.Keys.DOWN) {
                    battle.pauseMenuIndex = Utils.loop(battle.pauseMenuIndex, 1, 3, 0);
                }
            } else {
                if (battle.getPlayer(0).isHuman()) {
                    if (keyCode == Input.Keys.W) {
                        battle.getPlayer(0).keys.press(Keys.UP);
                    } else if (keyCode == Input.Keys.A) {
                        battle.getPlayer(0).keys.press(Keys.LEFT);
                    } else if (keyCode == Input.Keys.S) {
                        battle.getPlayer(0).keys.press(Keys.DOWN);
                    } else if (keyCode == Input.Keys.D) {
                        battle.getPlayer(0).keys.press(Keys.RIGHT);
                    } else if (keyCode == Input.Keys.F) {
                        battle.getPlayer(0).keys.press(Keys.ATTACK);
                    } else if (keyCode == Input.Keys.T) {
                        battle.getPlayer(0).keys.press(Keys.PARRY);
                    }
                }

                if (battle.getPlayer(1).isHuman()) {
                    if (keyCode == Input.Keys.UP) {
                        battle.getPlayer(1).keys.press(Keys.UP);
                    } else if (keyCode == Input.Keys.LEFT) {
                        battle.getPlayer(1).keys.press(Keys.LEFT);
                    } else if (keyCode == Input.Keys.DOWN) {
                        battle.getPlayer(1).keys.press(Keys.DOWN);
                    } else if (keyCode == Input.Keys.RIGHT) {
                        battle.getPlayer(1).keys.press(Keys.RIGHT);
                    } else if (keyCode == Input.Keys.M) {
                        battle.getPlayer(1).keys.press(Keys.ATTACK);
                    } else if (keyCode == Input.Keys.N) {
                        battle.getPlayer(1).keys.press(Keys.PARRY);
                    }
                }
            }

            if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.SHIFT_RIGHT) {
                battle.togglePause();
                if (host) {
                    if (LOG_NETWORK_EVENTS) System.out.printf("%s on tick %s\n", battle.isPaused() ? "Pause" : "Unpause", battle.getBattleTick());
                    server.send(0, new PausePacket(battle.isPaused()));
                }
            } else if (keyCode == Input.Keys.ENTER) {
                battle.onPressEnter();
                if (host) {
                    server.send(0, new PausePacket(battle.isPaused()));
                    if (battle.gameOver()) {
                        if (LOG_NETWORK_EVENTS) System.out.printf("Game ended on tick %s\n", battle.getBattleTick());
                        server.send(0, new EndGamePacket());
                    }
                }
            } else if (currentReplay != null) {
                numInputs++;
                currentReplay.keyStateChanged(keyCode, true);
            }
        }

        if (host) {
            switch (keyCode) {
                case Input.Keys.W -> server.send(0, new KeyEventPacket(Keys.UP, true));
                case Input.Keys.S -> server.send(0, new KeyEventPacket(Keys.DOWN, true));
                case Input.Keys.A -> server.send(0, new KeyEventPacket(Keys.LEFT, true));
                case Input.Keys.D -> server.send(0, new KeyEventPacket(Keys.RIGHT, true));
                case Input.Keys.F -> server.send(0, new KeyEventPacket(Keys.ATTACK, true));
                case Input.Keys.T -> server.send(0, new KeyEventPacket(Keys.PARRY, true));
            }
        } else if (!local) {
            switch (keyCode) {
                case Input.Keys.UP -> client.send(new KeyEventPacket(Keys.UP, true));
                case Input.Keys.DOWN -> client.send(new KeyEventPacket(Keys.DOWN, true));
                case Input.Keys.LEFT -> client.send(new KeyEventPacket(Keys.LEFT, true));
                case Input.Keys.RIGHT -> client.send(new KeyEventPacket(Keys.RIGHT, true));
                case Input.Keys.M -> client.send(new KeyEventPacket(Keys.ATTACK, true));
                case Input.Keys.N -> client.send(new KeyEventPacket(Keys.PARRY, true));
            }
        }

        // if (keyCode == Input.Keys.ENTER) if (battle.onSelect()) return Game.game.globalCharacterSelectScreen;

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (host) {
            if (keyCode == Input.Keys.LEFT) keyCode = Input.Keys.A;
            else if (keyCode == Input.Keys.RIGHT) keyCode = Input.Keys.D;
            else if (keyCode == Input.Keys.DOWN) keyCode = Input.Keys.S;
            else if (keyCode == Input.Keys.UP) keyCode = Input.Keys.W;
            else if (keyCode == Input.Keys.M) keyCode = Input.Keys.F;
            else if (keyCode == Input.Keys.N) keyCode = Input.Keys.T;
        } else if (!local) {
            if (keyCode == Input.Keys.A) keyCode = Input.Keys.LEFT;
            else if (keyCode == Input.Keys.D) keyCode = Input.Keys.RIGHT;
            else if (keyCode == Input.Keys.S) keyCode = Input.Keys.DOWN;
            else if (keyCode == Input.Keys.W) keyCode = Input.Keys.UP;
            else if (keyCode == Input.Keys.F) keyCode = Input.Keys.M;
            else if (keyCode == Input.Keys.T) keyCode = Input.Keys.N;
        }

        if (!battle.gameOver() && (local || host)) {
            if (keyCode == Input.Keys.W) {
                battle.getPlayer(0).keys.release(Keys.UP);
            } else if (keyCode == Input.Keys.A) {
                battle.getPlayer(0).keys.release(Keys.LEFT);
            } else if (keyCode == Input.Keys.S) {
                battle.getPlayer(0).keys.release(Keys.DOWN);
            } else if (keyCode == Input.Keys.D) {
                battle.getPlayer(0).keys.release(Keys.RIGHT);
            } else if (keyCode == Input.Keys.F) {
                battle.getPlayer(0).keys.release(Keys.ATTACK);
            } else if (keyCode == Input.Keys.T) {
                battle.getPlayer(0).keys.release(Keys.ATTACK);
            }

            if (keyCode == Input.Keys.UP) {
                battle.getPlayer(1).keys.release(Keys.UP);
            } else if (keyCode == Input.Keys.LEFT) {
                battle.getPlayer(1).keys.release(Keys.LEFT);
            } else if (keyCode == Input.Keys.DOWN) {
                battle.getPlayer(1).keys.release(Keys.DOWN);
            } else if (keyCode == Input.Keys.RIGHT) {
                battle.getPlayer(1).keys.release(Keys.RIGHT);
            } else if (keyCode == Input.Keys.M) {
                battle.getPlayer(1).keys.release(Keys.ATTACK);
            } else if (keyCode == Input.Keys.N) {
                battle.getPlayer(0).keys.release(Keys.ATTACK);
            }
        }

        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.SHIFT_RIGHT) {
            // Placeholder
        } else if (keyCode == Input.Keys.ENTER) {
            // Another placeholder
        } else if (currentReplay != null) {
            numInputs++;
            currentReplay.keyStateChanged(keyCode, false);
        }

        if (host) {
            switch (keyCode) {
                case Input.Keys.W -> server.send(0, new KeyEventPacket(Keys.UP, false));
                case Input.Keys.S -> server.send(0, new KeyEventPacket(Keys.DOWN, false));
                case Input.Keys.A -> server.send(0, new KeyEventPacket(Keys.LEFT, false));
                case Input.Keys.D -> server.send(0, new KeyEventPacket(Keys.RIGHT, false));
                case Input.Keys.F -> server.send(0, new KeyEventPacket(Keys.ATTACK, false));
                case Input.Keys.T -> server.send(0, new KeyEventPacket(Keys.PARRY, false));
            }
        } else if (!local) {
            switch (keyCode) {
                case Input.Keys.UP -> client.send(new KeyEventPacket(Keys.UP, false));
                case Input.Keys.DOWN -> client.send(new KeyEventPacket(Keys.DOWN, false));
                case Input.Keys.LEFT -> client.send(new KeyEventPacket(Keys.LEFT, false));
                case Input.Keys.RIGHT -> client.send(new KeyEventPacket(Keys.RIGHT, false));
                case Input.Keys.M -> client.send(new KeyEventPacket(Keys.ATTACK, false));
                case Input.Keys.N -> client.send(new KeyEventPacket(Keys.PARRY, false));
            }
        }

        return this;
    }

    @Override
    public Screen update() {
        if (!initialized) return this;

        if (disconnected) {
            if (host) {
                battle.endBattle();
                return new ErrorScreen(new SabError("Client Disconnected", "Player 2 disconnected"));
            } else {
                battle.endBattle();
                return new ErrorScreen(new SabError("Lost Connection", "Lost connection to the server"));
            }
        }

        if (battle.gameEnded) {
            Game.game.window.camera.zoom = 1;
            Game.game.window.camera.targetZoom = 1;

            // TODO: Add victory screen to multiplayer
            if (host) {
                Game.game.window.camera.reset();
                return new CharacterSelectScreen(server);
            } else if (!local) {
                Game.game.window.camera.reset();
                return new CharacterSelectScreen(client);
            }
            battle.endBattle();
            if (currentReplay != null && Settings.localSettings.debugMode.value) currentReplay.saveReplay();
            System.out.println("Battle inputs detected: " + numInputs);
            System.out.println("Replay inputs detected: " + Replay.inputsDetected);
            return new VictoryScreen(battle.winner, battle.loser, battle.getStage().background);
        }
        if (host || local) {
            if (host) {
                server.send(0, new UpdatePacket());
            }
            if (battle.update()) {
                if (currentReplay != null) currentReplay.update(battle.getBattleTick());
                else playingReplay.tickReplay(battle, battle.getBattleTick() + 1);
            }
            battle.postUpdate();
        }

        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }

    @Override
    public void debugRender(ShapeRenderer s) {
        battle.debugRender(s);
    }
}