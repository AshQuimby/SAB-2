package sab.game;

import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.attacks.AttackType;
import sab.game.fighters.BigSeagull;
import sab.game.fighters.Chain;
import sab.game.fighters.EmperorEvil;
import sab.game.fighters.FighterType;
import sab.game.fighters.Gus;
import sab.game.fighters.Marvin;
import sab.game.fighters.Walouis;
import sab.game.screens.CharacterSelectScreen;
import sab.game.screens.ModErrorScreen;
import sab.game.screens.TitleScreen;
import sab.game.stages.Boxtopia;
import sab.game.stages.LastLocation;
import sab.game.stages.StageType;
import sab.game.stages.Warzone;
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
    private List<String> modErrors;
    private Screen screen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<String, Mod>();
        fighters = new ArrayList<>();
        stages = new ArrayList<>();
        attacks = new HashMap<>();
        globalCharacterSelectScreen = new CharacterSelectScreen();
        modErrors = new ArrayList<>();
    }
    
    @Override
    public void load() {
        Settings.loadSettings();
        Mod baseGame = new Mod("Super Ass Brothers", "sab", "1.0", "base game assets");
        baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[] {Marvin.class, Chain.class, Walouis.class, Gus.class, EmperorEvil.class, BigSeagull.class});
        baseGame.addStages((Class<? extends StageType>[]) new Class<?>[] {LastLocation.class, Warzone.class, Boxtopia.class});
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

    public AttackType getAttackType(String id) {
        return ModLoader.getAttackType(attacks.get(id));
    }
    
    public void addModError(String errorMessage) {
        modErrors.add(errorMessage);
    }

    private void addMod(Mod mod) {
        mods.put(mod.namespace, mod);
    }

    @Override
    public void keyUp(int keyCode) {
        screen = screen.keyReleased(keyCode);
    }

    @Override
    public void keyDown(int keyCode) {
        screen = screen.keyPressed(keyCode);
    }
    
    @Override
    public void update() {
        screen = screen.update();
    }

    @Override
    public void render(Seagraphics g) {
        screen.render(g);
    }

    @Override
    public void close() {
        screen.close();
    }

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
