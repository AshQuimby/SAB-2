package sab.game.screen.extras;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.game.screen.*;
import sab.screen.Screen;

public class ExtrasScreen extends SelectorScreen {
    
    public ExtrasScreen() {
        super(new String[] {"Fighters", "Credits", "Jukebox", "Mods", "Back"});
    }
    
    @Override
    public void render(Seagraphics g) {
        g.useDynamicCamera();
        g.getDynamicCamera().targetPosition = new Vector2(0, -(selectorIndex - 3) * 10).add(64 * MathUtils.sin(.004f * Game.game.window.getTick()), 0);
        g.getDynamicCamera().targetZoom = 0.9f;
        g.getDynamicCamera().updateSeagullCamera(16);
        g.scalableDraw(g.imageProvider.getImage(Game.titleBackground), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        TitleScreen.drawTitle(g);
        g.useStaticCamera();

        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -400 / 2, -Game.game.window.resolutionY / 2, 400, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));

        for (int i = 0; i < options.length; i++) {
            Rectangle bounds = g.getTextBounds(options[i], Game.getDefaultFont(), 0,  i * -52 - 26, 1.5f * Game.getDefaultFontScale(), 0);

            float color = i == selectorIndex ? 1f : 0;

            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
                    new Color(color, color, color, 0.5f));

            g.drawText(options[i], Game.getDefaultFont(), 0,  i * -52 - 26, 1.5f * Game.getDefaultFontScale(), Color.WHITE, 0);
        }
    }

    @Override
    public Screen onSelect(int selection) {
        SABSounds.playSound(SABSounds.SELECT);
        switch(selection) {
            case 0 -> {
                Game.game.window.camera.reset();
                return new FightersScreen();
            }
            case 1 -> {
                Game.game.window.camera.reset();
                return new CreditsScreen();
            }
            case 2 -> {
                Game.game.window.camera.reset();
                return new JukeboxScreen();
            }
            case 3 -> {
            }
            case 4 -> {
                return onBack();
            }
            default -> {

            }
        }

        return this;
    }

    @Override
    protected Screen onBack() {
        return new TitleScreen(false);
    }
}