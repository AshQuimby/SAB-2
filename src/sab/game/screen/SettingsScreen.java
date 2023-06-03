package sab.game.screen;

import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.SABSounds;
import sab.game.Settings;
import sab.game.Game;
import sab.screen.Screen;
import sab.util.Utils;

public class SettingsScreen extends SelectorScreen {
    private HashMap<String, String> settings;
    private String[][] subSelection;
    private String[][] subSelectionSettingIds;
    private int subSelectionIndex;
    private boolean inSubSelection;

    public SettingsScreen() {
        super(new String[] { "Game Settings", "Audio Settings", "Video Settings", "Back" });
        exitSubSelection();
        subSelection = new String[][] {
                new String[] { "Stage Hazards", "Spawn Ass Balls", "Victory Anticipation", "Debug Mode", "Back" },
                new String[] { "Master Volume", "Music Volume", "SFX Volume", "Back" },
                new String[] { "Default Fullscreened", "Static Camera", "Screen Shake", "Player Arrows", "Font", "Back" }
        };
        subSelectionSettingIds = new String[][] {
                new String[] { "stage_hazards", "ass_balls", "anticipation", "debug_mode" },
                new String[] { "master_volume", "music_volume", "sfx_volume" },
                new String[] { "fullscreen", "static_camera", "screen_shake", "draw_player_arrows", "font" },
        };
        settings = Settings.toHashMap();

    }

    public void toggleSubSelection() {
        inSubSelection = !inSubSelection;
    }

    public void enterSubSelection() {
        inSubSelection = true;
    }

    public void exitSubSelection() {
        inSubSelection = false;
    }

    @Override
    public Screen keyPressed(int keyCode) {

        if (keyCode == Input.Keys.RIGHT) increase();

        else if (keyCode == Input.Keys.LEFT) decrease();

        else if (keyCode == Input.Keys.ENTER) {
            if (selectorIndex == 3 || subSelectionIndex == subSelectionSettingIds[selectorIndex].length) {
                SABSounds.playSound(SABSounds.SELECT);
                return onBack();
            } else {
                if (inSubSelection) {
                    exitSubSelection();
                    SABSounds.playSound("deselect.mp3");
                } else {
                    enterSubSelection();
                    SABSounds.playSound("select.mp3");
                }
            }
        }
        
        return super.keyPressed(keyCode);
    }

    @Override
    public void incrementSelection() {
        if (inSubSelection) {
            SABSounds.playSound(SABSounds.BLIP);
            subSelectionIndex = (subSelectionIndex + 1) % subSelection[selectorIndex].length;
        } else {
//            super.incrementSelection();
        }
    }

    @Override
    public void decrementSelection() {
        if (inSubSelection) {
            SABSounds.playSound(SABSounds.BLIP);
            subSelectionIndex = (subSelectionIndex - 1) % subSelection[selectorIndex].length;
            System.out.println(-1 % 4);
        } else {
//            super.decrementSelection();
        }
    }

    public void increase() {
        if (inSubSelection) {
            if (subSelectionIndex < subSelectionSettingIds[selectorIndex].length) {
                String key = subSelectionSettingIds[selectorIndex][subSelectionIndex];
                if (selectorIndex == 0 || selectorIndex == 2) {
                    if (selectorIndex == 2 && subSelectionIndex == 4) {
                        switch (settings.get(key)) {
                            case "SAB_font" :
                                settings.replace(key, "comic_snas");
                                break;
                            case "comic_snas" :
                                settings.replace(key, "SAB_font");
                                break;
                        }
                    } else {
                        settings.replace(key, "true");
                    }
                } else {
                    settings.replace(key, "" + Math.min(Float.parseFloat(settings.get(key)) + 0.05f, 1));
                }
                SABSounds.playSound(SABSounds.BLIP);
            }
        } else {
            super.incrementSelection();
        }
//        switch (selectorIndex) {
//            case 0 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("static_camera", "true");
//            }
//            case 1 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("master_volume", Math.min(100, (int) (Float.parseFloat(settings.get("master_volume")) * 100) + 5) / 100f + "");
//            }
//            case 2 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("music_volume", Math.min(100, (int) (Float.parseFloat(settings.get("music_volume")) * 100) + 5) / 100f + "");
//            }
//            case 3 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("sfx_volume", Math.min(100, (int) (Float.parseFloat(settings.get("sfx_volume")) * 100) + 5) / 100f + "");
//            }
//            default -> {
//
//            }
//        }
    }

