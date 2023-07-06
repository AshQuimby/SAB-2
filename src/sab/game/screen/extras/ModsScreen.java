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
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class ModsScreen extends ScreenAdapter {
    private static List<Mod> mods;
    private int modIndex;

    public ModsScreen() {
        if (mods == null) {
            mods = new ArrayList<>();
            for (Mod mod : Game.game.mods.values()) {
                mods.add(mod);
            }
        }
        modIndex = 0;
    }

    private Mod getSelectedMod() {
        return mods.get(modIndex);
    }

    @Override
    public void render(Seagraphics g) {
        Mod mod = getSelectedMod();
        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage(mod.icon), Game.game.window.resolutionX / 2 - 512 - 8, -Game.game.window.resolutionY / 2 + 12, 512, 512);

        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.drawText(mod.displayName, Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 64, 2 * Game.getDefaultFontScale(), Color.WHITE, 0);
        g.drawText(mod.version, Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 128, 1 * Game.getDefaultFontScale(), Color.WHITE, 0);

        String description = "     " + mod.description + " \n \nNamespace: " + mod.namespace;

        description = Utils.textWrap(g, description, Game.getDefaultFontScale(), 80);

        g.drawText(description, Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 32, Game.game.window.resolutionY / 2 - 220, Game.getDefaultFontScale(), Color.WHITE, -1);

        Rectangle dots = new Rectangle(0, -320, (16 + 8) * mods.size(), 16);

        dots.setCenter(0, -320);

        for (int i = 0; i < mods.size(); i++) {
            g.usefulTintDraw(g.imageProvider.getImage("dot.png"), dots.x + i * dots.width / mods.size(), dots.y, 16, 16, 0, 1, 0, false, false, i == modIndex ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.5f));
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {

        if (keyCode == Input.Keys.RIGHT) {
            SabSounds.playSound(SabSounds.BLIP);
            modIndex = Utils.loop(modIndex, 1, mods.size(), 0);
        } else if (keyCode == Input.Keys.LEFT) {
            SabSounds.playSound(SabSounds.BLIP);
            modIndex = Utils.loop(modIndex, -1, mods.size(), 0);
        } else if (keyCode == Input.Keys.ESCAPE) {
            SabSounds.playSound(SabSounds.BLIP);
            return new ExtrasScreen();
        }

        return this;
    }
}