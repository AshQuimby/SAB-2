package sab.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.attacks.AttackType;
import sab.game.fighters.BigSeagull;
import sab.game.fighters.Chain;
import sab.game.fighters.EmperorEvil;
import sab.game.fighters.EmptySoldier;
import sab.game.fighters.FighterType;
import sab.game.fighters.Gus;
import sab.game.fighters.John;
import sab.game.fighters.Marvin;
import sab.game.fighters.Snas;
import sab.game.fighters.Stephane;
import sab.game.fighters.UnnamedDuck;
import sab.game.fighters.Walouis;
import sab.game.screens.CharacterSelectScreen;
import sab.game.screens.JukeboxScreen;
import sab.game.screens.ModErrorScreen;
import sab.game.screens.TitleScreen;
import sab.game.stages.Boxtopia;
import sab.game.stages.COBS;
import sab.game.stages.DesertBridge;
import sab.game.stages.LastLocation;
import sab.game.stages.OurSports;
import sab.game.stages.StageType;
import sab.game.stages.ThumbabasLair;
import sab.game.stages.Warzone;
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game extends Messenger {
    public static final Game game = new Game();
    
    public final List<Class<? extends FighterType>> fighters;
    public final List<Class<? extends StageType>> stages;
    public final HashMap<String, Class<? extends AttackType>> attacks;
    public final CharacterSelectScreen globalCharacterSelectScreen;
    public static String titleBackground;
    private List<String> modErrors;
    private Screen screen;
    private boolean fullScreen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<String, Mod>();
        selectNewTitleScreen();
        fighters = new ArrayList<>();
        stages = new ArrayList<>();
        attacks = new HashMap<>();
        globalCharacterSelectScreen = new CharacterSelectScreen();
        modErrors = new ArrayList<>();
        JukeboxScreen.loadVanillaSongs();
    }
    
    // Initial load tasks (like from among us)
    @Override
    public void load() {
        Settings.loadSettings();
        Mod baseGame = new Mod("Super Ass Brothers", "sab", "1.0", "base game assets");

        // Class casting to have stuff work with the modloader (The program treats vanilla characters and stages like a mod)
        baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[] {Marvin.class, Chain.class, Walouis.class, Gus.class, EmperorEvil.class, Snas.class, Stephane.class, UnnamedDuck.class, EmptySoldier.class, John.class, BigSeagull.class});
        baseGame.addStages((Class<? extends StageType>[]) new Class<?>[] {LastLocation.class, Warzone.class, DesertBridge.class, ThumbabasLair.class, OurSports.class, COBS.class, Boxtopia.class});
        addMod(baseGame);
        loadMods();
        
        // Adding modded content to the game
        for (Mod mod : Game.game.mods.values()) {
            fighters.addAll(mod.fighters);
            stages.addAll(mod.stages);
            for (String id : mod.attacks.keySet()) {
                attacks.put(id, mod.attacks.get(id));
            }
        }

        screen = new TitleScreen(true);

        if (modErrors.size() > 0) {
            screen = new ModErrorScreen(modErrors);
        }
    }

    // Randomly selects a title screen background
    public static void selectNewTitleScreen() {
        if (Utils.christmas()) { 
            titleBackground = "title_screen_background_christmas.png";
            return;
        }
        switch (MathUtils.random.nextInt(2)) {
            case 0 : {
                titleBackground = "title_screen_background.png";
                break;
            }
            case 1 : {
                titleBackground = "title_screen_background_alt_1.png";
                break;
            }
            case 2 : {
                titleBackground = "title_screen_background_alt_2.png";
                break;
            }
            default : {
                titleBackground = "title_screen_background.png";
            }
        }
    }

    // Get an AttackType from a String ID, idential to the ModLoader.getAttackType method
    public AttackType getAttackType(String id) {
        return ModLoader.getAttackType(attacks.get(id));
    }
    
    // Called when an error is detected loading mods
    public void addModError(String errorMessage) {
        modErrors.add(errorMessage);
    }

    // Adds a mod to the mod list
    private void addMod(Mod mod) {
        mods.put(mod.namespace, mod);
    }

    // Called when a key is released
    @Override
    public void keyUp(int keyCode) {
        screen = screen.keyReleased(keyCode);
    }

    // Called when a key is pressed
    @Override
    public void keyDown(int keyCode) {
        if (keyCode == Input.Keys.F11) {
            if (fullScreen) Gdx.graphics.setWindowedMode(window.resolutionX, window.resolutionY);
            else Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            fullScreen = !fullScreen;
        }
        screen = screen.keyPressed(keyCode);
    }
    
    // Updates every tick
    @Override
    public void update() {
        screen = screen.update();
    }

    // Renders every tick
    @Override
    public void render(Seagraphics g) {
        screen.render(g);
    }

    // Called when the window is closed
    @Override
    public void close() {
        screen.close();
    }

    // Loads mods in the mods folder
    public void loadMods() {
        // Jar file extraction
        File modResources = new File("../mods/resources");
        boolean createResources = false;

        if (modResources.exists()) {
            if (modResources.isDirectory()) {
                for (File file : modResources.listFiles()) {
                    file.delete();
                }
            } else {
                createResources = true;
            }
        } else {
            createResources = true;
        }

        if (createResources) {
            try {
				Files.createDirectories(Paths.get("../mods/resources"));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
        }

        File modsFolder = new File("../mods");
        if (!modsFolder.exists()) {
            try {
                Files.createDirectories(Paths.get("../mods"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (modsFolder.exists() && !modsFolder.isDirectory()) {
            modsFolder.delete();
        }

        try {
            modsFolder = new File("../mods");

            for (File mod : modsFolder.listFiles()) {
                if (mod.getName().endsWith(".jar")) {
                    Mod loadedMod = ModLoader.loadMod(mod, this);

                    if (loadedMod != null) {
                        addMod(loadedMod);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
