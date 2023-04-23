package sab.game.screen;

import sab.screen.ScreenAdapter;
import sab.util.Utils;

import com.badlogic.gdx.Input;

import sab.game.SABSounds;
import sab.screen.Screen;

public class SelectorScreen extends ScreenAdapter {
    protected int selectorIndex;
    protected String[] options;
    protected int incrementKey;
    protected int decrementKey;

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
            return onSelect(selectorIndex);
        } else if (keyCode == Input.Keys.ESCAPE) {
            return onBack();
        }

        return this;
    }

    public void incrementSelection() {
        SABSounds.playSound(SABSounds.BLIP);
        selectorIndex = Utils.loop(selectorIndex, 1, options.length, 0);
    }

    public void decrementSelection() {
        SABSounds.playSound(SABSounds.BLIP);
        selectorIndex = Utils.loop(selectorIndex, -1, options.length, 0);
    }

    protected Screen onSelect(int selection) {
        SABSounds.playSound(SABSounds.SELECT);
        return this;
    }

    protected Screen onBack() {
        return this;
    }
}
