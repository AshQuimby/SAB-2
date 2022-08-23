package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.SABSounds;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class JukeboxScreen extends ScreenAdapter {
    private List<String> songIDs;
    private List<String> songNames;
    private List<String> songCredits;
    private int songIndex;
    private int widgetIndex;
    private int playing;
    private boolean looping;
    private boolean paused;
    private int loopAnimationTimer;
    private int johnFrame;
    private int johnFrameTimer;
    private boolean johnFlip;
    private boolean johnSleeping;

    public JukeboxScreen() {
        songIDs = new ArrayList<String>();
        songNames = new ArrayList<String>();
        songCredits = new ArrayList<String>();
        songIndex = 0;
        widgetIndex = 0;
        johnFrame = 0;
        johnFrameTimer = 0;
        playing = -1;
        johnSleeping = true;
        loopAnimationTimer = 0;
        paused = true;
        loadVanillaSongs();
    }

    public void loadVanillaSongs() {
        addSong("lobby_music.mp3", "Lobby Music", "Beat Thorn");
        addSong("lobby_music_old.mp3", "Lobby Music (Old)", "Beat Thorn");
        addSong("loading_music.mp3", "Loading Music", "Beat Thorn/AshQuimby");
        addSong("last_location.mp3", "Last Location", "Beat Thorn");
        addSong("overgrown_armada.mp3", "Overgrown Armada", "a_viper");
        addSong("desert_bridge.mp3", "Desert Bridge", "AshQuimby");
        addSong("thumbaba_lair.mp3", "Thumbaba's Lair", "a_viper");
        addSong("our_sports.mp3", "Our Sports Resort", "Beat Thorn");
        addSong("seagull_ultima.mp3", "Seagull Ultima", "Beat Thorn");
        addSong("genetically_engineered_bad.mp3", "Genetically Engineered Bad", "AshQuimby");
        addSong("artificially_enhanced_trash.mp3", "Artificially Enchanced Trash", "AshQuimby");
        addSong("walouis_sax_solo.mp3", "Walouis' Sax Solo", "Beat Thorn");
    }

    public void addSong(String fileName, String songName, String artist) {
        songIDs.add(fileName);
        songNames.add(songName);
        songCredits.add(artist);
    }

    @Override
    public void render(Seagraphics g) {
        if (looping) {
            loopAnimationTimer++;
        }

        if (johnSleeping != paused) {
            johnSleeping = paused;
            johnFrame = 0;
            johnFrameTimer = 0;
            johnFlip = false;
        }

        if (++johnFrameTimer >= 10 || (!johnSleeping && johnFrameTimer >= 5)) {
            if (++johnFrame >= 7) {
                if (!johnSleeping) johnFlip = !johnFlip;
                johnFrame = 0;
            }
            johnFrameTimer = 0;
        }

        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_1.png"), -1152 / 2, -704 / 2, 1152, 704);

        g.scalableDraw(g.imageProvider.getImage("character_description_background_layer_2.png"), -1152 / 2, -704 / 2, 1152, 704);

        g.drawText(songNames.get(songIndex), g.imageProvider.getFont("SAB_font"), 0, 704 / 2 - 72, 2, Color.WHITE, 0);

        g.drawText("-" + songCredits.get(songIndex), g.imageProvider.getFont("SAB_font"), 0, 704 / 2 - 128, 1.5f, Color.WHITE, 0);

        for (int i = 0; i < 3; i++) {
            g.usefulDraw(g.imageProvider.getImage("widgets.png"), -1152 / 2 + 18, -704 / 2 + 18 + 90 * i, 72, 72, ((songIndex == playing && i == 0 && !paused) ? 3 : i) + (i == widgetIndex ? 4 : 0), 8, (i == 2 && looping) ? loopAnimationTimer : 0, false, false);
        }

        g.usefulDraw(g.imageProvider.getImage(johnSleeping ? "john_sleeping.png" : "john_dancing.png"), 256, -256, 128, 128, johnFrame, 7, 0, johnFlip, false);

        // Rectangle dots = new Rectangle(0, -320, (16 + 8) * songIDs.size(), 16);
        
        // dots.setCenter(0, -320);
        
        // for (int i = 0; i < songIDs.size(); i++) {
        //     g.usefulTintDraw(g.imageProvider.getImage("dot.png"), dots.x + i * dots.width / songNames.size(), dots.y, 16, 16, 0, 1, 0, false, false, i == songIndex ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.5f));
        // }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        switch (keyCode) {
            case Input.Keys.RIGHT -> {
                SABSounds.playSound(SABSounds.BLIP);
                songIndex = Utils.loop(songIndex, 1, songNames.size(), 0);
            }
            case Input.Keys.LEFT -> {
                SABSounds.playSound(SABSounds.BLIP);
                songIndex = Utils.loop(songIndex, -1, songNames.size(), 0);
            }
            case Input.Keys.UP -> {
                SABSounds.playSound(SABSounds.BLIP);
                widgetIndex = Utils.loop(widgetIndex, 1, 3, 0);
            }
            case Input.Keys.DOWN -> {
                SABSounds.playSound(SABSounds.BLIP);
                widgetIndex = Utils.loop(widgetIndex, -1, 3, 0);
            }
            case Input.Keys.ENTER -> {
                SABSounds.playSound(SABSounds.BLIP);
                switch (widgetIndex) {
                    case 0 -> {
                        if (playing != songIndex) {
                            SABSounds.playMusic(songIDs.get(songIndex), looping);
                            playing = songIndex;
                            paused = false;
                        } else if (!paused) {
                            SABSounds.pauseMusic();
                            paused = true;
                        } else {
                            SABSounds.unpauseMusic();
                            paused = false;
                        }
                    }
                    case 1 -> {
                        SABSounds.stopMusic();
                        playing = -1;
                        paused = true;
                    }
                    case 2 -> {
                        loopAnimationTimer = 0;
                        looping = !looping;
                        SABSounds.setLooping(looping);
                    }
                }
            }
            case Input.Keys.ESCAPE -> {
                SABSounds.playSound(SABSounds.BLIP);
                return new ExtrasScreen();
            }
        }
        
        return this;
    }
}
