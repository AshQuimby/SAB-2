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
import sab.game.Game;
import sab.game.attack.AttackType;
import sab.game.fighter.FighterType;
import sab.game.screen.extras.JukeboxScreen;
import sab.game.stage.StageType;

public final class ModLoader {

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

    public static List<File> getPotentialMods(File modsFolder, List<File> directories) {
        List<File> files = new ArrayList<>();
        List<File[]> folders = new ArrayList<>();
        folders.add(modsFolder.listFiles());
        for (int i = 0; i < folders.size(); i++) {
            File[] folder = folders.get(i);
            for (File file : folder) {
                if (file.isDirectory()) {
                    folders.add(file.listFiles());
                    directories.add(file);
                } else if (file.getName().endsWith(".jar")) {
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
                modSettings.get("version"), modSettings.get("description"));
        System.out.println(modSettings.get("load_message"));

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
            String path = new File("../mods/resources/").getCanonicalPath() + "/" + fileName;

            Path target = Paths.get(path);
            Files.copy(entryReader, target);

            if (entry.getName().endsWith(".png")) {
                game.window.imageProvider.loadAbsoluteImage(path, mod.namespace + ":" + (entry.getName().split("/"))[entry.getName().split("/").length - 1]);
            } else if (entry.getName().endsWith(".mp3")) {
                if (entry.getRealName().contains("music")) {
                    game.window.soundEngine.loadMusicAbsolute(path, mod.namespace + ":" + (entry.getName().split("/"))[entry.getName().split("/").length - 1]);
                } else {
                    game.window.soundEngine.loadSoundAbsolute(path, mod.namespace + ":" + (entry.getName().split("/"))[entry.getName().split("/").length - 1]);
                }
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
                    if (ModType.class.isAssignableFrom(clazz)) {
                        // "Unsafe"
                        mod.modType = (ModType) clazz.getConstructors()[0].newInstance();
                        System.out.println(mod.modType.getLoadMessage());
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            try {
                Files.delete(target);
            } catch (FileSystemException e) {
                fileCache.add(new File(path));
            }
        }

        jarReader.close();
        modJarFile.close();
        classLoader.close();

        return mod;
    }

    public static void dispose() {
        for (File file : fileCache) {
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
