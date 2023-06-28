package sab.game.screen.extras;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.sab_format.SabData;
import com.sab_format.SabParsingException;
import com.sab_format.SabReader;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SabSounds;
import sab.game.fighter.Fighter;
import sab.game.fighter.FighterType;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class FightersScreen extends ScreenAdapter {
    private int characterIndex;
    private List<Fighter> fighters = new ArrayList<>();
    private SabData timesPlayed;

    public FightersScreen() {
        characterIndex = 0;
        for (Class<? extends FighterType> fighter : Game.game.fighters) {
            fighters.add(new Fighter(ModLoader.getFighterType(fighter)));
        }

        try {
            timesPlayed = SabReader.read(new File("../saves/times_played.sab"));
        } catch (SabParsingException ignored) {
        }
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage(fighters.get(characterIndex).id + "_render.png"), Game.game.window.resolutionX / 2 - 512 - 8, -Game.game.window.resolutionY / 2 + 12, 512, 512);

        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.drawText(fighters.get(characterIndex).name, Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 64, 3 * Game.getDefaultFontScale(), Color.WHITE, 0);

        String timesPlayed = this.timesPlayed == null ? "Couldn't load data :(" : this.timesPlayed.getValue(fighters.get(characterIndex).id).getRawValue();
        if (timesPlayed == null) {
            timesPlayed = "Why haven't you played me yet :(";
        } else {
            int numTimesPlayed = Integer.parseInt(timesPlayed);
            timesPlayed += " (";
            if (numTimesPlayed >= 1000) timesPlayed += "Beyond Insanity";
            else if (numTimesPlayed >= 500) timesPlayed += "Godlike";
            else if (numTimesPlayed >= 250) timesPlayed += "Religious";
            else if (numTimesPlayed >= 100) timesPlayed += "Maining";
            else if (numTimesPlayed >= 50) timesPlayed += "Adept";
            else if (numTimesPlayed >= 20) timesPlayed += "Rookie";
            else if (numTimesPlayed >= 5) timesPlayed += "Newbie";
            else timesPlayed += "Inexperienced";
            timesPlayed += ")";
        }
        String description = "     " + fighters.get(characterIndex).description + " \n \nDebut: " + fighters.get(characterIndex).debut + "\n \nTimes Played: " + timesPlayed;

        description = Utils.textWrap(g, description, 1, 460);

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
            SabSounds.playSound(SabSounds.BLIP);
            characterIndex = Utils.loop(characterIndex, 1, fighters.size(), 0);
        } else if (keyCode == Input.Keys.LEFT) {
            SabSounds.playSound(SabSounds.BLIP);
            characterIndex = Utils.loop(characterIndex, -1, fighters.size(), 0);
        } else if (keyCode == Input.Keys.ESCAPE) {
            SabSounds.playSound(SabSounds.BLIP);
            return new ExtrasScreen();
        }
        
        return this;
    }
}