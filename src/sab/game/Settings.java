package sab.game;

import com.sab_format.*;

import java.io.File;
import java.io.IOException;

public class Settings {
    private static boolean staticCamera;
    private static boolean fullscreen;
    private static boolean screenShake;
    private static boolean crtEffect;
    private static float masterVolume;
    private static float sfxVolume;
    private static float musicVolume;
    private static boolean stageHazards;
    private static boolean assBalls;
    private static boolean anticipation;
    private static boolean debugMode;
    private static boolean drawPlayerArrows;
    private static int hostingPort;
    private static String font;

    public static void loadSettings() {
        SabData settings = null;

        try {
            settings = SabReader.read(new File("../settings.sab"));
        } catch (SabParsingException e) {
            readError();
            loadSettings();
        }

        try {
            fromSabData(settings);
        } catch (NullPointerException e) {
            readError();
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
        staticCamera = false;
        screenShake = true;
        crtEffect = false;
        assBalls = true;
        fullscreen = false;
        anticipation = true;
        stageHazards = true;
        debugMode = false;
        drawPlayerArrows = false;
        hostingPort = 19128;
        font = "SAB_font";
    }

    // Don't use, leaving here just in case I want to come back to it at some point
    @Deprecated
    public static void setSettings(boolean staticCameraSetting, float volumeSetting, float musicSetting, float sfxSetting, int hostingPortSetting) {
        staticCamera = staticCameraSetting;
        masterVolume = volumeSetting;
        musicVolume = musicSetting;
        sfxVolume = sfxSetting;
        hostingPort = hostingPortSetting;
    }

    public static void writeFile() {
        try {
            SabWriter.write(new File("../settings.sab"), toSabData());
        } catch (IOException ignored) {
        }
    }

    public static SabData toSabData() {
        SabData data = new SabData();
        data.insertValue("static_camera", SabValue.fromBool(staticCamera));
        data.insertValue("screen_shake", SabValue.fromBool(screenShake));
        data.insertValue("fullscreen", SabValue.fromBool(fullscreen));
        data.insertValue("crt_effect", SabValue.fromBool(crtEffect));
        data.insertValue("ass_balls", SabValue.fromBool(assBalls));
        data.insertValue("stage_hazards", SabValue.fromBool(stageHazards));
        data.insertValue("anticipation", SabValue.fromBool(anticipation));
        data.insertValue("debug_mode", SabValue.fromBool(debugMode));
        data.insertValue("draw_player_arrows", SabValue.fromBool(drawPlayerArrows));
        data.insertValue("master_volume", SabValue.fromFloat(masterVolume));
        data.insertValue("sfx_volume", SabValue.fromFloat(sfxVolume));
        data.insertValue("music_volume", SabValue.fromFloat(musicVolume));
        data.insertValue("hosting_port", SabValue.fromInt(hostingPort));
        data.insertValue("font", new SabValue(font));
        return data;
    }

    public static void fromSabData(SabData settings) {
        try {
            staticCamera = settings.getValue("static_camera").asBool();
            screenShake = settings.getValue("screen_shake").asBool();
            fullscreen = settings.getValue("fullscreen").asBool();
            crtEffect = settings.getValue("crt_effect").asBool();
            assBalls = settings.getValue("ass_balls").asBool();
            stageHazards = settings.getValue("stage_hazards").asBool();
            anticipation = settings.getValue("anticipation").asBool();
            debugMode = settings.getValue("debug_mode").asBool();
            drawPlayerArrows = settings.getValue("draw_player_arrows").asBool();
            masterVolume = settings.getValue("master_volume").asFloat();
            sfxVolume = settings.getValue("sfx_volume").asFloat();
            musicVolume = settings.getValue("music_volume").asFloat();
            hostingPort = settings.getValue("hosting_port").asInt();
            font = settings.getValue("font").getRawValue();

            if (font == null) {
                font = "SAB_font";
                writeFile();
            }
        } catch (NumberFormatException e) {
            defaultSettings();
            writeFile();
            loadSettings();
        }
    }

    public static boolean getDrawPlayerArrows() {
        return drawPlayerArrows;
    }

    public static boolean getStaticCamera() {
        return staticCamera;
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

    public static int getHostingPort() {
        return hostingPort;
    }

    public static boolean isStaticCamera() {
        return staticCamera;
    }

    public static boolean getFullscreen() {
        return fullscreen;
    }

    public static boolean getCrtEffect() {
        return crtEffect;
    }

    public static boolean getScreenShake() {
        return screenShake;
    }

    public static float getSfxVolume() {
        return sfxVolume;
    }

    public static boolean getStageHazards() {
        return stageHazards;
    }

    public static boolean getAssBalls() {
        return assBalls;
    }

    public static boolean getAnticipation() {
        return anticipation;
    }

    public static boolean getDebugMode() {
        return debugMode;
    }

    public static String getDefaultFont() {
        return font;
    }
}
