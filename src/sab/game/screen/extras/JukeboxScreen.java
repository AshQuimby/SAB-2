package sab.game.screen.extras;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.CollisionResolver;
import sab.game.Game;
import sab.game.SABSounds;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class JukeboxScreen extends ScreenAdapter {
    private static List<String> songIDs = new ArrayList<>();
    private static List<String> songNames = new ArrayList<>();
    private static List<String> songCredits = new ArrayList<>();
    private static List<String> songBackgrounds = new ArrayList<>();
    private static List<Integer> songTempos = new ArrayList<>();
    private static List<RaveLight> raveLights = new ArrayList<>();
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
        addSong("lobby_music.mp3", "Lobby Music", "Beat Thorn", 65, "title_screen_background.png");
        addSong("lobby_music_old.mp3", "Lobby Music (Legacy)", "Beat Thorn", 60, "title_screen_background_alt_1.png");
        addSong("jazzlouis.mp3", "Loading Music/Walouis Jazz", "a_viper", 80, "loading_screen_background.png");
        addSong("last_location.mp3", "Last Location", "Beat Thorn", 160, "background.png");
        addSong("invasion.mp3", "Invasion", "a_viper", 120, "warzone_background.png");
        addSong("desert_bridge.mp3", "Scorched Sands", "a_viper (motif by AshQuimby)", 85, "desert_background.png");
        addSong("thumbabas_lair.mp3", "Thumbaba's Lair", "a_viper", 170, "thumbabas_lair_background.png");
        addSong("our_sports.mp3", "Our Sports Resort", "Beat Thorn", 80, "our_sports_background.png");
        addSong("seagull_ultima.mp3", "Ultimatum of Seagull", "Beat Thorn", 130, "cobs_background.png");
        addSong("wavezone.mp3", "Wavezone", "Beat Thorn", 144, "hyperspace_background.png");
        addSong("first_tough_enemy.mp3", "First Tough Enemy", "a_viper/AshQuimby", 145, "warzone_night_background.png");
        addSong("wings_of_glory.mp3", "Wings of Glory", "a_viper", 130, "error_background.png");
        addSong("genetically_engineered_bad.mp3", "Genetically Engineered Bad", "AshQuimby", 120, "no_ducks.png");
        addSong("walouis_sax_solo.mp3", "Walouis' Sax Solo", "Beat Thorn", 80, "warzone_background.png");
        addSong("walouis_sax_solo_alt.mp3", "Walouis' Second Sax Solo", "AshQuimby/a_viper", 80, "warzone_night_background.png");
    }

    public static void addSong(String fileName, String songName, String artist, int tempo, String jukeboxBackground) {
        songIDs.add(fileName);
        songNames.add(songName);
        songCredits.add(artist);
        songTempos.add(tempo);
        songBackgrounds.add(jukeboxBackground);
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

        g.scalableDraw(g.imageProvider.getImage(songBackgrounds.get(songIndex)), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        g.scalableDraw(g.imageProvider.getImage("jukebox_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);
        g.usefulDraw(g.imageProvider.getImage(johnSleeping ? "john_sleeping.png" : "john_dancing.png"), 256 + 80    , -256 - 24, 128, 128, johnFrame, 7, 0, johnFlip, false);
        g.scalableDraw(g.imageProvider.getImage("jukebox_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.drawText(songNames.get(songIndex), Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 72, 2, Color.WHITE, 0);

        g.drawText("-" + songCredits.get(songIndex), Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 128, 1.5f, Color.WHITE, 0);

        for (int i = 0; i < 3; i++) {
            g.usefulDraw(g.imageProvider.getImage("widgets.png"), -Game.game.window.resolutionX / 2 + 8, -Game.game.window.resolutionY / 2 + 18 + 90 * i, 72, 72, ((songIndex == playing && i == 0 && !paused) ? 3 : i) + (i == widgetIndex ? 4 : 0), 8, (i == 2 && looping) ? loopAnimationTimer : 0, false, false);
        }

        drawRaveLights(g);

        // Rectangle dots = new Rectangle(0, -320, (16 + 8) * songIDs.size(), 16);
        
        // dots.setCenter(0, -320);
        
        // for (int i = 0; i < songIDs.size(); i++) {
        //     g.usefulTintDraw(g.imageProvider.getImage("dot.png"), dots.x + i * dots.width / songNames.size(), dots.y, 16, 16, 0, 1, 0, false, false, i == songIndex ? Color.WHITE : new Color(0.5f, 0.5f, 0.5f, 0.5f));
        // }
    }

    private void drawRaveLights(Seagraphics g) {
        for (RaveLight raveLight : raveLights) {
            raveLight.render(g);
        }
    }

    private void createRaveLights() {
        int numLights = MathUtils.random(4, 6);
        for (int i = 0; i < numLights; i++) {
            raveLights.add(new RaveLight());
        }
    }

    @Override
    public Screen keyPressed(int keyCode) {
        switch (keyCode) {
            case Input.Keys.J -> {
                if (!paused) createRaveLights();
            }
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
                            raveLights.clear();
                            paused = false;
                        } else if (!paused) {
                            SABSounds.pauseMusic();
                            johnFrameTimer = 0;
                            johnFrame = 0;
                            currentDanceRate = (60/7f) / songTempos.get(playing) * 60f;
                            raveLights.clear();
                            paused = true;
                        } else {
                            SABSounds.unpauseMusic();
                            paused = false;
                            raveLights.clear();
                        }
                    }
                    case 1 -> {
                        SABSounds.stopMusic();
                        playing = -1;
                        raveLights.clear();
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

    private static class RaveLight {
        Color color;
        float rotation;
        int x;
        int swingDirection;
        float rotationSpeed;

        RaveLight() {
            ArrayList<Float> colors = new ArrayList<>();
            colors.add(1f);
            colors.add(MathUtils.random() * 0.75f + 0.25f);
            colors.add(0.25f);
            float[] rgb = new float[3];
            for (int i = 0; i < 3; i++) {
                int colorIndex = MathUtils.random(colors.size() - 1);
                rgb[i] = colors.get(colorIndex);
                colors.remove(colorIndex);
            }
            color = new Color(rgb[0], rgb[1], rgb[2], 0.25f);

            rotation = MathUtils.random(0, 360);
            rotationSpeed = MathUtils.random(-2f, 2f    );
            x = MathUtils.random(-Game.game.window.resolutionX / 8, Game.game.window.resolutionX / 8);
        }

        void render(Seagraphics g) {
            rotation += (int) (rotationSpeed);
            rotationSpeed += MathUtils.random(-0.005f, 0.005f) * songTempos.get(playing);
            rotationSpeed = Math.max(-6, Math.min(6, rotationSpeed));
            g.usefulTintDraw(g.imageProvider.getImage("rave_light.png"), x, -Game.game.window.resolutionY + 32, 256, 2560, 0, 1, (int) rotation, false, false, color);
        }
    }
}
