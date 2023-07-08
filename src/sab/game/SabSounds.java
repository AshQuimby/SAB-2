package sab.game;

import com.seagull_engine.SeagullSounds;
import sab.game.settings.Settings;


public class SabSounds {
    public static final SeagullSounds soundEngine = Game.game.window.soundEngine;

    public static final String BLIP = "blip.mp3";
    public static final String SELECT = "select.mp3";

    public static void playSound(String name) {
        soundEngine.playSound(name, Settings.sfxVolume.asFloat() * Settings.masterVolume.asFloat());
    }

    public static void playMusic(String name, boolean loops) {
        soundEngine.playMusic(name, loops, 1, Settings.musicVolume.asFloat() * Settings.masterVolume.asFloat(), 0);
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
