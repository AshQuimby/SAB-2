package sab.game.screen.error;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;
import sab.error.SabError;
import sab.game.Game;
import sab.game.screen.TitleScreen;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

public class ErrorScreen extends ScreenAdapter {
    private final SabError error;

    public ErrorScreen(SabError error) {
        this.error = error;
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("cobs_background.png"), -1280 / 2, -720 / 2, 1280, 720);
        g.drawText(error.type(), Game.getDefaultFont(), 0, 64, 2f, Color.WHITE, 0);
        g.drawText(error.message(), Game.getDefaultFont(), 0, 0, 1.5f, Color.WHITE, 0);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.SHIFT_LEFT) {
            return new TitleScreen(true);
        }

        return this;
    }
}
