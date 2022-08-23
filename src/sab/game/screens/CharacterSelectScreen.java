package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighters.Fighter;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class CharacterSelectScreen extends ScreenAdapter {
    private List<Fighter> player1Fighters;
    private List<Fighter> player2Fighters;
    private int player1Index;
    private int player2Index;
    private int player1CostumeIndex;
    private int player2CostumeIndex;

    public CharacterSelectScreen() {
        player1Index = 0;
        player2Index = 1;
        player1CostumeIndex = 0;
        player2CostumeIndex = 0;
        player1Fighters = new ArrayList<Fighter>(); 
        player2Fighters = new ArrayList<Fighter>();
        for (Fighter fighter : Game.game.fighters) {
            Fighter f = fighter.copy();
            f.type.setDefaults(f);
            player1Fighters.add(f);
            f = fighter.copy();
            f.type.setDefaults(f);
            player2Fighters.add(fighter.copy());
        }
    }

    @Override
    public void render(Seagraphics g) {
        int fighterCount = Game.game.fighters.size();

        if (player1CostumeIndex >= player1Fighters.get(player1Index).costumes) player1CostumeIndex = player1Fighters.get(player1Index).costumes - 1;
        if (player2CostumeIndex >= player2Fighters.get(player2Index).costumes) player2CostumeIndex = player2Fighters.get(player2Index).costumes - 1;

        player1Fighters.get(player1Index).walkAnimation.stepLooping();
        player2Fighters.get(player2Index).walkAnimation.stepLooping();

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_1.png"), -1152 / 2, -704 / 2, 1152, 704);

        String player1Costume = player1CostumeIndex == 0 ? "" : "_alt_" + player1CostumeIndex;
        String player2Costume = player2CostumeIndex == 0 ? "" : "_alt_" + player2CostumeIndex;

        g.usefulDraw(g.imageProvider.getImage(player1Fighters.get(player1Index).id + "_render" + player1Costume + ".png"), -1152 / 2, -704 / 2, 512, 512, 0, 1, 0, true, false);
        g.usefulDraw(g.imageProvider.getImage(player1Fighters.get(player1Index).id + player1Costume + ".png"), -1156 / 2 + 256 - player1Fighters.get(player1Index).renderWidth / 2, 704 / 2 - 160, player1Fighters.get(player1Index).renderWidth, player1Fighters.get(player1Index).renderHeight, player1Fighters.get(player1Index).walkAnimation.getFrame(), player1Fighters.get(player1Index).frames, 0, true, false);

        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).id + ".png"), -1156 / 2 + 376 - player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).id + ".png"), -1156 / 2 + 132 - player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        g.usefulDraw(g.imageProvider.getImage(player2Fighters.get(player2Index).id + "_render" + player2Costume + ".png"), 1152 / 2 - 512, -704 / 2, 512, 512, 0, 1, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage(player2Fighters.get(player2Index).id + player2Costume + ".png"), 1156 / 2 - 260 - player2Fighters.get(player2Index).renderWidth / 2, 704 / 2 - 160, player2Fighters.get(player2Index).renderWidth, player2Fighters.get(player2Index).renderHeight, player2Fighters.get(player2Index).walkAnimation.getFrame(), player2Fighters.get(player2Index).frames, 0, false, false);

        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).id + ".png"), 1156 / 2 - 380 - player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).id + ".png"), 1156 / 2 - 136 - player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        
        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_2.png"), -1152 / 2, -704 / 2, 1152, 704);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        int fighterCount = Game.game.fighters.size();
        if (keyCode == Input.Keys.A) {
            player1Fighters.get(player1Index).walkAnimation.reset();
            player1Index = Utils.loop(player1Index, -1, fighterCount, 0);
            if (player1CostumeIndex >= player1Fighters.get(player1Index).costumes) player1CostumeIndex = player1Fighters.get(player1Index).costumes - 1;
            if (player1CostumeIndex == player2CostumeIndex && player1Index == player2Index) player1CostumeIndex = Utils.loop(player2CostumeIndex, 1, player2Fighters.get(player2Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.D) {
            player1Fighters.get(player1Index).walkAnimation.reset();
            player1Index = Utils.loop(player1Index, 1, fighterCount, 0);
            if (player1CostumeIndex >= player1Fighters.get(player1Index).costumes) player1CostumeIndex = player1Fighters.get(player1Index).costumes - 1;
            if (player1CostumeIndex == player2CostumeIndex && player1Index == player2Index) player1CostumeIndex = Utils.loop(player2CostumeIndex, 1, player2Fighters.get(player2Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.RIGHT) {
            player2Fighters.get(player2Index).walkAnimation.reset();
            player2Index = Utils.loop(player2Index, 1, fighterCount, 0);
            if (player2CostumeIndex >= player2Fighters.get(player2Index).costumes) player2CostumeIndex = player2Fighters.get(player2Index).costumes - 1;
            if (player1CostumeIndex == player2CostumeIndex && player1Index == player2Index) player2CostumeIndex = Utils.loop(player1CostumeIndex, 1, player1Fighters.get(player1Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.LEFT) {
            player2Fighters.get(player2Index).walkAnimation.reset();
            player2Index = Utils.loop(player2Index, -1, fighterCount, 0);
            if (player2CostumeIndex >= player2Fighters.get(player2Index).costumes) player2CostumeIndex = player2Fighters.get(player2Index).costumes - 1;
            if (player1CostumeIndex == player2CostumeIndex && player1Index == player2Index) player2CostumeIndex = Utils.loop(player1CostumeIndex, 1, player1Fighters.get(player1Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.W) {
            int newCostume = Utils.loop(player1CostumeIndex, 1, player1Fighters.get(player1Index).costumes, 0);
            if (newCostume == player2CostumeIndex && player1Index == player2Index) newCostume = Utils.loop(player1CostumeIndex, 1, player1Fighters.get(player1Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
            player1CostumeIndex = newCostume;
        } else if (keyCode == Input.Keys.S) {
            int newCostume = Utils.loop(player1CostumeIndex, -1, player1Fighters.get(player1Index).costumes, 0);
            if (newCostume == player2CostumeIndex && player1Index == player2Index) newCostume = Utils.loop(player1CostumeIndex, -1, player1Fighters.get(player1Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
            player1CostumeIndex = newCostume;
        } else if (keyCode == Input.Keys.UP) {
            int newCostume = Utils.loop(player2CostumeIndex, 1, player2Fighters.get(player2Index).costumes, 0);
            if (player1CostumeIndex == newCostume && player1Index == player2Index) newCostume = Utils.loop(newCostume, 1, player2Fighters.get(player2Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
            player2CostumeIndex = newCostume;
        } else if (keyCode == Input.Keys.DOWN) {
            int newCostume = Utils.loop(player2CostumeIndex, -1, player2Fighters.get(player2Index).costumes, 0);
            if (player1CostumeIndex == newCostume && player1Index == player2Index) newCostume = Utils.loop(newCostume, -1, player2Fighters.get(player2Index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
            player2CostumeIndex = newCostume;
        } else if (keyCode == Input.Keys.ENTER) {
            player1Fighters.get(player1Index).walkAnimation.reset();
            player2Fighters.get(player2Index).walkAnimation.reset();
            SABSounds.playSound(SABSounds.BLIP);
            return new LocalBattleScreen(player1Fighters.get(player1Index), player2Fighters.get(player2Index), new int[]{player1CostumeIndex, player2CostumeIndex});
        }
        return this;
    }
}