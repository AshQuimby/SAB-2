package sab.game.screen.character_select;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.ControllerManager;
import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.fighter.Fighter;
import sab.game.fighter.FighterType;
import sab.game.screen.StageSelectScreen;
import sab.game.screen.TitleScreen;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.SabReader;
import sab.util.Utils;

public class CharacterSelectScreen extends ScreenAdapter {
    protected static class CharacterSelection {
        public int index;
        public int costume;
        public int type;

        public boolean ready;
        public boolean sus;

        public CharacterSelection(int index, int costume, int type) {
            this.index = index;
            this.costume = costume;
            this.type = type;
            this.ready = false;
            this.sus = false;
        }

        public void setSelection(int index, int costume, List<Fighter> fighters) {
            this.index = index;
            this.costume = costume;

            sus = fighters.get(index).id.equals("gus") && costume == 2 && Math.random() <= .01;
        }
    }

    protected final CharacterSelection player1;
    protected final CharacterSelection player2;

    protected List<Fighter> player1Fighters;
    protected List<Fighter> player2Fighters;

    private boolean updateCharacterList;

    public CharacterSelectScreen() {
        player1 = new CharacterSelection(0, 0, 0);
        player2 = new CharacterSelection(1, 0, 0);

        player1Fighters = new ArrayList<>();
        player2Fighters = new ArrayList<>();

        updateCharacterList = true;
    }

    @Override
    public Screen update() {
        if (updateCharacterList) {
            Game.controllerManager.setInGameState(true);
            player1Fighters.clear();
            player2Fighters.clear();
            for (Class<? extends FighterType> fighter : Game.game.fighters) {
                Fighter f = new Fighter(ModLoader.getFighterType(fighter));
                f.type.setDefaults(f);
                player1Fighters.add(f);
                f = new Fighter(ModLoader.getFighterType(fighter));
                f.type.setDefaults(f);
                player2Fighters.add(f);
            }

            updateCharacterList = false;
        }
        return this;
    }

    public void start() {
        player1.ready = false;
        player2.ready = false;
    }

