package sab.modloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import sab.game.Game;
import sab.game.fighters.FighterType;
import sab.game.stages.StageType;
import sab.util.SabReader;

public final class ModLoader {
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
        Map<String, String> settings = SabReader.read(Paths.get(path).toFile());
        Files.delete(Paths.get(path));

        jar.close();
        return settings;
    }

    public static Mod loadMod(File modFile, Game game) throws IOException {
        if (modFile.isDirectory()) {
            throw new IllegalArgumentException("File cannot be a directory");
        }
        if (!modFile.getName().endsWith(".jar")) {
            throw new IllegalArgumentException("File must be of type jar");
        }
        if (!modFile.exists()) {
            throw new IllegalArgumentException("File must exist");
        }

        Map<String, String> modSettings = getModSettings(modFile);
        if (modSettings == null)
            return null;

        Mod mod = new Mod(modSettings.get("display_name"), modSettings.get("namespace"),
                modSettings.get("version"), modSettings.get("version"));

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

            String fileName = entry.getName().split("/")[entry.getName().split("/").length - 1];
            String path = modFile.getCanonicalPath().substring(0,
                    modFile.getCanonicalPath().length() - modFile.getName().length())
                    + "resources/" + fileName;

            Files.copy(entryReader, Paths.get(path));
            File entryFile = Paths.get(path).toFile();

            if (entry.getName().endsWith(".png")) {
                game.window.imageProvider.loadAbsoluteImage(path, mod.namespace
                        + ":" + (entry.getName().split("/"))[entry.getName().split("/").length - 1]);
            } else if (entry.getName().endsWith(".class") && entry.getName().startsWith(mod.namespace)) {
                try {
                    Class<?> clazz = classLoader
                            .loadClass(entry.getName().replace("/", ".").substring(0, entry.getName().length() - 6));
                    if (FighterType.class.isAssignableFrom(clazz)) {
                        mod.addFighter((Class<? extends FighterType>) clazz);
                    }
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }

            Files.delete(Paths.get(path));
        }

        jarReader.close();
        modJarFile.close();
        classLoader.close();

        return mod;
    }

    public static FighterType getFighterType(Class<? extends FighterType> type) {
        try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
    }

    public static StageType getStageType(Class<? extends StageType> type) {
        try {
			return type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException(e);
		}
    }
}