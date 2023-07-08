package sab.game.screen;

import com.seagull_engine.Seagraphics;

import sab.game.SabSounds;
import sab.game.settings.Settings;
import sab.game.Game;
import sab.screen.Screen;
import sab.util.Utils;

public class SettingsMenuScreen extends SelectorScreen {
    public SettingsMenuScreen() {
        super(new String[] {"Gameplay", "Video", "Audio", "Save Settings"});
    }

    @Override
    protected Screen onBack() {
        SabSounds.resetCurrentMusicVolume();
        Settings.localSettings.save();
        if (Settings.localSettings.fullscreen.value) {
            Game.game.goFullscreened();
        } else {
            Game.game.goWindowed();
        }
        return new TitleScreen(false);
    }

    @Override
    protected Screen onSelect(int selection) {
        super.onSelect(selection);
        return switch (selection) {
            case 0 -> new SettingsEditorScreen(Settings.localSettings.gameplaySettings);
            case 1 -> new SettingsEditorScreen(Settings.localSettings.videoSettings);
            case 2 -> new SettingsEditorScreen(Settings.localSettings.audioSettings);
            default -> onBack();
        };
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("settings_screen_background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        for (int i = 0; i < options.length; i++) {
            String option = options[i];
            Utils.drawButton(g, 0, i * -58 + 116, option, Game.getDefaultFontScale() * 1.5f, i == selectorIndex);
        }
    }
}