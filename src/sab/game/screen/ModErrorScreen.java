package sab.game.screen;

import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.screen.extras.ExtrasScreen;
import sab.screen.ScreenAdapter;
import sab.screen.Screen;
import sab.util.Utils;

public class ModErrorScreen extends ScreenAdapter {
    private final List<String> errors;
    private float scrollDistance;
    private int scrollAmount;

    public ModErrorScreen(List<String> errors) {
        this.errors = errors;
        errors.add(0, "Error Loading Mods:");
        scrollDistance = 0;
    }

    @Override
    public void render(Seagraphics g) {
        scrollDistance += scrollAmount;
        if (scrollDistance < 0) scrollDistance = 0;
        g.scalableDraw(g.imageProvider.getImage("error_background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        float length = 0;
        for (int i = 0; i < errors.size(); i++) {
            float size;
            if (i == 0) size = 2; else size = 1f;
            String[] text = Utils.textWrapArray(g, errors.get(i), size * Game.getDefaultFontScale(), Game.game.window.resolutionX - 256);
            for (String string : text) {
                Rectangle bounds = g.drawText(string, Game.getDefaultFont(), 0, scrollDistance - length + 256, size * Game.getDefaultFontScale(), Color.WHITE, 0);
                length += bounds.height + 12 * size;
            }
        }

        if (scrollDistance > length) scrollDistance = length;
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ENTER || keyCode == Input.Keys.ESCAPE) {
            return new ExtrasScreen();
        }
        if (keyCode == Input.Keys.UP || keyCode == Input.Keys.W) {
            scrollAmount = -5;
        }
        if (keyCode == Input.Keys.DOWN || keyCode == Input.Keys.S) {
            scrollAmount = 5;
        }
        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.UP || keyCode == Input.Keys.DOWN || keyCode == Input.Keys.W || keyCode == Input.Keys.S) {
            scrollAmount = 0;
        }
        return this;
    }
}