    public void decrease() {
        if (inSubSelection) {
            if (subSelectionIndex < subSelectionSettingIds[selectorIndex].length) {
                String key = subSelectionSettingIds[selectorIndex][subSelectionIndex];
                if (selectorIndex == 0 || selectorIndex == 2) {
                    if (selectorIndex == 2 && subSelectionIndex == 4) {
                        switch (settings.get(key)) {
                            case "SAB_font" :
                                settings.replace(key, "comic_snas");
                                break;
                            case "comic_snas" :
                                settings.replace(key, "SAB_font");
                                break;
                        }
                    } else {
                        settings.replace(key, "false");
                    }
                } else {
                    settings.replace(key, "" + Math.max(Float.parseFloat(settings.get(key)) - 0.05f, 0));
                }
                SABSounds.playSound(SABSounds.BLIP);
            }
        } else {
            super.decrementSelection();
        }
//        switch (selectorIndex) {
//            case 0 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("static_camera", "false");
//            }
//            case 1 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("master_volume", Math.max(0, (int) (Float.parseFloat(settings.get("master_volume")) * 100) - 5) / 100f + "");
//            }
//            case 2 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("music_volume", Math.max(0, (int) (Float.parseFloat(settings.get("music_volume")) * 100) - 5) / 100f + "");
//            }
//            case 3 -> {
//                SABSounds.playSound(SABSounds.BLIP);
//                settings.replace("sfx_volume", Math.max(0, (int) (Float.parseFloat(settings.get("sfx_volume")) * 100) - 5) / 100f + "");
//            }
//            default -> {
//
//            }
//        }
    }

    @Override
    public Screen onSelect(int selection) {
        if (inSubSelection) {
            switch (subSelectionIndex) {
                case 4:
                    return onBack();
                default:
            }
        }

        return this;
    }

    @Override
    protected Screen onBack() {
        Settings.fromHashMap(settings);
        SABSounds.soundEngine.setCurrentMusicVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
        Settings.writeFile();
        if (Settings.getFullscreen()) {
            Game.game.goFullscreened();
        } else {
            Game.game.goWindowed();
        }
        return new TitleScreen(false);
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("settings_screen_background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        String title = options[selectorIndex];
        Rectangle bounds = g.getTextBounds(title, Game.getDefaultFont(), 0, 320, 2 * Game.getDefaultFontScale(), 0);
        //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));
        //g.drawText(title, Game.getDefaultFont(), 0, 320, 2 * Game.getDefaultFontScale(), Color.WHITE, 0);
        Utils.drawButton(g, 24, 288, title, 2 * Game.getDefaultFontScale(), inSubSelection);
        if (inSubSelection) {
            //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 0, 1, 0, false, false, new Color(0, 0, 0, 0.25f));
        }
//        for (int i = 0; i < options.length; i++) {
//            Rectangle bounds = g.drawText(options[i], Game.getDefaultFont(), Game.game.window.resolutionX / 2 - 8,  i * -52 - 16, 1.5f, Color.WHITE, 1);
//
//            float color = i == selectorIndex ? 1f : 0;
//
//            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
//                    new Color(color, color, color, 0.5f));
//
//            g.drawText(options[i], Game.getDefaultFont(),  Game.game.window.resolutionX / 2 - 8,  i * -52 - 16, 1.5f, Color.WHITE, 1);
//        }

        String setting = "";

        float textSize = 1.75f;

        if (selectorIndex >= subSelectionSettingIds.length || subSelectionIndex >= subSelectionSettingIds[selectorIndex].length) {
            setting = "Save Settings";
        } else {
            setting = settings.get(subSelectionSettingIds[selectorIndex][subSelectionIndex]);
            if (selectorIndex == 1) {
                setting = Math.round(Float.parseFloat(setting) * 100) + "%";
            } else if (selectorIndex == 2 && subSelectionIndex == 4) {
                switch (settings.get("font")) {
                    case "SAB_font" :
                        setting = "SAB Font";
                        break;
                    case "arial" :
                        setting = "Arial";
                        break;
                    case "comic_snas" :
                        setting = "Comic Snas";
                        break;
                }
            } else {
                setting = ("" + setting.charAt(0)).toUpperCase() + setting.substring(1);
            }
        }

        if (selectorIndex < subSelectionSettingIds.length) {
            //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), Game.game.window.resolutionX / 2 - 500, -Game.game.window.resolutionY / 2, 500, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));
            for (int i = 0; i < subSelection[selectorIndex].length; i++) {
                String settingName = subSelection[selectorIndex][i];
                int x = Game.game.window.resolutionX / 2 - 16;
                int y = -60 - i * 40;
//                bounds = g.getTextBounds(settingName, Game.getDefaultFont(), x, y, 1.25f * Game.getDefaultFontScale(), 1);
//                Color buttonColor = subSelectionIndex == i ? new Color(1, 1, 1, 0.5f) : new Color(0, 0, 0, 0.5f);
                //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 0, 1, 0, false, false, buttonColor);
                //g.drawText(settingName, Game.getDefaultFont(), x, y, 1.25f * Game.getDefaultFontScale(), Color.WHITE, 1);
                Utils.drawButton(g, x, y, settingName, 1.25f * Game.getDefaultFontScale(), inSubSelection && subSelectionIndex == i, 1);
            }
            if (!inSubSelection) {
                //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), Game.game.window.resolutionX / 2 - 500, -Game.game.window.resolutionY / 2, 500, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.25f));
            }
        }


        bounds = g.getTextBounds(setting, Game.getDefaultFont(), 0, 0, textSize * Game.getDefaultFontScale(), 0);

        //g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
        //        new Color(0, 0, 0, 0.5f));

        //g.drawText(setting, Game.getDefaultFont(),  0, 0, textSize * Game.getDefaultFontScale(), Color.WHITE, 0);
        Utils.drawButton(g, 0, 0, setting, 1.25f * Game.getDefaultFontScale(), true);

    }
}