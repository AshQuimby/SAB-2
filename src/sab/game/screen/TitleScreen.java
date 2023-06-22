package sab.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Game;
import sab.game.SABSounds;
import sab.game.screen.extras.ExtrasScreen;
import sab.screen.Screen;
import sab.util.Utils;

public class TitleScreen extends SelectorScreen {
    public TitleScreen(boolean playMusic) {
        super(new String[] {"Play", "Host", "Join", "Campaign", "Settings", "Extras", "Quit"});
        if (playMusic) SABSounds.playMusic("lobby_music.mp3", true);
    }

    @Override
    public void render(Seagraphics g) {
        g.useDynamicCamera();
        g.getDynamicCamera().targetPosition.y = (selectorIndex - options.length) * -10;
        g.getDynamicCamera().position.x = Game.game.window.getTick() * 4;
        g.getDynamicCamera().targetZoom = 0.9f;
        g.getDynamicCamera().updateSeagullCamera(16);

        Game.titleBackground.render(g);

        drawTitle(g);

        for (int i = 0; i < options.length; i++) {
            Utils.drawButton(g, 0, -i * 52 + 4, options[i], 1.5f * Game.getDefaultFontScale(), selectorIndex == i);
        }
    }

    public static void drawTitle(Seagraphics g) {
        if (Utils.christmas()) {
            g.scalableDraw(g.imageProvider.getImage("title_pagan.png"), -488 / 2, 52 + MathUtils.sin(Game.game.window.getTick() / 64f) * 4, 488, 248);
        } else {
            g.scalableDraw(g.imageProvider.getImage("title.png"), -488 / 2, 60 + MathUtils.sin(Game.game.window.getTick() / 64f) * 4, 488, 232);
        }
    }

    @Override
    public Screen onSelect(int selection) {
        SABSounds.playSound(SABSounds.SELECT);
        
        switch(selection) {
            case 0 : {
                Game.game.window.camera.reset();
                Game.game.globalCharacterSelectScreen.start();
                return Game.game.globalCharacterSelectScreen;
            }
            case 1 : {
                Game.game.window.camera.reset();
                return new HostGameScreen();
            }
            case 2 : {
                Game.game.window.camera.reset();
                return new JoinGameScreen();
            }
            case 3 : {
                Game.game.window.camera.reset();
                return new CampaignScreen();
            }
            case 4 : {
                Game.game.window.camera.reset();
                return new SettingsScreen();
            }
            case 5 : {
                return new ExtrasScreen();
            }
            case 6 : {
                Gdx.app.exit();
                return this;
            }
        }

        return this;
    }
}