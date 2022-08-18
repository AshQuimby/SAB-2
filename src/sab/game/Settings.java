package sab.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import sab.util.SabReader;

public class Settings {
    private static float masterVolume;
    private static float sfxVolume;
    private static float musicVolume;

    public static void loadSettings() {
        HashMap<String, String> settings = null;

        try {
            settings = SabReader.read(new File("../settings.sab"));
        } catch (FileNotFoundException | RuntimeException e) {
            readError();
            loadSettings();
        }

        try {
            fromHashMap(settings);
        } catch (NullPointerException e) {
            loadSettings();
        }
    }

    public static void readError() {
        System.out.println("Error reading settings file, resetting file");
        defaultSettings();
        writeFile();
    }

    public static void defaultSettings() {
        masterVolume = 0.5f;
        sfxVolume = 1f;
        musicVolume = 1f;
    }

    public static void setSettings(float volumeSetting, float musicSetting, float sfxSetting) {
        masterVolume = volumeSetting;
        musicVolume = musicSetting;
        sfxVolume = sfxSetting;
    }

    public static void writeFile() {
        try {
            SabReader.write(toHashMap(), new File("../settings.sab"));
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public static HashMap<String, String> toHashMap() {
        HashMap<String, String> settings = new HashMap<String, String>();
        settings.put("master_volume", Float.toString(masterVolume));
        settings.put("sfx_volume", Float.toString(sfxVolume));
        settings.put("music_volume", Float.toString(musicVolume));
        return settings;
    }

    public static void fromHashMap(HashMap<String, String> settings) {
        masterVolume = Float.parseFloat(settings.get("master_volume"));
        sfxVolume = Float.parseFloat(settings.get("sfx_volume"));
        musicVolume = Float.parseFloat(settings.get("music_volume"));
    }

    public static float getMasterVolume() {
        return masterVolume;
    }

    public static float getMusicVolume() {
        return musicVolume;
    }

    public static float getSFXVolume() {
        return sfxVolume;
    }
}
