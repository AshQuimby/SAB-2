package sab.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.screen.Screen;

public class ExtrasScreen extends SelectorScreen {
    
    public ExtrasScreen() {
        super(new String[] {"Fighters", "Credits", "Jukebox", "Mods", "Back"});
    }
    
    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("title_screen_background.png"), -1152 / 2, -704 / 2, 1152, 704);

        g.scalableDraw(g.imageProvider.getImage("title.png"), -488 / 2, 100 + MathUtils.sin(Game.game.window.getTick() / 64f) * 4, 488, 232);

        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -400 / 2, -704 / 2, 400, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));

        for (int i = 0; i < options.length; i++) {
            Rectangle bounds = g.drawText(options[i], g.imageProvider.getFont("SAB_font"), 0,  i * -52 - 16, 1.5f, Color.WHITE, 0);

            float color = i == selectorId ? 1f : 0;

            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
                    new Color(color, color, color, 0.5f));

            g.drawText(options[i], g.imageProvider.getFont("SAB_font"), 0,  i * -52 - 16, 1.5f, Color.WHITE, 0);
        }
    }

    @Override
    public Screen onSelect(int selection) {
        switch(selection) {
            case 0 -> {
                SABSounds.playSound("select.mp3");
                return new CharactersScreen();
            }
            case 1 -> {
                SABSounds.playSound("select.mp3");
            }
            case 2 -> {
                SABSounds.playSound("select.mp3");
                return new JukeboxScreen();
            }
            case 3 -> {
                SABSounds.playSound("select.mp3");
            }
            case 4 -> {
                SABSounds.playSound("select.mp3");
                return new TitleScreen(false);
            }
            default -> {

            }
        }

        return this;
    }
}