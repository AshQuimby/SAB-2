package sab.game;

import com.seagull_engine.Messenger;
import com.seagull_engine.Seagraphics;

import sab.game.fighters.Chain;
import sab.game.fighters.FighterType;
import sab.game.fighters.Marvin;
import sab.game.screens.TitleScreen;
import sab.modloader.Mod;
import sab.screen.Screen;
import sab.util.SabReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.FileAlreadyExistsException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

public class Game extends Messenger {
    public static final Game game = new Game();

    private Screen screen;

    public final Map<String, Mod> mods;

    public Game() {
        mods = new HashMap<String, Mod>();
    }
    
    @Override
    public void load() {
        Settings.loadSettings();
        Mod baseGame = new Mod("Super Ass Brothers", "sab", "1.0", "base game assets");
        baseGame.addFighters(new Marvin(), new Chain());
        addMod(baseGame);
        loadMods();
        
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

    public Mod getModFromJar(File mod) throws IOException {
        if (!mod.getName().endsWith(".jar")) {
            throw new IllegalArgumentException("File must be of type jar");
        }
        if (!mod.exists()) {
            throw new IllegalArgumentException("File must exist");
        }

        URL url = mod.toURI().toURL();
        URL[] urls = new URL[] { url };

        URLClassLoader cl = new URLClassLoader(urls);

        JarFile modJar = new JarFile(mod);

        JarInputStream jar = new JarInputStream(new FileInputStream(mod));

        String namespace = "";

        // Find the settings file   
        while (jar.available() > 0) {
            JarEntry jFile = jar.getNextJarEntry();
            if (jFile == null)
                break;
            InputStream jIn = modJar.getInputStream(jFile);
            String fileName = jFile.getName().split("/")[jFile.getName().split("/").length - 1];
            String path = mod.getCanonicalPath().substring(0, mod.getCanonicalPath().length() - mod.getName().length())
                    + "resources/" + fileName;
            if (jFile.getName().equals("mod.sab")) {
                Files.copy(jIn, Paths.get(path));
                HashMap<String, String> settings = SabReader.read(Paths.get(path).toFile());
                Files.delete(Paths.get(path));
                namespace = settings.get("namespace");
                mods.put(settings.get("namespace"), new Mod(settings.get("display_name"), settings.get("namespace"),
                        settings.get("version"), settings.get("version")));
                break;
            }
        }

        jar.close();
        jar = new JarInputStream(new FileInputStream(mod));

        // Find assets and classes
        while (jar.available() > 0) {
            JarEntry jFile = jar.getNextJarEntry();
            if (jFile == null)
                break;
            InputStream jIn = modJar.getInputStream(jFile);
            String fileName = jFile.getName().split("/")[jFile.getName().split("/").length - 1];
            String path = mod.getCanonicalPath().substring(0, mod.getCanonicalPath().length() - mod.getName().length())
                    + "resources/" + fileName;
            if (jFile.getName().endsWith(".png")) {
                try {
                    Files.copy(jIn, Paths.get(path));
                    window.imageProvider.loadAbsoluteImage(path, namespace
                            + ":" + (jFile.getName().split("/"))[jFile.getName().split("/").length - 1]);
                    Files.delete(Paths.get(path));
                } catch (FileAlreadyExistsException e) {
                    Files.delete(Paths.get(path));
                    Files.copy(jIn, Paths.get(path));
                    window.imageProvider.loadAbsoluteImage(path, namespace
                            + ":" + (jFile.getName().split("/"))[jFile.getName().split("/").length - 1]);
                    Files.delete(Paths.get(path));
                }
            } else if (jFile.getName().endsWith(".class")
                    && jFile.getName().startsWith(namespace)) {
                try {
                    Class<?> clazz = cl.loadClass(jFile.getName().replace("/", ".").substring(0, jFile.getName().length() - 6));
                    if (FighterType.class.isAssignableFrom(clazz)) {
                        FighterType fighter = (FighterType) clazz.getConstructors()[0].newInstance((Object []) null);
                        mods.get(namespace).addFighters(fighter);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        cl.close();
        modJar.close();
        jar.close();

        return mods.get(namespace);
    }

    public void loadMods() {
        try {
            File modsFolder = new File("../mods");
            for (File mod : modsFolder.listFiles()) {
                if (mod.getName().endsWith(".jar")) {
                    addMod(getModFromJar(mod));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(69);
        }
    }
}
