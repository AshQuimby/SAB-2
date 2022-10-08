package sab.game;

import com.badlogic.gdx.math.MathUtils;
import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.attack.AttackType;
import sab.game.fighter.BigSeagull;
import sab.game.fighter.Chain;
import sab.game.fighter.EmperorEvil;
import sab.game.fighter.FighterType;
import sab.game.fighter.Gus;
import sab.game.fighter.Marvin;
import sab.game.fighter.Snas;
import sab.game.fighter.Walouis;
import sab.game.screen.CharacterSelectScreen;
import sab.game.screen.JukeboxScreen;
import sab.game.screen.ModErrorScreen;
import sab.game.screen.TitleScreen;
import sab.game.stage.Boxtopia;
import sab.game.stage.COBS;
import sab.game.stage.DesertBridge;
import sab.game.stage.LastLocation;
import sab.game.stage.OurSports;
import sab.game.stage.StageType;
import sab.game.stage.ThumbabasLair;
import sab.game.stage.Warzone;
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.screen.Screen;

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
        baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[] {Marvin.class, Chain.class, Walouis.class, Gus.class, EmperorEvil.class, Snas.class, BigSeagull.class});
        baseGame.addStages((Class<? extends StageType>[]) new Class<?>[] {LastLocation.class, Warzone.class, DesertBridge.class, ThumbabasLair.class, OurSports.class, COBS.class, Boxtopia.class});
        addMod(baseGame);
        loadMods();
        
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
        switch (MathUtils.random.nextInt(2)) {
            case 0 : {
                titleBackground = "title_screen_background.png";
                break;
            }
            case 1 : {
                titleBackground = "title_screen_background_alt_1.png";
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
            e.printStackTrace();
            System.exit(69);
        }
    }
}
