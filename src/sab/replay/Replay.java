package sab.replay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import sab.game.Battle;
import sab.game.Player;
import sab.game.stage.Stage;
import sab.net.Keys;
import sab.util.SABReader;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class Replay {
    public static int inputsDetected;
    public long seed;
    public Player player1;
    public Player player2;
    public Stage stage;
    public int lives;
    public int player1AI;
    public int player2AI;
    public boolean assBalls;
    public boolean stageHazards;

    private HashMap<Integer, FrameAction> frameActions = new HashMap<>();
    private FrameAction currentFrameAction = new FrameAction();

    public Replay(long seed, Player player1, Player player2, int player1AI, int player2AI, Stage stage, int lives, boolean assBalls, boolean stageHazards) {
        this.seed = seed;
        this.player1 = player1;
        this.player2 = player2;
        this.player1AI = player1AI;
        this.player2AI = player2AI;
        this.stage = stage;
        this.lives = lives;
        this.assBalls = assBalls;
        this.stageHazards = stageHazards;
        inputsDetected = 0;
    }

    public Replay() {

    }

    public void keyStateChanged(int keyCode, boolean press) {
        if (press) currentFrameAction.keysPressed.add(keyCode);
        else currentFrameAction.keysReleased.add(keyCode);
    }

    public void update(int tick) {
        if (currentFrameAction.keysReleased.size() > 0 || currentFrameAction.keysPressed.size() > 0) frameActions.put(tick, currentFrameAction);
        currentFrameAction = new FrameAction();
    }

    public void tickReplay(Battle battle, int tick) {
//        System.out.println(tick);
//        System.out.println(frameActions.containsKey(tick));
        if (frameActions.get(tick) != null) frameActions.get(tick).fire(battle);
    }

    private HashMap<String, String> getSABEncodedMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("player1Fighter", player1.fighter.type.getClass().getName());
        map.put("player2Fighter", player2.fighter.type.getClass().getName());
        map.put("stage", stage.type.getClass().getName());
        map.put("player1Costume", "" + player1.costume);
        map.put("player2Costume", "" + player2.costume);
        map.put("player1AI", "" + player1AI);
        map.put("player2AI", "" + player2AI);
        map.put("lives", "" + lives);
        map.put("assBalls", "" + assBalls);
        map.put("stageHazards", "" + stageHazards);
        map.put("seed", "" + seed);
        for (int i : frameActions.keySet()) {
            FrameAction action = frameActions.get(i);
            String key = i + "P";
            String value = action.pressedToString();
            map.put(key, value);

            key = i + "R";
            value = action.releasedToString();
            map.put(key, value);
        }
        return map;
    }

    public void fromSABEncodedMap(HashMap<String, String> data) {
        int inputs = 0;
        for (String key : data.keySet()) {
            if (!(key.endsWith("R") || key.endsWith("P"))) continue;

            String[] values = data.get(key).split(" ");
            int tick = Integer.parseInt(key.substring(0, key.length() - 1));
            if (key.endsWith("P")) {
                for (String value : values) {
                    int keyCode = Integer.parseInt(value);
                    if (!frameActions.containsKey(tick)) {
                        frameActions.put(tick, new FrameAction());
                    }
                    frameActions.get(tick).keysPressed.add(keyCode);
                }
            } else {
                for (String value : values) {
                    int keyCode = Integer.parseInt(value);
                    if (!frameActions.containsKey(tick)) {
                        frameActions.put(tick, new FrameAction());
                    }
                    frameActions.get(tick).keysReleased.add(keyCode);
                }
            }
        }
    }

    public void saveReplay() {
        File replaysFolder = new File("../saves/replays");
        if (!replaysFolder.exists()) {
            replaysFolder.mkdir();
        }

        String replayPath = "../saves/replays/" + Calendar.getInstance().getTime().toString().replace(":", "-").replace(" ", "-") + ".sab";

        try {
            SABReader.createFile(replayPath, getSABEncodedMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class FrameAction {
        public List<Integer> keysPressed = new ArrayList<>();
        public List<Integer> keysReleased = new ArrayList<>();

        public void fire(Battle battle) {
            for (int keyCode : keysPressed) {
                inputsDetected++;
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
                } else if (keyCode == Input.Keys.UP) {
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
                } else {
                    Gdx.input.getInputProcessor().keyDown(keyCode);
                }
            }

            for (int keyCode : keysReleased) {
                inputsDetected++;
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
                    battle.getPlayer(0).keys.release(Keys.PARRY);
                } else if (keyCode == Input.Keys.UP) {
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
                    battle.getPlayer(1).keys.release(Keys.PARRY);
                } else {
                    Gdx.input.getInputProcessor().keyUp(keyCode);
                }
            }
        }

        public String pressedToString() {
            String value = "";
            for (int i : keysPressed) {
                value += i + " ";
            }
            return value;
        }

        public String releasedToString() {
            String value = "";
            for (int i : keysReleased) {
                value += i + " ";
            }
            return value;
        }
    }
}
