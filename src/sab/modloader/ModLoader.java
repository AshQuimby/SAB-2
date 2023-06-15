package sab.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import sab.game.Game;
import sab.game.attack.AttackType;
import sab.game.fighter.FighterType;
import sab.game.screen.extras.JukeboxScreen;
import sab.game.stage.StageType;
import sab.util.SABReader;

public final class ModLoader {
	
    // Loads a mod's "mod.sab" file from a mod file
    private static Map<String, String> getModSettings(File modFile) throws IOException {
        JarFile jar = new JarFile(modFile);
        JarEntry settingsEntry = jar.getJarEntry("mod.sab");
        if (settingsEntry == null) {
            jar.close();
            return null;
        }

        InputStream settingsReader = jar.getInputStream(settingsEntry);
        String fileName = settingsEntry.getName().split("/")[settingsEntry.getName().split("/").length - 1];
        String path = modFile.getCanonicalPath().substring(0,
                modFile.getCanonicalPath().length() - modFile.getName().length())
                + "resources/" + fileName;

        Files.copy(settingsReader, Paths.get(path));
        Map<String, String> settings = SABReader.read(Paths.get(path).toFile());
        Files.delete(Paths.get(path));

        jar.close();
        return settings;
    }

    // Attempts to load an entire mod from a file object
    public static Mod loadMod(File modFile, Game game) throws IOException {
        if (modFile.isDirectory()) {
            Game.game.addModError("Mod " + modFile.getName() + " failed to load: File cannot be a directory");
            return null;
        }
        if (!modFile.getName().endsWith(".jar")) {
            Game.game.addModError("Mod " + modFile.getName() + " failed to load: File must be of type jar");
            return null;
        }
        if (!modFile.exists()) {
            Game.game.addModError("Mod " + modFile.getName() + " failed to load: File must exist");
            return null;
        }

        Map<String, String> modSettings = getModSettings(modFile);
        if (modSettings == null) {
            Game.game.addModError("Jar file " + modFile.getName() + " is missing a mod.sab file");
            return null;
        }
        for (Mod mod : Game.game.mods.values()) {
            if (modSettings.get("namespace").equals(mod.namespace)) {
                Game.game.addModError("Mod " + modSettings.get("display_name") + " failed to load: Mods with same namespace \"" + mod.namespace + "\". Conflicting mod: " + mod.displayName);
                return null;
            }
        }

        Mod mod = new Mod(modSettings.get("display_name"), modSettings.get("namespace"),
                modSettings.get("version"), modSettings.get("description"));

        URL modURL = modFile.toURI().toURL();
        URL[] urls = new URL[] { modURL };
        URLClassLoader classLoader = new URLClassLoader(urls);

        JarFile modJarFile = new JarFile(modFile);
        JarInputStream jarReader = new JarInputStream(new FileInputStream(modFile));

        while (jarReader.available() > 0) {
            JarEntry entry = jarReader.getNextJarEntry();
            if (entry == null)
                break;

            InputStream entryReader = modJarFile.getInputStream(entry);

            // Transfer sound/image assets in order to be loaded by the game
            String fileName = entry.getName().split("/")[entry.getName().split("/").length - 1];
            String path = modFile.getCanonicalPath().substring(0, modFile.getCanonicalPath().length() - modFile.getName().length()) + "resources/" + fileName;

            Path target = Paths.get(path);
            Files.copy(entryReader, target);

            if (entry.getName().endsWith(".png")) {
                game.window.imageProvider.loadAbsoluteImage(path, mod.namespace
                        + ":" + (entry.getName().split("/"))[entry.getName().split("/").length - 1]);
            } else if (entry.getName().endsWith(".class") && entry.getName().startsWith(mod.namespace)) {
                try {
                    Class<?> clazz = classLoader
                            .loadClass(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6));
                    if (FighterType.class.isAssignableFrom(clazz)) {
                        // This is "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        mod.addFighter((Class<? extends FighterType>) clazz);
                    }
                    if (StageType.class.isAssignableFrom(clazz)) {
                        // This is also "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        mod.addStage((Class<? extends StageType>) clazz);
                    }
                    if (AttackType.class.isAssignableFrom(clazz)) {
                        // Again, "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        String id = clazz.getSimpleName().toLowerCase();
                        mod.addAttack(mod.namespace + ":" + id, (Class<? extends AttackType>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            Files.delete(target);
        }

        jarReader.close();
        modJarFile.close();
        classLoader.close();

        return mod;
    }

    // Adds sound file to jukebox screen as a song
    // Tempo in beats per minute
    public void addSongToJukebox(String fileName, String songName, String artist, int tempo, String background) {
        JukeboxScreen.addSong(fileName, songName, artist, tempo, background);
    }

    // Returns a fighter type from its respective Class
    public static FighterType getFighterType(Class<? extends FighterType> type) {
        try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
    }

    // Returns a fighter type from its respective Class
    public static StageType getStageType(Class<? extends StageType> type) {
        try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
    }

    // Returns a fighter type from its respective Class
    public static AttackType getAttackType(Class<? extends AttackType> type) {
        try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
    }

    // Returns an AttackType based on an all lowercase version of the class name (this is the one a modder would use)
    // Ex: SuperCharge -> "supercharge"
    public static AttackType getAttack(String id) {
        return Game.game.getAttackType(id);
    }
}
