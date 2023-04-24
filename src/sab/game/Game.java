package sab.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.MathUtils;
import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.attack.AttackType;
import sab.game.fighter.*;
import sab.game.screen.character_select.CharacterSelectScreen;
import sab.game.screen.extras.JukeboxScreen;
import sab.game.screen.ModErrorScreen;
import sab.game.screen.TitleScreen;
import sab.game.stage.*;
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.util.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Game extends Messenger {
    public static final Game game = new Game();

    public static ControllerManager controllerManager;
    public static Map<Integer, Boolean> controllerKeysPressed;
    public final List<Class<? extends FighterType>> fighters;
    public final List<Class<? extends StageType>> stages;
    public final HashMap<String, Class<? extends AttackType>> attacks;
    public final CharacterSelectScreen globalCharacterSelectScreen;
    public static String titleBackground;
    private List<String> modErrors;
    private Screen screen;
    private boolean fullscreen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<>();
        selectNewTitleScreen();
        fighters = new ArrayList<>();
        stages = new ArrayList<>();
        attacks = new HashMap<>();
        controllerManager = new ControllerManager();
        globalCharacterSelectScreen = new CharacterSelectScreen();
        controllerKeysPressed = new HashMap<>();
        modErrors = new ArrayList<>();
        fullscreen = false;
        JukeboxScreen.loadVanillaSongs();
    }

    public static BitmapFont getDefaultFont() {
        String fontId = Settings.getDefaultFont();
        BitmapFont font = game.window.imageProvider.getFont(fontId);
        if (font == null) return game.window.imageProvider.getFont("SAB_font");
        return font;
    }

    public static float getDefaultFontScale() {
        String fontId = Settings.getDefaultFont();
        if (fontId.equals("SAB_font")) {
            return 1f;
        } else {
            return 1.5f;
        }
    }
    
    // Initial load tasks (like from Among Us)
    @Override
    public void load() {
        Controllers.addListener(controllerManager);
        Settings.loadSettings();
        Mod baseGame = new Mod("Super Ass Brothers: Remasstered", "sab", "1.0", "Base game content");
        try {
            baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[]{Marvin.class, Chain.class, Walouis.class, Gus.class, EmperorEvil.class, Snas.class, Stephane.class, UnnamedDuck.class, Matthew.class, EmptySoldier.class, John.class, BowlBoy.class, BigSeagull.class });
            baseGame.addStages((Class<? extends StageType>[]) new Class<?>[]{LastLocation.class, Warzone.class, DesertBridge.class, ThumbabasLair.class, OurSports.class, COBS.class, Boxtopia.class });
        } catch (Exception e) {
            throw new RuntimeException("Like actually what the hell, how did you break this. You should not be able to break this unless your brain cell count reached the long limit.");
        }
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

        if (Settings.getFullscreen()) {
            goFullscreened();
        }
    }

    public static int getTick() {
        return game.window.getTick();
    }

    // Randomly selects a title screen background
    public static void selectNewTitleScreen() {
        if (Utils.christmas()) {
            titleBackground = "title_screen_background_christmas.png";
        } else {
            switch (MathUtils.random.nextInt(3)) {
                case 0 -> {
                    titleBackground = "title_screen_background.png";
                }
                case 1 -> {
                    titleBackground = "title_screen_background_alt_1.png";
                }
                case 2 -> {
                    titleBackground = "title_screen_background_alt_2.png";
                }
            }
        }
    }

    // Get an AttackType from a String ID, identical to the ModLoader.getAttackType method
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
            if (fullscreen) {
                goWindowed();
            } else {
                goFullscreened();
            }
        }
        screen = screen.keyPressed(keyCode);
    }

    public void goFullscreened() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        fullscreen = true;
    }

    public void goWindowed() {
        Gdx.graphics.setWindowedMode(window.resolutionX, window.resolutionY);
        fullscreen = false;
    }
    
    // Updates every tick
    @Override
    public void update() {
        checkControllerKeys();
        screen = screen.update();
        controllerManager.update();
    }

    public void checkControllerKeys() {
        for (PlayerController controller : controllerManager.getControllers()) {
            Set<Integer> testedInputs = new HashSet<>();
            for (int i = 0; i < controller.getButtonCount(); i++) {
                int key = controller.getKeyFromButton(i);
                if (key != -1) {
                    boolean alreadyChecked = false;
                    for (int input : testedInputs) {
                        if (input == key) {
                            alreadyChecked = true;
                            break;
                        }
                    }
                    if (!alreadyChecked) {
                        if (controller.getButton(i)) {
                            testedInputs.add(key);
                            if (controllerKeysPressed.get(key) == null || !controllerKeysPressed.get(key)) {
                                if (controllerKeysPressed.containsKey(key)) {
                                    controllerKeysPressed.replace(key, true);
                                } else {
                                    controllerKeysPressed.put(key, true);
                                }
                                Gdx.input.getInputProcessor().keyDown(key);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < controller.getAxisCount(); i++) {
                int key = controller.getKeyFromAxis(i);
                if (key != -1) {
                    boolean alreadyChecked = false;
                    for (int input : testedInputs) {
                        if (input == key) {
                            alreadyChecked = true;
                            break;
                        }
                    }
                    if (!alreadyChecked) {
                        if (Math.abs(controller.getAxis(i)) > 0.35f) {
                            testedInputs.add(key);
                            if (controllerKeysPressed.get(key) == null || !controllerKeysPressed.get(key)) {
                                if (controllerKeysPressed.containsKey(key)) {
                                    controllerKeysPressed.replace(key, true);
                                } else {
                                    controllerKeysPressed.put(key, true);
                                }
                                Gdx.input.getInputProcessor().keyDown(key);
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < controller.getButtonCount(); i++) {
                int key = controller.getKeyFromButton(i);
                boolean checked = false;
                for (int input : testedInputs) {
                    if (input == key) {
                        checked = true;
                    }
                }
                if (!checked) {
                    Gdx.input.getInputProcessor().keyUp(key);
                    controllerKeysPressed.replace(key, false);
                }
            }
        }
    }

    public void releaseControllerKey(int key) {
        Gdx.input.getInputProcessor().keyUp(key);
        controllerKeysPressed.replace(key, false);
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
