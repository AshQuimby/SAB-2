package sab.game.screen.extras;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.sab_format.SabParsingException;
import com.seagull_engine.Seagraphics;

import sab.error.SabError;
import sab.game.Game;
import sab.game.SabSounds;
import sab.game.screen.*;
import sab.game.screen.error.ErrorScreen;
import sab.screen.Screen;
import sab.util.Utils;

import java.io.File;

public class ReplayScreen extends SelectorScreen {

    public ReplayScreen() {
        super(new File("../saves/replays").list());
        String[] replays = new String[options.length + 1];
        for (int i = 0; i < options.length; i++) {
            replays[i] = options[i];
        }
        replays[options.length] = "Back";
        options = replays;
    }

    @Override
    public void render(Seagraphics g) {
        g.useDynamicCamera();
        g.getDynamicCamera().targetPosition = new Vector2(0, -(selectorIndex - 3) * 10).add(64 * MathUtils.sin(.004f * Game.game.window.getTick()), 0);
        g.getDynamicCamera().targetZoom = 0.9f;
        g.getDynamicCamera().updateSeagullCamera(16);
        TitleScreen.drawTitle(g);
        g.useStaticCamera();

        //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -400 / 2, -Game.game.window.resolutionY / 2, 400, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));

        for (int i = 0; i < options.length; i++) {
            //Rectangle bounds = g.getTextBounds(options[i], Game.getDefaultFont(), 0,  i * -52 - 26, 1.5f * Game.getDefaultFontScale(), 0);

            //float color = i == selectorIndex ? 1f : 0;

            //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
            //new Color(color, color, color, 0.5f));

            //g.drawText(options[i], Game.getDefaultFont(), 0,  i * -52 - 26, 1.5f * Game.getDefaultFontScale(), Color.WHITE, 0);
            Utils.drawButton(g, 0, -i * 52 - 26, options[i],1.5f * Game.getDefaultFontScale(), selectorIndex == i);
        }
    }

    @Override
    public Screen onSelect(int selection) {
        SabSounds.playSound(SabSounds.SELECT);
        if (selection == options.length - 1) {
            return onBack();
        } else {
            File[] replays = new File("../saves/replays").listFiles();
            try {
                return new BattleScreen(replays[selection]);
            } catch (SabParsingException e) {
                return new ErrorScreen(new SabError("Error parsing replay file", e.getLocalizedMessage()));
            }
        }
    }

    @Override
    protected Screen onBack() {
        return new TitleScreen(false);
    }
}