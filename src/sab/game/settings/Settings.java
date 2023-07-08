package sab.game.settings;

import com.sab_format.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Settings {
    // Gameplay
    public static final BoolSetting stageHazards = new BoolSetting("stage_hazards", "Stage Hazards", true);
    public static final BoolSetting assBalls = new BoolSetting("ass_balls", "Spawn Ass Balls", true);
    public static final BoolSetting anticipation = new BoolSetting("anticipation", "Victory Anticipation", true);
    public static final BoolSetting debugMode = new BoolSetting("debug_mode", "Debug Mode", false);

    // Video
    public static final BoolSetting fullscreen = new BoolSetting("fullscreen", "Fullscreen", true);
    public static final BoolSetting staticCamera = new BoolSetting("static_camera", "Static Camera", false);
    public static final BoolSetting screenShake = new BoolSetting("screen_shake", "Screen Shake", true);
    public static final BoolSetting drawPlayerArrows = new BoolSetting("draw_player_arrows", "Player Arrows", false);
    public static final ListSetting font = new ListSetting("font", "Font", 0, new String[] {
            "SAB_font", "comic_snas", "minecraft", "shitfont23", "arial"
    }, new String[] {
            "SAB", "Comic Snas", "Blockbreak", "tfont 23", "Arial"
    });
    public static final BoolSetting crtEffect = new BoolSetting("crt_effect", "CRT Monitor Effect [!]", false);

    // Audio
    public static final PercentageSetting masterVolume = new PercentageSetting("master_volume", "Master Volume", 50);
    public static final PercentageSetting musicVolume = new PercentageSetting("music_volume", "Music Volume", 100);
    public static final PercentageSetting sfxVolume = new PercentageSetting("sfx_volume", "SFX Volume", 100);

    // Multiplayer
    public static int hostingPort;

    public static final Setting<?>[] settings = new Setting[] {
            stageHazards, assBalls, anticipation, debugMode,
            fullscreen, staticCamera, screenShake, drawPlayerArrows, font, crtEffect,
            masterVolume, musicVolume, sfxVolume
    };

    public static final Setting<?>[] gameplaySettings = new Setting[] {
            stageHazards, assBalls, anticipation, screenShake
    };

    public static final Setting<?>[] videoSettings = new Setting[] {
            fullscreen, staticCamera, screenShake, drawPlayerArrows, font, crtEffect
    };

    public static final Setting<?>[] audioSettings = new Setting[] {
            masterVolume, musicVolume, sfxVolume
    };

    public static void resetAll() {
        for (Setting<?> setting : settings) {
            setting.reset();
        }
        hostingPort = 19128;
    }

    public static void save() {
        try {
            SabWriter.write(new File("../settings.sab"), toSabData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load() {
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

    private static SabData toSabData() {
        SabData data = new SabData();
        for (Setting<?> setting : settings) {
            data.insertValue(setting.id, new SabValue(setting.asRawValue()));
        }
        data.insertValue("hosting_port", SabValue.fromInt(hostingPort));
        return data;
    }

    private static List<Setting<?>> setFrom(SabData data) {
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
