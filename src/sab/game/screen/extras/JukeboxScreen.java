package sab.game.screen.extras;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.SABSounds;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class JukeboxScreen extends ScreenAdapter {
    private static List<String> songIDs = new ArrayList<>();
    private static List<String> songNames = new ArrayList<>();
    private static List<String> songCredits = new ArrayList<>();
    private static List<Integer> songTempos = new ArrayList<>();
    private static int songIndex;
    private static int widgetIndex;
    private static int playing;
    private static boolean looping;
    private static boolean paused;
    private static int loopAnimationTimer;
    private static int johnFrame;
    private static float johnFrameTimer;
    private static float currentDanceRate;
    private static boolean johnFlip;
    private static boolean johnSleeping;

    static {
        songIndex = 0;
        widgetIndex = 0;
        johnFrame = 0;
        johnFrameTimer = 0;
        playing = -1;
        johnSleeping = true;
        loopAnimationTimer = 0;
        paused = true;
    }

    public static void loadVanillaSongs() {
        addSong("lobby_music.mp3", "Lobby Music", "Beat Thorn", 65);
        addSong("lobby_music_old.mp3", "Lobby Music (Legacy)", "Beat Thorn", 60);
        addSong("jazzlouis.mp3", "Loading Music/Walouis Jazz", "a_viper", 80);
        addSong("last_location.mp3", "Last Location", "Beat Thorn", 160);
        addSong("invasion.mp3", "Invasion", "a_viper", 120);
        addSong("desert_bridge.mp3", "Scorched Sands", "a_viper (motif by AshQuimby)", 85);
        addSong("thumbabas_lair.mp3", "Thumbaba's Lair", "a_viper", 170);
        addSong("our_sports.mp3", "Our Sports Resort", "Beat Thorn", 80);
        addSong("seagull_ultima.mp3", "Ultimatum of Seagull", "Beat Thorn", 130);
        addSong("wavezone.mp3", "Wavezone", "Beat Thorn", 143);
        addSong("genetically_engineered_bad.mp3", "Genetically Engineered Bad", "AshQuimby", 120);
        addSong("artificially_enhanced_trash.mp3", "Artificially Enchanced Trash", "AshQuimby", 120);
        addSong("walouis_sax_solo.mp3", "Walouis' Sax Solo", "Beat Thorn", 160);
    }

    public static void addSong(String fileName, String songName, String artist, int tempo) {
        songIDs.add(fileName);
        songNames.add(songName);
        songCredits.add(artist);
        songTempos .add(tempo);
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

        if ((johnSleeping && ++johnFrameTimer >= 10) || (!johnSleeping && ++johnFrameTimer >= currentDanceRate)) {
            if (++johnFrame >= 7) {
                if (!johnSleeping) johnFlip = !johnFlip;
                johnFrame = 0;
            }
            if (johnSleeping) johnFrameTimer = 0;
            else johnFrameTimer = johnFrameTimer - currentDanceRate;
        }

        g.scalableDraw(g.imageProvider.getImage("jukebox_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage("jukebox_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.drawText(songNames.get(songIndex), g.imageProvider.getFont("SAB_font"), 0, Game.game.window.resolutionY / 2 - 72, 2, Color.WHITE, 0);

        g.drawText("-" + songCredits.get(songIndex), g.imageProvider.getFont("SAB_font"), 0, Game.game.window.resolutionY / 2 - 128, 1.5f, Color.WHITE, 0);

        for (int i = 0; i < 3; i++) {
            g.usefulDraw(g.imageProvider.getImage("widgets.png"), -Game.game.window.resolutionX / 2 + 8, -Game.game.window.resolutionY / 2 + 18 + 90 * i, 72, 72, ((songIndex == playing && i == 0 && !paused) ? 3 : i) + (i == widgetIndex ? 4 : 0), 8, (i == 2 && looping) ? loopAnimationTimer : 0, false, false);
        }

        g.usefulDraw(g.imageProvider.getImage(johnSleeping ? "john_sleeping.png" : "john_dancing.png"), 256 + 80, -256 - 24, 128, 128, johnFrame, 7, 0, johnFlip, false);

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
                            currentDanceRate = (60/7f) / songTempos.get(playing) * 60f;
                            paused = false;
                        } else if (!paused) {
                            SABSounds.pauseMusic();
                            johnFrameTimer = 0;
                            johnFrame = 0;
                            currentDanceRate = (60/7f) / songTempos.get(playing) * 60f;
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
