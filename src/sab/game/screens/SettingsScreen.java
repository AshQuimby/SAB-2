package sab.game.screens;

import java.util.HashMap;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.SABSounds;
import sab.game.Settings;
import sab.screen.Screen;

public class SettingsScreen extends SelectorScreen {
    
    HashMap<String, String> settings;

    public SettingsScreen() {
        super(new String[] {"Master Volume", "Music Volume", "SFX Volume", "Back"});
        settings = Settings.toHashMap();
    }
    
    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("settings_screen_background.png"), -1152 / 2, -704 / 2, 1152, 704);

        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), 1152 / 2 - 400, -704 / 2, 400, 350, 0, 1, 0, false, false, new Color(0, 0, 0, 0.5f));

        for (int i = 0; i < options.length; i++) {
            Rectangle bounds = g.drawText(options[i], g.imageProvider.getFont("SAB_font"), 1152 / 2 - 8,  i * -52 - 16, 1.5f, Color.WHITE, 1);

            float color = i == selectorId ? 1f : 0;

            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
                    new Color(color, color, color, 0.5f));

            g.drawText(options[i], g.imageProvider.getFont("SAB_font"),  1152 / 2 - 8,  i * -52 - 16, 1.5f, Color.WHITE, 1);
        }

        String setting = "";

        float textSize = 2f;

        switch(selectorId) {
            case 0 -> {
                setting = (int) (Float.parseFloat(settings.get("master_volume")) * 100) + "%";
            }
            case 1 -> {
                setting = (int) (Float.parseFloat(settings.get("music_volume")) * 100) + "%";
            }
            case 2 -> {
                setting = (int) (Float.parseFloat(settings.get("sfx_volume")) * 100) + "%";
            }
            default -> {
                setting = "Save Settings";
                textSize = 1.5f;
            }
        }
        Rectangle bounds = g.drawText(setting, g.imageProvider.getFont("SAB_font"), 0, 64, textSize, Color.WHITE, 0);

        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), bounds.x - 4, bounds.y + 4, (int) bounds.width + 9, (int) -bounds.height - 9, 1, 0, 0, false, false,
                new Color(0, 0, 0, 0.5f));

        g.drawText(setting, g.imageProvider.getFont("SAB_font"),  0, 64, textSize, Color.WHITE, 0);
        
    }

    @Override
    public Screen keyPressed(int keyCode) {

        if (keyCode == Input.Keys.RIGHT) increase();

        if (keyCode == Input.Keys.LEFT) decrease();
        
        return super.keyPressed(keyCode);
    }

    public void increase() {
        switch (selectorId) {
            case 0 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("master_volume", Math.min(100, (int) (Float.parseFloat(settings.get("master_volume")) * 100) + 5) / 100f + "");
            }
            case 1 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("music_volume", Math.min(100, (int) (Float.parseFloat(settings.get("music_volume")) * 100) + 5) / 100f + "");
            }
            case 2 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("sfx_volume", Math.min(100, (int) (Float.parseFloat(settings.get("sfx_volume")) * 100) + 5) / 100f + "");
            }
            default -> {

            }
        }
    }

    public void decrease() {
        switch (selectorId) {
            case 0 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("master_volume", Math.max(0, (int) (Float.parseFloat(settings.get("master_volume")) * 100) - 5) / 100f + "");
            }
            case 1 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("music_volume", Math.max(0, (int) (Float.parseFloat(settings.get("music_volume")) * 100) - 5) / 100f + "");
            }
            case 2 -> {
                SABSounds.playSound(SABSounds.BLIP);
                settings.replace("sfx_volume", Math.max(0, (int) (Float.parseFloat(settings.get("sfx_volume")) * 100) - 5) / 100f + "");
            }
            default -> {

            }
        }
    }

    @Override
    public Screen onSelect(int selection) {
        switch(selection) {
            case 0 -> {

            }
            case 1 -> {
                
            }
            case 2 -> {

            }
            case 3 -> {
                Settings.fromHashMap(settings);
                SABSounds.soundEngine.setCurrentMusicVolume(Settings.getMusicVolume() * Settings.getMasterVolume());
                Settings.writeFile();
                SABSounds.playSound(SABSounds.SELECT);
                return new TitleScreen(false);
            }
            default -> {

            }
        }

        return this;
    }
}