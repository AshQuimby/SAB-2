package sab.game.screen;

import com.badlogic.gdx.Input;
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
import sab.game.stage.LastLocation;
import sab.game.stage.Stage;
import sab.net.Keys;
import sab.net.client.Client;
import sab.net.packet.KeyEventPacket;
import sab.net.packet.Packet;
import sab.net.packet.PlayerStatePacket;
import sab.net.server.Server;
import sab.replay.Replay;
import sab.screen.*;
import sab.util.Utils;

import java.io.File;

public class BattleScreen extends NetScreen {
    private static final int NETWORK_TICK_MILLISECONDS = 50;
    public Battle battle;
    private Replay currentReplay;
    private Replay playingReplay;
    private boolean disconnected;
    private boolean ended;
    private long lastBroadcastTimestamp;

    private int numInputs;

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

        battle = new Battle(seed, player1, player2, costumes, new Stage(new LastLocation()), player1Type, player2Type, lives, assBalls, stageHazards);
        playingReplay = new Replay();
        playingReplay.fromSabData(data);
        playingReplay.tickReplay(battle, 0);
    }

    public BattleScreen(Fighter player1, Fighter player2, int[] costumes, Stage stage, int player1Type, int player2Type, int lives) {
        super();
        long seed = System.currentTimeMillis();
        battle = new Battle(seed, player1, player2, costumes, stage, player1Type, player2Type, lives, Settings.assBalls.value, Settings.stageHazards.value);
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), player1Type, player2Type, stage, lives, Settings.assBalls.value, Settings.stageHazards.value);
    }

    public BattleScreen(Server server, Fighter player1, Fighter player2, int[] costumes, Stage stage, int lives) {
        super(server);
        long seed = System.currentTimeMillis();
        battle = new Battle(seed, player1, player2, costumes, stage, 0, 0, lives, Settings.assBalls.value, Settings.stageHazards.value);
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), 0, 0, stage, lives, Settings.assBalls.value, Settings.stageHazards.value);
    }

    public BattleScreen(Client client, Fighter player1, Fighter player2, int[] costumes, Stage stage, int lives) {
        super(client);
        long seed = System.currentTimeMillis();
        battle = new Battle(seed, player1, player2, costumes, stage, 0, 0, lives, Settings.assBalls.value, Settings.stageHazards.value);
        SabSounds.playMusic(battle.getStage().music, true);
        currentReplay = new Replay(seed, battle.getPlayer(0), battle.getPlayer(1), 0, 0, stage, lives, Settings.assBalls.value, Settings.stageHazards.value);
    }

    @Override
    protected void receive(Packet p) {
        if (p instanceof KeyEventPacket kep) {
            if (kep.state) battle.getPlayer(0).keys.press(kep.key);
            else battle.getPlayer(0).keys.release(kep.key);
        } else if (p instanceof PlayerStatePacket psp && battle != null) {
            psp.syncPlayer(battle.getPlayer(psp.playerId));
        }
    }

    @Override
    protected void receive(int connection, Packet p) {
        if (p instanceof KeyEventPacket kep) {
            if (kep.state) battle.getPlayer(1).keys.press(kep.key);
            else battle.getPlayer(1).keys.release(kep.key);
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

        if (Settings.debugMode.value) {
            if (keyCode == Input.Keys.V) {
                // SPAWN MASSIVE BALLS
                battle.spawnAssBall();
            } else if (keyCode == Input.Keys.H) {
                battle.drawHitboxes = !battle.drawHitboxes;
            } else if (keyCode == Input.Keys.P) {
                battle.drawPathfindingGraph = !battle.drawPathfindingGraph;
            } else if (keyCode == Input.Keys.SPACE && battle.isPaused()) {
                battle.unpause();
                battle.update();
                battle.postUpdate();
                battle.pause();
            } else if (keyCode == Input.Keys.B) {
                for (Player player : battle.getPlayers()) {
                    player.grantFinalAss();
                }
            }
        }

        if (!battle.gameOver()) {
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
            } else if (keyCode == Input.Keys.ENTER) {
                battle.onPressEnter();
            } else if (currentReplay != null) {
                numInputs++;
                currentReplay.keyStateChanged(keyCode, true);
            }
        }

        if (host) {
            for (byte i = 0; i < 6; i++) {
                if (battle.getPlayer(0).keys.isJustPressed(i)) {
                    server.send(0, new KeyEventPacket(i, true));
                }
            }
        } else if (!local) {
            for (byte i = 0; i < 6; i++) {
                if (battle.getPlayer(1).keys.isJustPressed(i)) {
                    client.send(new KeyEventPacket(i, true));
                }
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

        if (!battle.gameOver()) {
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
            for (byte i = 0; i < 6; i++) {
                if (battle.getPlayer(0).keys.isJustReleased(i)) {
                    server.send(0, new KeyEventPacket(i, false));
                }
            }
        } else if (!local) {
            for (byte i = 0; i < 6; i++) {
                if (battle.getPlayer(1).keys.isJustReleased(i)) {
                    client.send(new KeyEventPacket(i, false));
                }
            }
        }

        return this;
    }

    @Override
    public Screen update() {
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
            if (currentReplay != null && Settings.debugMode.value) currentReplay.saveReplay();
            System.out.println("Battle inputs detected: " + numInputs);
            System.out.println("Replay inputs detected: " + Replay.inputsDetected);
            return new VictoryScreen(battle.winner, battle.loser, battle.getStage().background);
        }
        if (battle.update()) {
            if (currentReplay != null) currentReplay.update(battle.getBattleTick());
            else playingReplay.tickReplay(battle, battle.getBattleTick() + 1);
        }
        battle.postUpdate();
        if (host && System.currentTimeMillis() - lastBroadcastTimestamp > NETWORK_TICK_MILLISECONDS) {
            lastBroadcastTimestamp = System.currentTimeMillis();
            for (int i = 0; i < battle.getPlayers().size(); i++) {
                Player player = battle.getPlayer(i);
                server.send(0, new PlayerStatePacket(player));
            }
        }
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }
}