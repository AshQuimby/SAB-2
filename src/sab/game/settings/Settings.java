package sab.game.settings;

import com.sab_format.*;
import sab.game.SabSounds;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    public static final Settings localSettings = new Settings();
    
    // Gameplay
    public final BoolSetting stageHazards = new BoolSetting("stage_hazards", "Stage Hazards", true);
    public final BoolSetting assBalls = new BoolSetting("ass_balls", "Spawn Ass Balls", true);
    public final BoolSetting anticipation = new BoolSetting("anticipation", "Victory Anticipation", true);
    public final BoolSetting debugMode = new BoolSetting("debug_mode", "Debug Mode", false);
    public final IntSetting lifeCount = new IntSetting("life_count", "Number of Lives", 3, 1, 5);
    public final ListSetting gameMode = new ListSetting("game_mode", "Game Mode", 0, new String[] {
        "damage", "health"
    }, new String[] {
        "Damage", "Health"
    });
    // Video
    public final BoolSetting fullscreen = new BoolSetting("fullscreen", "Fullscreen", true);
    public final BoolSetting staticCamera = new BoolSetting("static_camera", "Camera", false);
    public final BoolSetting followAssBall = new BoolSetting("follow_ass_ball", "Camera Follows Ass Ball", false);
    public final BoolSetting screenShake = new BoolSetting("screen_shake", "Screen Shake", true);
    public final BoolSetting drawPlayerArrows = new BoolSetting("draw_player_arrows", "Player Arrows", true);
    public final ListSetting font = new ListSetting("font", "Font", 0, new String[] {
            "SAB_font", "comic_snas", "minecraft", "shitfont23", "arial"
    }, new String[] {
            "SAB", "Comic Snas", "Blockbreak", "shitfont23", "Arial"
    });
    public final BoolSetting crtEffect = new BoolSetting("crt_effect", "CRT Monitor Effect [!]", false);

    // Audio
    public final BoolSetting muteGame = new BoolSetting("mute_game", "Mute", false) {
        @Override
        public void next() {
            super.next();
            SabSounds.resetCurrentMusicVolume();
        }
        @Override
        public void previous() {
            super.previous();
            SabSounds.resetCurrentMusicVolume();
        }
    };
    public final PercentageSetting masterVolume = new PercentageSetting("master_volume", "Master Volume", 50) {
        @Override
        public void next() {
            super.next();
            SabSounds.resetCurrentMusicVolume();
        }
        @Override
        public void previous() {
            super.previous();
            SabSounds.resetCurrentMusicVolume();
        }
    };
    public final PercentageSetting musicVolume = new PercentageSetting("music_volume", "Music Volume", 100) {
        @Override
        public void next() {
            super.next();
            SabSounds.resetCurrentMusicVolume();
        }
        @Override
        public void previous() {
            super.previous();
            SabSounds.resetCurrentMusicVolume();
        }
    };
    public final PercentageSetting sfxVolume = new PercentageSetting("sfx_volume", "SFX Volume", 100);
    public final PercentageSetting jukeboxVolume = new PercentageSetting("jukebox_volume", "Jukebox Volume", 100) {
        @Override
        public void next() {
            super.next();
            SabSounds.resetCurrentMusicVolume();
        }
        @Override
        public void previous() {
            super.previous();
            SabSounds.resetCurrentMusicVolume();
        }
    };
    public final BoolSetting bypassJukebox = new BoolSetting("bypass_jukebox", "Jukebox Ignores Mute/Master", false);

    // Multiplayer
    public int hostingPort;

    public final Setting<?>[] settings = new Setting[] {
            // Gameplay
            stageHazards, assBalls, anticipation, debugMode, lifeCount, gameMode,
            // Video
            fullscreen, staticCamera, followAssBall, screenShake, drawPlayerArrows, font, crtEffect,
            // Audio
            muteGame, masterVolume, musicVolume, sfxVolume, jukeboxVolume, bypassJukebox
    };

    public final Setting<?>[] gameplaySettings = new Setting[] {
            stageHazards, assBalls, anticipation, debugMode, lifeCount, gameMode
    };

    public final Setting<?>[] videoSettings = new Setting[] {
            fullscreen, staticCamera, followAssBall, screenShake, drawPlayerArrows, font, crtEffect
    };

    public final Setting<?>[] audioSettings = new Setting[] {
            muteGame, masterVolume, musicVolume, sfxVolume, jukeboxVolume, bypassJukebox
    };

    public void resetAll() {
        for (Setting<?> setting : settings) {
            setting.reset();
        }
        hostingPort = 19128;
    }

    public void save() {
        try {
            SabWriter.write(new File("../settings.sab"), toSabData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        try {
            SabData data = SabReader.read(new File("../settings.sab"));

            List<Setting<?>> unloaded = setFrom(data);
            for (Setting<?> setting : unloaded) {
                data.insertValue(setting.id, new SabValue(setting.asRawValue()));
            }

            if (unloaded.size() > 0) {
                save();
            }
        } catch (SabParsingException e) {
            e.printStackTrace();
            resetAll();
            save();
        }
    }

    private SabData toSabData() {
        SabData data = new SabData();
        for (Setting<?> setting : settings) {
            data.insertValue(setting.id, new SabValue(setting.asRawValue()));
        }
        data.insertValue("hosting_port", SabValue.fromInt(hostingPort));
        return data;
    }

    private List<Setting<?>> setFrom(SabData data) {
        List<Setting<?>> unloaded = new ArrayList<>();

        for (Setting<?> setting : settings) {
            if (data.hasValue(setting.id) && setting.isValid(data.getValue(setting.id).getRawValue())) {
                setting.set(data.getValue(setting.id).getRawValue());
            } else {
                unloaded.add(setting);
            }
        }
        if (data.hasValue("hosting_port")) {
            try {
                hostingPort = Integer.parseInt(data.getValue("hosting_port").getRawValue());
            } catch (NumberFormatException ignored) {

            }
        }

        return unloaded;
    }
}
