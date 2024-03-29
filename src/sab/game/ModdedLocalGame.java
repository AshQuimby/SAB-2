package sab.game;

import sab.modloader.ModLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModdedLocalGame {
    public static void main(String[] args) throws IOException {
        File modsFolder = new File("../mods");
        if (modsFolder.exists()) {
            List<File> modFolders;
            modFolders = ModLoader.getPotentialMods(modsFolder, new ArrayList<>());
            StringBuilder stringBuilder = new StringBuilder("sab_2.jar");
            if (modFolders != null) {
                for (File file : modFolders) {
                    stringBuilder.append(";");
                    stringBuilder.append(file.getPath());
                }
            }

            System.out.println(stringBuilder);

            if (System.getProperty("os.name").startsWith("Mac")) {
                ProcessBuilder builder = new ProcessBuilder("java", "-XstartOnFirstThread", "-cp", stringBuilder.toString(), "sab.game.LocalGame");
                builder.directory(new File("").getCanonicalFile());
                builder.inheritIO();
                builder.start();
            } else {
                ProcessBuilder builder = new ProcessBuilder("java", "-cp", stringBuilder.toString(), "sab.game.LocalGame");
                builder.directory(new File("").getCanonicalFile());
                builder.inheritIO();
                builder.start();
            }

        } else {
            if (System.getProperty("os.name").startsWith("Mac")) {
                ProcessBuilder builder = new ProcessBuilder("java", "-XstartOnFirstThread", "-cp", "sab_2.jar", "sab.game.LocalGame");
                builder.directory(new File("").getCanonicalFile());
                builder.inheritIO();
                builder.start();
            } else {
                ProcessBuilder builder = new ProcessBuilder("java", "-cp", "sab_2.jar", "sab.game.LocalGame");
                builder.directory(new File("").getCanonicalFile());
                System.out.println(new File("").getCanonicalFile());
                builder.inheritIO();
                builder.start();
            }
        }
    }
}
