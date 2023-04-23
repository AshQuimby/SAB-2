package sab.game.screen.extras;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighter.Fighter;
import sab.game.fighter.FighterType;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class FightersScreen extends ScreenAdapter {
    private int characterIndex;
    private List<Fighter> fighters = new ArrayList<>();

    public FightersScreen() {
        characterIndex = 0;
        for (Class<? extends FighterType> fighter : Game.game.fighters) {
            fighters.add(new Fighter(ModLoader.getFighterType(fighter)));
        }
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage(fighters.get(characterIndex).id + "_render.png"), Game.game.window.resolutionX / 2 - 512 - 8, -Game.game.window.resolutionY / 2 + 12, 512, 512);

        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.drawText(fighters.get(characterIndex).name, Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 64, 3 * Game.getDefaultFontScale(), Color.WHITE, 0);

        String description = "     " + fighters.get(characterIndex).description + " \n \nDebut: " + fighters.get(characterIndex).debut;

        description = Utils.textWrap(g, description, 1, 400);

        g.drawText(description, Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 32, Game.game.window.resolutionY / 2 - 220, Game.getDefaultFontScale(), Color.WHITE, -1);

        Rectangle dots = new Rectangle(0, -320, (16 + 8) * fighters.size(), 16);
        
        dots.setCenter(0, -320);
        
        for (int i = 0; i < fighters.size(); i++) {
            g.usefulTintDraw(g.imageProvider.getImage("dot.png"), dots.x + i * dots.width / fighters.size(), dots.y, 16, 16, 0, 1, 0, false, false, i == characterIndex ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.5f));
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {

        if (keyCode == Input.Keys.RIGHT) {
            SABSounds.playSound(SABSounds.BLIP);
            characterIndex = Utils.loop(characterIndex, 1, fighters.size(), 0);
        } else if (keyCode == Input.Keys.LEFT) {
            SABSounds.playSound(SABSounds.BLIP);
            characterIndex = Utils.loop(characterIndex, -1, fighters.size(), 0);
        } else if (keyCode == Input.Keys.ESCAPE) {
            SABSounds.playSound(SABSounds.BLIP);
            return new ExtrasScreen();
        }
        
        return this;
    }
}