package sab.game.screen;

import com.badlogic.gdx.controllers.Controller;
import sab.net.Keys;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

import com.badlogic.gdx.Input;

import sab.game.SABSounds;
import sab.screen.Screen;

public class SelectorScreen extends ScreenAdapter {
    protected int selectorId;
    protected String[] options;
    protected int incrementKey;
    protected  int decrementKey;

    public SelectorScreen(String[] options) {
        this.options = options;
        incrementKey = Input.Keys.DOWN;
        decrementKey = Input.Keys.UP;
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == incrementKey) {
            incrementSelection();
        } else if (keyCode == decrementKey) {
            decrementSelection();
        } else if (keyCode == Input.Keys.ENTER) {
            return onSelect(selectorId);
        }

        return this;
    }

    public void incrementSelection() {
        SABSounds.playSound(SABSounds.BLIP);
        selectorId = Utils.loop(selectorId, 1, options.length, 0);
    }

    public void decrementSelection() {
        SABSounds.playSound(SABSounds.BLIP);
        selectorId = Utils.loop(selectorId, -1, options.length, 0);
    }

    @Override
    public Screen controllerAxisMoved(Controller controller, int axis, float value, float deltaValue) {
        if (axis == 1) {
            if (deltaValue < -0.25f) {
                decrementSelection();
            } else if (deltaValue > 0.25f) {
                incrementSelection();
            }
        }
        return this;
    }

    protected Screen onSelect(int selection) {
        return this;
    }
}
