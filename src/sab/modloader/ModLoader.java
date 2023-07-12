package sab.modloader;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystemException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.sab_format.SabData;
import com.sab_format.SabParsingException;
import com.sab_format.SabReader;
import com.seagull_engine.graphics.ParallaxBackground;
import sab.game.Game;
import sab.game.SabSounds;
import sab.game.attack.AttackType;
import sab.game.fighter.FighterType;
import sab.game.screen.extras.JukeboxScreen;
import sab.game.stage.StageType;

import javax.management.ReflectionException;

public final class ModLoader {
    private static final Map<String, ParallaxBackground> parallaxBackgrounds = new HashMap<>();
    public static List<String> fileKeyCache = new ArrayList<>();
    public static List<File> fileCache = new ArrayList<>();

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
        String path = new File("../mods/resources/").getCanonicalPath() + "/" + fileName;

        Files.copy(settingsReader, Paths.get(path));
        try {
            SabData data = SabReader.read(Paths.get(path).toFile());
            Map<String, String> settings = new HashMap<>();
            for (String key : data.getValues().keySet()) settings.put(key, data.getValue(key).getRawValue());
            Files.delete(Paths.get(path));
            jar.close();
            return settings;
        } catch (SabParsingException e) {
            jar.close();
            throw new IOException("Error parsing mod.sab: " + e.getLocalizedMessage());
        }
    }

    public static ParallaxBackground getParallaxBackground(String identifier) {
        return parallaxBackgrounds.get(identifier);
    }

    public static List<File> getPotentialMods(File modsFolder, List<File> directories) {
        List<File> files = new ArrayList<>();
        List<File[]> folders = new ArrayList<>();
        folders.add(modsFolder.listFiles());
        int n = folders.size();
        for (int i = 0; i < n; i++) {
            File[] folder = folders.get(i);
            for (File file : folder) {
                if (file.isDirectory()) {
                    folders.add(file.listFiles());
                    n = folders.size();
                    directories.add(file);
                } else if (file.getName().endsWith(".jar") && !file.getName().endsWith("sab-mod-tools.jar")) {
                    files.add(file);
                }
            }
        }
        return files;
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
                modSettings.get("version"), modSettings.get("description"), modSettings.get("icon"));
        System.out.println(modSettings.get("load_message"));
        boolean autoLoadFighters = modSettings.get("auto_load_fighters") == null ? true : Boolean.parseBoolean(modSettings.get("auto_load_fighters"));
        if (!modSettings.containsKey("icon")) {
            Game.game.addModError("Mod " + modSettings.get("display_name") + " failed to load: mod.sab file lacks @icon property, set it to an image path, ex: example_mod:poopy_man_render.png");
            return null;
        }
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
            String path = new File("../mods/resources/").getCanonicalPath() + "/" + mod.namespace + fileName;

            Path target = Paths.get(path);
            Files.copy(entryReader, target);

            String name = entry.getName().split("/")[entry.getName().split("/").length - 1];
            String key = mod.namespace + ":" + name;
            if (entry.getName().endsWith(".png")) {
                String realName = entry.getRealName();
                if (realName.contains("backgrounds") && !realName.contains("images")) {
                    String shortName = entry.getName().substring(entry.getName().lastIndexOf('/') + 1);
                    int first = realName.indexOf("backgrounds/") + 12;
                    int last = realName.lastIndexOf(shortName) - 1;
                    String backgroundKey = mod.namespace + ":" + realName.substring(first, last);
                    if (parallaxBackgrounds.containsKey(backgroundKey)) {
                    } else {
                        parallaxBackgrounds.put(backgroundKey, new ParallaxBackground());
                    }
                    parallaxBackgrounds.get(backgroundKey).addLayer(new File(path));
                } else {
                    game.window.imageProvider.loadAbsoluteImage(path, key);
                }
            } else if (entry.getName().endsWith(".mp3")) {
                if (entry.getRealName().contains("music")) {
                    game.window.soundEngine.loadMusicAbsolute(path, key);
                } else {
                    game.window.soundEngine.loadSoundAbsolute(path, key);
                }
            } else if (entry.getName().endsWith(".class") && entry.getName().startsWith(mod.namespace)) {
                try {
                    Class<?> clazz = classLoader
                            .loadClass(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6));
                    if (autoLoadFighters && FighterType.class.isAssignableFrom(clazz)) {
                        // This is "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        mod.addFighter((Class<? extends FighterType>) clazz);
                    }
                    if (StageType.class.isAssignableFrom(clazz)) {
                        // This is also "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        mod.addStage((Class<? extends StageType>) clazz);
                    }
                    if (ModBattle.class.isAssignableFrom(clazz)) {
                        // Again, "unsafe" but we know that it will always be safe as long as mods are up-to-date
                        mod.addModBattle((Class<? extends ModBattle>) clazz);
                    }
                    if (ModType.class.isAssignableFrom(clazz)) {
                        // "Unsafe"
                        mod.modType = (ModType) clazz.getConstructors()[0].newInstance();
                        if (!autoLoadFighters) mod.addFighters(mod.modType.getFighters());
                        System.out.println(mod.modType.getLoadMessage());
                    }
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                         ClassNotFoundException e) {
                    System.out.println("Issue loading mod: " + mod.displayName);
                    throw new RuntimeException(e);
                }
            }

            try {
                Files.delete(target);
            } catch (FileSystemException e) {
                if (entry.getRealName().contains("music")) {
                    fileKeyCache.add(key);
                    fileCache.add(new File(path));
                }
            }
        }

        jarReader.close();
        modJarFile.close();
        classLoader.close();

        return mod;
    }

    public static void dispose() {
        for (int i = 0; i < fileCache.size(); i++) {
            File file = fileCache.get(i);
            try {
                SabSounds.soundEngine.getMusic(fileKeyCache.get(i)).dispose();
            } catch (NullPointerException e) {
            }
            file.delete();
        }
    }

    public static void downloadExampleMod() {
        String source = "ignore me :3";
        String destination = "../mods/production";

        try {
            // To be implemented
            ZipFile zipFile = new ZipFile(source);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();
                File entryDestination = new File(destination, entryName);

                if (entry.isDirectory()) {
                    entryDestination.mkdirs();
                } else {
                    InputStream inputStream = zipFile.getInputStream(entry);
                    FileOutputStream outputStream = new FileOutputStream(entryDestination);
                    byte[] buffer = new byte[1024];
                    int length;

                    while ((length = inputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, length);
                    }

                    outputStream.close();
                    inputStream.close();
                }
            }
            zipFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Adds sound file to jukebox screen as a song
    // Tempo in beats per minute
    public static void addSongToJukebox(String fileName, String songName, String artist, int tempo, String background) {
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
}
