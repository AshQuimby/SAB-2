package sab.game;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.SeagullEngine;
import sab.modloader.ModLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModdedLocalGame {
    public static void main(String[] args) throws IOException {
        File modsFolder = new File("../mods");
        if (modsFolder.exists()) {
            List<File> modFolders = new ArrayList<>();
            ModLoader.getPotentialMods(modsFolder, new ArrayList<>());
            StringBuilder stringBuilder = new StringBuilder("sab_2.jar");
            for (File file : modFolders) {
                stringBuilder.append(";");
                stringBuilder.append(file.getPath());
                stringBuilder.append("/*");
            }
            ProcessBuilder builder = new ProcessBuilder("java", "-cp", stringBuilder.toString(), "sab.game.LocalGame");
            builder.directory(new File("").getCanonicalFile());
            builder.inheritIO();
            builder.start();
        } else {
            LocalGame.main(args);
        }
    }
}
