package sab.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.*;
import sab.net.Keys;

import java.util.HashMap;
import java.util.Map;

public class PlayerController implements Controller {
    private Controller parent;
    public int playerId;
    private boolean inGame;
    private float[] previousAxesValues;
    private boolean[] previousButtonPressed;

    public PlayerController(Controller parentController, int id) {
        parent = parentController;
        previousAxesValues = new float[parent.getAxisCount()];
        previousButtonPressed = new boolean[parent.getMaxButtonIndex() + 1];
        playerId = id;
        inGame = false;
    }

    public int getButtonCount() {
        return previousButtonPressed.length;
    }

    public void update() {
        for (int i = 0; i < previousAxesValues.length; i++) {
            previousAxesValues[i] = parent.getAxis(i);
        }
        for (int i = 0; i < previousButtonPressed.length; i++) {
            previousButtonPressed[i] = parent.getButton(i);
        }
    }

    public void setInGame(boolean value) {
        inGame = value;
    }

    public void checkMacros(InputProcessor inputProcessor, Player player) {
        if (getAxisFlick(2) != 0) {
            if (getAxisFlick(2) == 1) {
                inputProcessor.keyDown(playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.A : Input.Keys.LEFT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.W : Input.Keys.UP);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.S : Input.Keys.DOWN);
            } else if (getAxisFlick(2) == -1) {
                inputProcessor.keyDown(playerId == 0 ? Input.Keys.A : Input.Keys.LEFT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.W : Input.Keys.UP);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.S : Input.Keys.DOWN);
            }
            inputProcessor.keyDown(playerId == 0 ? Input.Keys.F : Input.Keys.M);
        }
        if (getAxisFlick(3) != 0) {
            if (getAxisFlick(3) == 1) {
                inputProcessor.keyDown(playerId == 0 ? Input.Keys.S : Input.Keys.DOWN);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.A : Input.Keys.LEFT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.W : Input.Keys.UP);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT);
            } else if (getAxisFlick(3) == -1) {
                inputProcessor.keyDown(playerId == 0 ? Input.Keys.W : Input.Keys.UP);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.A : Input.Keys.LEFT);
                Game.game.releaseControllerKey(playerId == 0 ? Input.Keys.S : Input.Keys.DOWN);
            }
            inputProcessor.keyDown(playerId == 0 ? Input.Keys.F : Input.Keys.M);
        }
    }
    public int getKeyFromAxis(int axis) {
        switch (axis) {
            case 0 :
                if (getAxis(axis) > 0.35f) {
                    return inGame ? playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT : Input.Keys.RIGHT;
                } else if (getAxis(axis) < 0.35f) {
                    return inGame ? playerId == 0 ? Input.Keys.A : Input.Keys.LEFT : Input.Keys.LEFT;
                }
            case 1 :
                if (getAxis(axis) < -0.75f) {
                    return inGame ? playerId == 0 ? Input.Keys.W : Input.Keys.UP : Input.Keys.UP;
                } else if (getAxis(axis) > 0.75f) {
                    return inGame ? playerId == 0 ? Input.Keys.S : Input.Keys.DOWN : Input.Keys.DOWN;
                }
            default :
        }
        return -1;
    }

    public int getKeyFromButton(int button) {
        switch (button) {
            case 1 :
                return inGame ? playerId == 0 ? Input.Keys.F : Input.Keys.M : Input.Keys.ENTER;
            case 3 :
            case 0 :
                return inGame ? playerId == 0 ? Input.Keys.W : Input.Keys.UP : Input.Keys.ESCAPE;
            case 2 :
                return inGame ? playerId == 0 ? Input.Keys.SHIFT_LEFT : Input.Keys.SHIFT_RIGHT : -1;
            case 6 :
                return inGame ? Input.Keys.ENTER : -1;
            case 4 :
                return inGame ? Input.Keys.ESCAPE : -1;
            case 11 :
                return inGame ? playerId == 0 ? Input.Keys.W : Input.Keys.UP : Input.Keys.UP;
            case 12 :
                return inGame ? playerId == 0 ? Input.Keys.S : Input.Keys.DOWN : Input.Keys.DOWN;
            case 13 :
                return inGame ? playerId == 0 ? Input.Keys.A : Input.Keys.LEFT : Input.Keys.LEFT;
            case 14 :
                return inGame ? playerId == 0 ? Input.Keys.D : Input.Keys.RIGHT : Input.Keys.RIGHT;
        }
        return -1;
    }

    public boolean getButtonJustPressed(int button) {
        return !previousButtonPressed[button] && parent.getButton(button);
    }

    public int getAxisFlick(int axis) {
        if (previousAxesValues.length <= axis) return 0;
        return (Math.abs(parent.getAxis(axis)) > 0.5f && Math.abs(previousAxesValues[axis]) < 0.5f) ? (int) Math.signum(getAxis(axis)) : 0;
    }

    public float getAxisDelta(int axis) {
        if (previousAxesValues.length <= axis) return 0;
        return (parent.getAxis(axis) - previousAxesValues[axis]);
    }

    @Override
    public boolean getButton(int i) {
        return parent.getButton(i);
    }

    @Override
    public float getAxis(int i) {
        return parent.getAxis(i);
    }

    @Override
    public String getName() {
        return parent.getName();
    }

    @Override
    public String getUniqueId() {
        return parent.getUniqueId();
    }

    @Override
    public int getMinButtonIndex() {
        return parent.getMinButtonIndex();
    }

    @Override
    public int getMaxButtonIndex() {
        return parent.getMaxButtonIndex();
    }

    @Override
    public int getAxisCount() {
        return parent.getAxisCount();
    }

    @Override
    public boolean isConnected() {
        return parent.isConnected();
    }

    @Override
    public boolean canVibrate() {
        return parent.canVibrate();
    }

    @Override
    public boolean isVibrating() {
        return parent.isVibrating();
    }

    @Override
    public void startVibration(int i, float v) {
        parent.startVibration(i, v);
    }

    @Override
    public void cancelVibration() {
        parent.cancelVibration();
    }

    @Override
    public boolean supportsPlayerIndex() {
        return parent.supportsPlayerIndex();
    }

    @Override
    public int getPlayerIndex() {
        return parent.getPlayerIndex();
    }

    @Override
    public void setPlayerIndex(int i) {
        parent.setPlayerIndex(i);
    }

    @Override
    public ControllerMapping getMapping() {
        return parent.getMapping();
    }

    @Override
    public ControllerPowerLevel getPowerLevel() {
        return parent.getPowerLevel();
    }

    @Override
    public void addListener(ControllerListener controllerListener) {
        parent.addListener(controllerListener);
    }

    @Override
    public void removeListener(ControllerListener controllerListener) {
        parent.removeListener(controllerListener);
    }
}
