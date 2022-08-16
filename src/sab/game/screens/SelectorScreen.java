package sab.game.screens;

import sab.screen.ScreenAdapter;
import sab.util.Utils;

import com.badlogic.gdx.Input;

import sab.game.SABSounds;
import sab.screen.Screen;

public class SelectorScreen extends ScreenAdapter {
    protected int selectorId;
    protected String[] options;

    public SelectorScreen(String[] options) {
        this.options = options;
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.UP) {
            SABSounds.playSound("blip.mp3");
            selectorId = Utils.loop(selectorId, -1, options.length, 0);
        } else if (keyCode == Input.Keys.DOWN) {
            SABSounds.playSound("blip.mp3");
            selectorId = Utils.loop(selectorId, 1, options.length, 0);
        } else if (keyCode == Input.Keys.ENTER) {
            return onSelect(selectorId);
        }

        return this;
    }

    protected Screen onSelect(int selection) {
        return this;
    }
}
