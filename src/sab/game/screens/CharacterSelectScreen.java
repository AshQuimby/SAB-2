package sab.game.screens;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighters.Fighter;
import sab.game.fighters.FighterType;
import sab.modloader.ModLoader;
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
    private boolean player1Ready;
    private boolean player2Ready;
    private boolean refreshed;
    private boolean susGus;
    private boolean inputInvalidated;
    private int[] inputBuffer;

    public CharacterSelectScreen() {
        player1Index = 0;
        player2Index = 1;
        player1CostumeIndex = 0;
        player2CostumeIndex = 0;
        refreshed = false;
        inputInvalidated = false;
        player1Fighters = new ArrayList<Fighter>();
        player2Fighters = new ArrayList<Fighter>();
        susGus = Math.random() > 0.99;
        inputBuffer = new int[4];
    }

    public Screen update() {
        if (!refreshed) {
            for (Class<? extends FighterType> fighter : Game.game.fighters) {
                Fighter f = new Fighter(ModLoader.getFighterType(fighter));
                f.type.setDefaults(f);
                player1Fighters.add(f);
                f = new Fighter(ModLoader.getFighterType(fighter));
                f.type.setDefaults(f);
                player2Fighters.add(f);
            }
            refreshed = true;
        }
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        int fighterCount = Game.game.fighters.size();

        if (player1CostumeIndex >= player1Fighters.get(player1Index).costumes) player1CostumeIndex = player1Fighters.get(player1Index).costumes - 1;
        if (player2CostumeIndex >= player2Fighters.get(player2Index).costumes) player2CostumeIndex = player2Fighters.get(player2Index).costumes - 1;

        player1Fighters.get(player1Index).walkAnimation.stepLooping();
        player2Fighters.get(player2Index).walkAnimation.stepLooping();

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_1.png"), -1152 / 2, -704 / 2, 1152, 704);

        if (LocalDateTime.now().getDayOfMonth() == 1 && LocalDateTime.now().getMonth() == Month.APRIL)
        if (inputBuffer[0] == 49 && inputBuffer[1] == 47 && inputBuffer[1] == 47 && inputBuffer[3] == 46) {
            if (player1Fighters.get(player1Index).id.equals("marvin")) {
                inputInvalidated = true;
                player1CostumeIndex = 1929;
            }

            if (player2Fighters.get(player2Index).id.equals("marvin")) {
                inputInvalidated = true;
                player2CostumeIndex = 1929;
            }
        }

        String player1Costume = player1CostumeIndex == 0 ? "" : "_alt_" + player1CostumeIndex;
        String player2Costume = player2CostumeIndex == 0 ? "" : "_alt_" + player2CostumeIndex;    

        if (player1Fighters.get(player1Index).id.equals("gus") && player1CostumeIndex == 2 && susGus) player1Costume += "_alt";
        if (player2Fighters.get(player2Index).id.equals("gus") && player2CostumeIndex == 2 && susGus) player2Costume += "_alt";

        g.usefulDraw(g.imageProvider.getImage(player1Fighters.get(player1Index).id + "_render" + player1Costume + ".png"), -1152 / 2, -704 / 2, 512, 512, 0, 1, 0, true, false);
        g.usefulDraw(g.imageProvider.getImage(player1Fighters.get(player1Index).id + player1Costume + ".png"), -1156 / 2 + 256 - player1Fighters.get(player1Index).renderWidth / 2, 704 / 2 - 160, player1Fighters.get(player1Index).renderWidth, player1Fighters.get(player1Index).renderHeight, player1Fighters.get(player1Index).walkAnimation.getFrame(), player1Fighters.get(player1Index).frames, 0, true, false);

        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).id + ".png"), -1156 / 2 + 376 - player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1Index, 1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).id + ".png"), -1156 / 2 + 132 - player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1Index, -1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        g.usefulDraw(g.imageProvider.getImage(player2Fighters.get(player2Index).id + "_render" + player2Costume + ".png"), 1152 / 2 - 512, -704 / 2, 512, 512, 0, 1, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage(player2Fighters.get(player2Index).id + player2Costume + ".png"), 1156 / 2 - 260 - player2Fighters.get(player2Index).renderWidth / 2, 704 / 2 - 160, player2Fighters.get(player2Index).renderWidth, player2Fighters.get(player2Index).renderHeight, player2Fighters.get(player2Index).walkAnimation.getFrame(), player2Fighters.get(player2Index).frames, 0, false, false);

        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).id + ".png"), 1156 / 2 - 380 - player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2Index, -1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).id + ".png"), 1156 / 2 - 136 - player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderWidth / 2, 704 / 2 - 172, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2Index, 1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        
        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_2.png"), -1152 / 2, -704 / 2, 1152, 704);

        if (player1Ready && player2Ready) {
            g.usefulDraw(g.imageProvider.getImage("fight_button.png"), -1152 / 2 + 416, 704 / 2 + 144, 320, 100, (Game.game.window.getTick() / 4) % 10, 11, 0, false, false);
        }

        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), -1152 / 2 + 8, -704 /2 + 8, 496, 496, player1Ready ? 1 : 0, 4, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), 1152 / 2 - 8 - 496, -704 /2 + 8, 496, 496, player2Ready ? 3 : 2, 4, 0, false, false);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (!inputInvalidated) {
            for (int i = 0; i < 3; i++) {
                inputBuffer[i] = inputBuffer[i + 1];
            }
            inputBuffer[3] = keyCode;
        }
        
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
        } else if (keyCode == Input.Keys.ENTER && player1Ready && player2Ready) {
            player1Fighters.get(player1Index).walkAnimation.reset();
            player2Fighters.get(player2Index).walkAnimation.reset();
            SABSounds.playSound(SABSounds.BLIP);
            return new StageSelectScreen(player1Fighters.get(player1Index), player2Fighters.get(player2Index), player1CostumeIndex, player2CostumeIndex);
        } else if (keyCode == Input.Keys.F) {
            player1Ready = !player1Ready;
            if (player1Ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3"); 
        } else if (keyCode == Input.Keys.M) {
            player2Ready = !player2Ready;
            if (player2Ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3"); 
        } else if (keyCode == Input.Keys.ESCAPE) {
            return new TitleScreen(false);
        }
        return this;
    }
}