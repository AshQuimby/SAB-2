package sab.game.screens;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.screen.ScreenAdapter;
import sab.error.SabError;
import sab.screen.Screen;

public class NetErrorScreen extends ScreenAdapter {
    private final SabError error;

    public NetErrorScreen(SabError error) {
        this.error = error;
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("error_background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        g.drawText(error.type(), g.imageProvider.getFont("SAB_font"), 0, 64, 2f, Color.WHITE, 0);
        g.drawText(error.message(), g.imageProvider.getFont("SAB_font"), 0, 0, 1.5f, Color.WHITE, 0);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.SHIFT_LEFT) {
            return new TitleScreen(true);
        }

        return this;
    }
}
