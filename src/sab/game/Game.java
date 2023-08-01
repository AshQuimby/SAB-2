package sab.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import com.seagull_engine.graphics.ParallaxBackground;
import com.seagull_engine.graphics.SpriteShader;
import sab.game.fighter.*;
import sab.game.screen.battle_adjacent.CharacterSelectScreen;
import sab.game.screen.extras.JukeboxScreen;
import sab.game.screen.ModErrorScreen;
import sab.game.screen.TitleScreen;
import sab.game.settings.Settings;
import sab.game.stage.*;
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.modloader.ModType;
import sab.screen.Screen;
import sab.util.Utils;
import sab.util.SabRandom;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.zip.Deflater;

public class Game extends Messenger {
    public static final Game game = new Game();

    public static ControllerManager controllerManager;
    public static Map<Integer, Boolean> controllerKeysPressed;
    public final List<Class<? extends FighterType>> fighters;
    public final List<Class<? extends StageType>> stages;
    public final CharacterSelectScreen globalCharacterSelectScreen;
    public static ParallaxBackground titleBackground;
    private List<String> modErrors;
    private Screen screen;
    private boolean fullscreen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<>();
        fighters = new ArrayList<>();
        stages = new ArrayList<>();
        controllerManager = new ControllerManager();
        globalCharacterSelectScreen = new CharacterSelectScreen();
        controllerKeysPressed = new HashMap<>();
        modErrors = new ArrayList<>();
        fullscreen = false;
        JukeboxScreen.loadVanillaSongs();
    }

    public static BitmapFont getDefaultFont() {
        String fontId = Settings.localSettings.font.asRawValue();
        BitmapFont font = game.window.imageProvider.getFont(fontId);
        if (font == null) return game.window.imageProvider.getFont("SAB_font");
        return font;
    }

    public static float getDefaultFontScale() {
        String fontId = Settings.localSettings.font.asRawValue();
        if (fontId.equals("arial")) {
            return 0.28f;
        } else if (fontId.equals("comic_snas")) {
            return 0.24f;
        }
        return 0.16f;
    }

    public Screen getScreen() {
        return screen;
    }
    
    // Initial load tasks (like from Among Us)
    @Override
    public void load() {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.None);
        selectNewTitleScreen();
        window.imageProvider.loadFont("fonts/SAB_font.ttf", 100);
        window.imageProvider.loadFont("fonts/arial.ttf", 100);
        window.imageProvider.loadFont("fonts/comic_snas.ttf", 100);
        window.imageProvider.loadFont("fonts/minecraft.ttf", 137);
        window.imageProvider.loadFont("fonts/shitfont23.ttf", 225);

        window.getGraphics().addShader("enchanted_baguette", new SpriteShader("shaders/default.vsh", "shaders/enchanted_baguette.fsh"));

        Controllers.addListener(controllerManager);
        Settings.localSettings.load();
        if (Settings.localSettings.crtEffect.value) {
            window.getGraphics().addShader("crt", new SpriteShader("shaders/default.vsh", "shaders/crt.fsh"));
            window.getGraphics().addPostEffect("crt");
        }

        Mod baseGame = new Mod("Super Ass Brothers: Remasstered", null, "0.6.7", "Adds all the base game stages and fighters", "marvin_render.png");
        try {
            baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[]{Marvin.class, Chain.class, Walouis.class, Gus.class, EmperorEvil.class, Snas.class, Stephane.class, UnnamedDuck.class, Matthew.class, EmptySoldier.class, John.class, BowlBoy.class, BigSeagull.class});
            baseGame.addStages((Class<? extends StageType>[]) new Class<?>[]{LastLocation.class, Warzone.class, DesertBridge.class, ThumbabasLair.class, OurSports.class, COBS.class, Hyperspace.class, Boxtopia.class, LittleHLand.class, HellTwoBoogaloo.class, GreatShipEjective.class});
        } catch (Exception e) {
            throw new RuntimeException("Like actually what the hell, how did you break this. You should not be able to break this unless your brain cell count reached the long limit.");
        }
        baseGame.modType = new ModType();
        addMod(baseGame);
        loadMods();

        for (Mod mod : Game.game.mods.values()) {
            fighters.addAll(mod.fighters);
            stages.addAll(mod.stages);
            mod.modType.load();
        }

        screen = new TitleScreen(true);

        if (modErrors.size() > 0) {
            screen = new ModErrorScreen(modErrors);
        }

        if (Settings.localSettings.fullscreen.value) {
            goFullscreened();
        }
    }

    public static int getTick() {
        return game.window.getTick();
    }

    // Randomly selects a title screen background
    public static void selectNewTitleScreen() {
        if (Utils.christmas()) {
            titleBackground = new ParallaxBackground(Gdx.files.internal("assets/backgrounds/title_screen/christmas"));
            titleBackground.parallaxMultiplier = 0.5f;
            titleBackground.ambientSpeedMultiplier = 2f;
        } else {
            switch (SabRandom.random(3)) {
                case 0 -> {
                    titleBackground = new ParallaxBackground("assets/backgrounds/title_screen/last_location");
                    titleBackground.parallaxMultiplier = 1f;
                }
                case 1 -> {
                    titleBackground = new ParallaxBackground("assets/backgrounds/title_screen/warzone");
                    titleBackground.ambientSpeedMultiplier = 4f;
                }
                case 2 -> {
                    titleBackground = new ParallaxBackground("assets/backgrounds/title_screen/icy_slab");
                }
            }
        }
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
        } else if (keyCode == Input.Keys.F12) {
            Pixmap pixmap = Pixmap.createFromFrameBuffer(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
            ByteBuffer pixels = pixmap.getPixels();

            // This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
            int size = Gdx.graphics.getBackBufferWidth() * Gdx.graphics.getBackBufferHeight() * 4;
            for (int i = 3; i < size; i += 4) {
                pixels.put(i, (byte) 255);
            }
            String imagePath = "../screenshots/" + Calendar.getInstance().getTime().toString().replace(":", "-").replace(" ", "-") + ".png";
            PixmapIO.writePNG(Gdx.files.local(imagePath), pixmap, Deflater.DEFAULT_COMPRESSION, true);
            pixmap.dispose();
        } else if (keyCode == Input.Keys.F3) {
            window.getImages().loadFolder("assets/images");
            selectNewTitleScreen();
        }
        screen = screen.keyPressed(keyCode);
    }

    @Override
    public void renderLoading(Seagraphics g) {
        g.drawText("Loading...", g.imageProvider.getFont("SAB_font.ttf"), 0, 0, 1, Color.WHITE, 1);
        g.usefulDraw(g.imageProvider.getImage("john_ball_alt_2.png"), window.resolutionX / 2 - 64, window.resolutionY / 2 - 64, 128, 128, window.getTick() / 4 % 4, 4, 0, false, false);
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
    public void fixedUpdate() {
        checkControllerKeys();
        screen = screen.update();
        controllerManager.update();
    }

    public FighterType fighterFromString(String string) {
        for (Class<? extends FighterType> fighter : fighters) {
            if (fighter.getName().equals(string)) {
                try {
                    return (FighterType) fighter.getConstructors()[0].newInstance(null);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
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
                if (!checked && !controller.getButton(i) && controllerKeysPressed.get(key) != null && controllerKeysPressed.get(key)) {
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

    @Override
    public void debugRender(ShapeRenderer s) {
        screen.debugRender(s);
    }

    // Called when the window is closed
    @Override
    public void close() {
        screen.close();
        ModLoader.dispose();
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
            List<File> potentialMods = ModLoader.getPotentialMods(new File("../mods"), new ArrayList<>());

            for (File mod : potentialMods) {
                if (mod.getName().equals("sab-mod-tools.jar")) continue;
                Mod loadedMod = ModLoader.loadMod(mod, this);

                if (loadedMod != null) {
                    addMod(loadedMod);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(69);
        }
    }
}