    @Override
    public void render(Seagraphics g) {
        int fighterCount = Game.game.fighters.size();

        if (player1.costume >= player1Fighters.get(player1.index).costumes) player1.costume = player1Fighters.get(player1.index).costumes - 1;
        if (player2.costume >= player2Fighters.get(player2.index).costumes) player2.costume = player2Fighters.get(player2.index).costumes - 1;

        player1Fighters.get(player1.index).walkAnimation.stepLooping();
        player2Fighters.get(player2.index).walkAnimation.stepLooping();

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        String player1Costume = player1.costume == 0 ? "" : "_alt_" + player1.costume;
        String player2Costume = player2.costume == 0 ? "" : "_alt_" + player2.costume;

        if (player1Fighters.get(player1.index).id.equals("gus") && player1.costume == 2 && player1.sus) player1Costume += "_alt";
        if (player2Fighters.get(player2.index).id.equals("gus") && player2.costume == 2 && player2.sus) player2Costume += "_alt";

        Player player = new Player(player1Fighters.get(player1.index));
        player.drawRect.x = -1280 / 2 + 256 - player1Fighters.get(player1.index).renderWidth / 2;
        player.drawRect.y = Game.game.window.resolutionY / 2 - 160;
        player.drawRect.width = player1Fighters.get(player1.index).renderWidth;
        player.drawRect.height = player1Fighters.get(player1.index).renderHeight;
        player.frame = player1Fighters.get(player1.index).walkAnimation.getFrame();
        player.direction = 1;
        player.costume = player1.costume;
        player.fighter.render(player, g);

        g.usefulDraw(g.imageProvider.getImage(player1Fighters.get(player1.index).id + "_render" + player1Costume + ".png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, 512, 512, 0, 1, 0, true, false);

        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).id + ".png"), -1280 / 2 + 376 - player1Fighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player1Fighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1.index, 1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player1Fighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).id + ".png"), -1280 / 2 + 132 - player1Fighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player1Fighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderWidth, player1Fighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).renderHeight, 0, player1Fighters.get(Utils.loop(player1.index, -1, fighterCount, 0)).frames, 0, true, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        player = new Player(player2Fighters.get(player2.index));
        player.drawRect.x = 1280 / 2 - 256 - player2Fighters.get(player2.index).renderWidth / 2;
        player.drawRect.y = Game.game.window.resolutionY / 2 - 160;
        player.drawRect.width = player2Fighters.get(player2.index).renderWidth;
        player.drawRect.height = player2Fighters.get(player2.index).renderHeight;
        player.frame = player2Fighters.get(player2.index).walkAnimation.getFrame();
        player.costume = player2.costume;
        player.fighter.render(player, g);

        g.usefulDraw(g.imageProvider.getImage(player2Fighters.get(player2.index).id + "_render" + player2Costume + ".png"), Game.game.window.resolutionX / 2 - 512, -Game.game.window.resolutionY / 2, 512, 512, 0, 1, 0, false, false);

        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).id + ".png"), 1280 / 2 - 380 - player2Fighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player2Fighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2.index, -1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));
        g.usefulTintDraw(g.imageProvider.getImage(player2Fighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).id + ".png"), 1280 / 2 - 136 - player2Fighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderWidth / 2, Game.game.window.resolutionY / 2 - 172, player2Fighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderWidth, player2Fighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).renderHeight, 0, player2Fighters.get(Utils.loop(player2.index, 1, fighterCount, 0)).frames, 0, false, false, new Color(0.5f, 0.5f, 0.5f, 1f));

        // 92 x 64

        g.usefulDraw(g.imageProvider.getImage("player_type_indicators.png"), 260 - 132, Game.game.window.resolutionY / 2 - 256, 132, 52, player2.type + 1, 8, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage("player_type_indicators.png"), -260, Game.game.window.resolutionY / 2 - 256, 132, 52, player1.type == 0 ? 0 : player1.type + 1, 8, 0, false, false);

        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), -Game.game.window.resolutionX / 2 + 8, -Game.game.window.resolutionY / 2 + 12, 512, 512, player1.ready ? 1 : 0, 4, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage("fighter_selectors.png"), Game.game.window.resolutionX / 2 - 520, -Game.game.window.resolutionY / 2 + 12, 512, 512, player2.ready ? 3 : 2, 4, 0, false, false);

        g.scalableDraw(g.imageProvider.getImage("character_selector_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        if (player1.ready && player2.ready) {
            g.usefulDraw(g.imageProvider.getImage("fight_button.png"), 0 - 320 / 2, Game.game.window.resolutionY / 2 -100 - 44, 320, 100, (Game.game.window.getTick() / 4) % 10, 11, 0, false, false);
        }
    }

    private void updateTimesPlayed() {
        try {
            HashMap<String, String> timesPlayed = SabReader.read(new File("../saves/timesplayed.sab"));
            if (timesPlayed.containsKey(player1Fighters.get(player1.index).id)) {
                int num = Integer.parseInt(timesPlayed.get(player1Fighters.get(player1.index).id));
                num++;
                timesPlayed.replace(player1Fighters.get(player1.index).id, "" + num);
            } else {
                timesPlayed.put(player1Fighters.get(player1.index).id, "1");
            }
            if (timesPlayed.containsKey(player2Fighters.get(player2.index).id)) {
                int num = Integer.parseInt(timesPlayed.get(player2Fighters.get(player2.index).id));
                num++;
                timesPlayed.replace(player2Fighters.get(player2.index).id, "" + num);
            } else {
                timesPlayed.put(player2Fighters.get(player2.index).id, "1");
            }

            SabReader.write(timesPlayed, new File("../saves/timesplayed.sab"));
        } catch (Exception e) {
            try {
                Files.createFile(Paths.get("../saves/timesplayed.sab"));
            } catch (IOException err) {
                err.printStackTrace();
            }
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        int fighterCount = Game.game.fighters.size();

        if (keyCode == Input.Keys.A && !player1.ready) {
            player1Fighters.get(player1.index).walkAnimation.reset();
            player1.setSelection(Utils.loop(player1.index, -1, fighterCount, 0), player1.costume, player1Fighters);
            player1.costume = 0;
            if (player1.costume >= player1Fighters.get(player1.index).costumes) player1.costume = player1Fighters.get(player1.index).costumes - 1;
            if (player1.costume == player2.costume && player1.index == player2.index) player1.costume = Utils.loop(player2.costume, 1, player2Fighters.get(player2.index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.D && !player1.ready) {
            player1Fighters.get(player1.index).walkAnimation.reset();
            player1.setSelection(Utils.loop(player1.index, 1, fighterCount, 0), player1.costume, player1Fighters);
            player1.costume = 0;
            if (player1.costume >= player1Fighters.get(player1.index).costumes) player1.costume = player1Fighters.get(player1.index).costumes - 1;
            if (player1.costume == player2.costume && player1.index == player2.index) player1.costume = Utils.loop(player2.costume, 1, player2Fighters.get(player2.index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.LEFT && !player2.ready) {
            player2Fighters.get(player2.index).walkAnimation.reset();
            player2.setSelection(Utils.loop(player2.index, -1, fighterCount, 0), player2.costume, player2Fighters);
            player2.costume = 0;
            if (player2.costume >= player2Fighters.get(player2.index).costumes) player2.costume = player2Fighters.get(player2.index).costumes - 1;
            if (player1.costume == player2.costume && player1.index == player2.index) player2.costume = Utils.loop(player1.costume, 1, player1Fighters.get(player1.index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.RIGHT && !player2.ready) {
            player2Fighters.get(player2.index).walkAnimation.reset();
            player2.setSelection(Utils.loop(player2.index, 1, fighterCount, 0), player2.costume, player2Fighters);
            player2.costume = 0;
            if (player2.costume >= player2Fighters.get(player2.index).costumes) player2.costume = player2Fighters.get(player2.index).costumes - 1;
            if (player1.costume == player2.costume && player1.index == player2.index) player2.costume = Utils.loop(player1.costume, 1, player1Fighters.get(player1.index).costumes, 0);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.W) {
            int newCostume = Utils.loop(player1.costume, 1, player1Fighters.get(player1.index).costumes, 0);
            if (newCostume == player2.costume && player1.index == player2.index) newCostume = Utils.loop(newCostume, 1, player1Fighters.get(player1.index).costumes, 0);
            player1.setSelection(player1.index, newCostume, player1Fighters);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.S) {
            int newCostume = Utils.loop(player1.costume, -1, player1Fighters.get(player1.index).costumes, 0);
            if (newCostume == player2.costume && player1.index == player2.index) newCostume = Utils.loop(newCostume, -1, player1Fighters.get(player1.index).costumes, 0);
            player1.setSelection(player1.index, newCostume, player1Fighters);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.UP) {
            int newCostume = Utils.loop(player2.costume, 1, player2Fighters.get(player2.index).costumes, 0);
            if (player1.costume == newCostume && player1.index == player2.index) newCostume = Utils.loop(newCostume, 1, player2Fighters.get(player2.index).costumes, 0);
            player2.setSelection(player2.index, newCostume, player2Fighters);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.DOWN) {
            int newCostume = Utils.loop(player2.costume, -1, player2Fighters.get(player2.index).costumes, 0);
            if (player1.costume == newCostume && player1.index == player2.index) newCostume = Utils.loop(newCostume, -1, player2Fighters.get(player2.index).costumes, 0);
            player2.setSelection(player2.index, newCostume, player2Fighters);
            SABSounds.playSound(SABSounds.BLIP);
        } else if (keyCode == Input.Keys.ENTER && player1.ready && player2.ready) {
            player1Fighters.get(player1.index).walkAnimation.reset();
            player2Fighters.get(player2.index).walkAnimation.reset();
            updateTimesPlayed();
            updateCharacterList = true;
            SABSounds.playSound(SABSounds.BLIP);
            return new StageSelectScreen(player1Fighters.get(player1.index).copy(), player2Fighters.get(player2.index).copy(), player1.costume, player2.costume, player1.type, player2.type);
        } else if (keyCode == Input.Keys.F) {
            player1.ready = !player1.ready;
            if (player1.ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3");
        } else if (keyCode == Input.Keys.M) {
            player2.ready = !player2.ready;
            if (player2.ready) SABSounds.playSound(SABSounds.SELECT); else SABSounds.playSound("deselect.mp3");
        } else if (keyCode == Input.Keys.ESCAPE) {
            updateCharacterList = true;
            Game.controllerManager.setInGameState(false);
            return new TitleScreen(false);
        } else if (keyCode == Input.Keys.SHIFT_LEFT) {
            player1.type = Utils.loop(player1.type, 1, 6, 0);
        } else if (keyCode == Input.Keys.SHIFT_RIGHT) {
            player2.type = Utils.loop(player2.type, 1, 6, 0);
        }

        return this;
    }
}