package sab.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class SABReader {
    public static HashMap<String, String> read(File file) {
        HashMap<String, String> contents = new HashMap<>();
        Scanner scanner = null;
        try { scanner = new Scanner(file); } catch (FileNotFoundException e) { throw new RuntimeException(e); }

        while (scanner.hasNext()) {
            String token = scanner.next();

            if (token.startsWith("@")) {
                String value = scanner.nextLine();
                contents.put(token.substring(1), value.substring(1));
            } else {
                scanner.close();
                throw new RuntimeException("Invalid token: " + token);
            }
        }

        scanner.close();
        return contents;
    }

    public static String readProperty(String property, File file) {
        Scanner scanner = null;
        try { scanner = new Scanner(file); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        while (scanner.hasNext()) {
            String token = scanner.next();

            if (token.startsWith("@" + property)) {
                String value = scanner.nextLine();
                scanner.close();
                return value.substring(1);
            }
        }
        scanner.close();
        return null;
    }

    public static String readProperty(String property, String path) {
        Scanner scanner = null;
        try { scanner = new Scanner(new File("./")); } catch (FileNotFoundException e) { throw new RuntimeException(e); }
        while (scanner.hasNext()) {
            String token = scanner.next();

            if (token.startsWith("@" + property)) {
                String value = scanner.nextLine();
                scanner.close();
                return value.substring(1);
            }
        }
        scanner.close();
        return null;
    }

    public static void write(Map<String, String> data, File file) throws IOException {
        String path = file.getPath();
        if (file.exists()) {
            Files.delete(file.toPath());
        }
        File f = new File(path);
        f.createNewFile();
        FileWriter writer = new FileWriter(f);
        for (String key : data.keySet()) {
            if (data.get(key).length() > 0) writer.write("@" + key + " " + data.get(key) + "\n");
        }
        writer.close();
    }

    public static void createFile(String path, Map<String, String> data) throws IOException {
        File f = new File(path);

        if (f.exists()) throw new IllegalArgumentException("Specified path already exists");

        f.createNewFile();

        write(data, f);
    }
}