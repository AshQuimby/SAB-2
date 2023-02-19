package sab.game;

import com.badlogic.gdx.audio.Sound;
import com.seagull_engine.SeagullSounds;

public class SABSounds {
    public static final SeagullSounds soundEngine = Game.game.window.soundEngine;

    public static final String BLIP = "blip.mp3";
    public static final String SELECT = "select.mp3";

    public static void playSound(String name) {
        soundEngine.playSound(name, Settings.getSFXVolume() * Settings.getMasterVolume());
    }

    public static void playMusic(String name, boolean loops) {
        soundEngine.playMusic(name, loops, 1, false, Settings.getMusicVolume() * Settings.getMasterVolume());
    }

    public static Sound getSound(String name) {
        return soundEngine.getSound(name);
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
