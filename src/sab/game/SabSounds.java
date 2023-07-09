package sab.game;

import com.seagull_engine.SeagullSounds;
import sab.game.settings.Setting;
import sab.game.settings.Settings;

import java.util.Set;


public class SabSounds {
    public static final SeagullSounds soundEngine = Game.game.window.soundEngine;

    public static final String BLIP = "blip.mp3";
    public static final String SELECT = "select.mp3";

    private static boolean playingJukebox = false;

    public static void playSound(String name) {
        if (!Settings.localSettings.muteGame.value) soundEngine.playSound(name, Settings.localSettings.sfxVolume.asFloat() * Settings.localSettings.masterVolume.asFloat());
    }

    public static void playMusic(String name, boolean loops) {
        float volume;
        if (Settings.localSettings.muteGame.value) volume = 0f;
        else volume = Settings.localSettings.musicVolume.asFloat() * Settings.localSettings.masterVolume.asFloat();
        soundEngine.playMusic(name, loops, 1, volume, 0);
        playingJukebox = false;
    }

    public static void playJukeboxMusic(String name, boolean loops) {
        float volume;
        if (Settings.localSettings.muteGame.value) volume = 0f;
        else if (Settings.localSettings.bypassJukebox.value) volume = Settings.localSettings.jukeboxVolume.asFloat();
        else volume = Settings.localSettings.jukeboxVolume.asFloat() * Settings.localSettings.masterVolume.asFloat();
        soundEngine.playMusic(name, loops, 1, volume, 0);
        playingJukebox = true;
    }

    public static void resetCurrentMusicVolume() {
        if (Settings.localSettings.muteGame.value) {
            soundEngine.setCurrentMusicVolume(0f);
        } else if (playingJukebox) {
            if (Settings.localSettings.bypassJukebox.value) soundEngine.setCurrentMusicVolume(Settings.localSettings.jukeboxVolume.asFloat());
            else soundEngine.setCurrentMusicVolume(Settings.localSettings.jukeboxVolume.asFloat() * Settings.localSettings.masterVolume.asFloat());
        } else {
            soundEngine.setCurrentMusicVolume(Settings.localSettings.musicVolume.asFloat() * Settings.localSettings.masterVolume.asFloat());
        }
    }

    public static void stopMusic() {
        soundEngine.stopMusic();
    }

    public static void pauseMusic() {
        soundEngine.pauseMusic();
    }

    public static void unpauseMusic() {
        soundEngine.unpauseMusic();
    }

    public static void setLooping(boolean looping) {
        soundEngine.setCurrentMusicLoop(looping);
    }
}
