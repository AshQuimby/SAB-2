package sab.dialogue;

import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.util.Utils;

import java.awt.Font;

public class Dialogue {
    private String[] text;
    private int atPosition;
    private Font font;
    private String[] characterNames;
    private String[] fileNames;
    private String lastBlock;
    private int atBlock;
    private int waitFor;
    private boolean fastBlock;

    public Dialogue(String[] text, String[] characterNames, String[] fileNames) {
        this.text = text;
        atPosition = 0;
        atBlock = 0;
        this.characterNames = characterNames;
        this.fileNames = fileNames;
        waitFor = 0;
    }

    public String getPortrait() {
        return fileNames[Integer.parseInt(text[atBlock].substring(0, 1)) - 1];
    }

    public void toEnd() {
        atPosition = text[atBlock].length() - 2;
    }

    public String getName() {
        return characterNames[Integer.parseInt(text[atBlock].substring(0, 1)) - 1];
    }

    public void nextBlock() {
        if (atBlock + 1 >= text.length) {
            return;
        }
        fastBlock = false;
        atPosition = 0;
        atBlock++;
    }

    public boolean finishedBlock() {
        return atPosition >= text[atBlock].length() - 1;
    }

    public boolean finished() {
        return atBlock + 1 >= text.length;
    }

    public String next() {
        if (waitFor > 0 || finishedBlock()) {
            waitFor--;
            return lastBlock;
        }
        atPosition++;
        String next = text[atBlock].substring(Math.min(atPosition, 1), atPosition + 1);
        lastBlock = next;
        return next;
    }

    public String getCurrent() {
        return lastBlock;
    }

    public void render(Seagraphics g) {
        String dialogue = Utils.textWrap(g, getCurrent(), 1, Game.game.window.resolutionX);
        g.scalableDraw(g.imageProvider.getImage("dialogue_back.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        g.scalableDraw(g.imageProvider.getImage(getPortrait()), -Game.game.window.resolutionX / 2 + 8, -Game.game.window.resolutionY / 2 + 4, 176, 176);
        g.drawText(dialogue, Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 176 + 40, -Game.game.window.resolutionY / 2 + 176 - 12, Game.getDefaultFontScale(), Color.WHITE, -1);
        g.drawText(getName(), Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 88 + 8, -Game.game.window.resolutionY / 2 + 28, Game.getDefaultFontScale(), Color.WHITE, 0);
        g.scalableDraw(g.imageProvider.getImage("dialogue_overlay.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
    }
}