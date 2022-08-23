package sab.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.fighters.Chain;
import sab.game.fighters.Fighter;
import sab.game.fighters.FighterType;
import sab.game.fighters.Marvin;
import sab.game.fighters.Walouis;
import sab.game.screens.TitleScreen;
import sab.modloader.Mod;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.util.SabReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class Game extends Messenger {
    public static final Game game = new Game();
    
    public final List<Fighter> fighters;
    private Screen screen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<String, Mod>();
        fighters = new ArrayList<Fighter>();
    }
    
    @Override
    public void load() {
        Settings.loadSettings();
        Mod baseGame = new Mod("Super Ass Brothers", "sab", "1.0", "base game assets");
        baseGame.addFighters((Class<? extends FighterType>[]) new Class<?>[] {Marvin.class, Chain.class, Walouis.class});
        addMod(baseGame);
        loadMods();
        
        for (Mod mod : Game.game.mods.values()) {
            for (Class<? extends FighterType> fighter : mod.fighters) {
                fighters.add(new Fighter(ModLoader.getFighterType(fighter)));
            }
        }

        screen = new TitleScreen(true);
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
        try {
            File modsFolder = new File("../mods");

            if (modsFolder.isDirectory()) {
                for (File mod : modsFolder.listFiles()) {
                    if (mod.getName().endsWith(".jar")) {
                        addMod(ModLoader.loadMod(mod, this));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(69);
        }
    }
}
