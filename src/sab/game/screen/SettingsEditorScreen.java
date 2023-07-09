package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.game.InputState;
import sab.game.SabSounds;
import sab.game.settings.Setting;
import sab.game.settings.Settings;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class SettingsEditorScreen extends ScreenAdapter {
    private final Setting<?>[] settings;
    private int index;
    private final InputState input;
    private int ticksSinceKeyPress;

    public SettingsEditorScreen(Setting<?>... settings) {
        this.settings = settings;
        input = new InputState(2);
    }

    private Screen onBack() {
        Settings.localSettings.save();
        return new SettingsMenuScreen();
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE) {
            SabSounds.playSound(SabSounds.BLIP);
            return onBack();
        } else if (keyCode == Input.Keys.ENTER) {
            if (index == settings.length) {
                SabSounds.playSound(SabSounds.SELECT);
                return onBack();
            }
        } else if (keyCode == Input.Keys.UP) {
            ticksSinceKeyPress = 0;
            SabSounds.playSound(SabSounds.BLIP);
            index = Utils.loop(index, -1, settings.length + 1, 0);
        } else if (keyCode == Input.Keys.DOWN) {
            ticksSinceKeyPress = 0;
            SabSounds.playSound(SabSounds.BLIP);
            index = Utils.loop(index, 1, settings.length + 1, 0);
        } else if (keyCode == Input.Keys.LEFT) {
            input.press(0);
            ticksSinceKeyPress = 0;
            SabSounds.playSound(SabSounds.BLIP);
            settings[index].previous();
        } else if (keyCode == Input.Keys.RIGHT) {
            input.press(1);
            ticksSinceKeyPress = 0;
            SabSounds.playSound(SabSounds.BLIP);
            settings[index].next();
        }

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.LEFT) {
            input.release(0);
        } else if (keyCode == Input.Keys.RIGHT) {
            input.release(1);
        }

        return this;
    }

    @Override
    public Screen update() {
        if (index == settings.length) {
        } else if (!settings[index].isDiscrete()) {
            if (input.isPressed(0) && ticksSinceKeyPress > 30 && ticksSinceKeyPress % 3 == 0) {
                SabSounds.playSound(SabSounds.BLIP);
                settings[index].previous();
            }
            if (input.isPressed(1) && ticksSinceKeyPress > 30 && ticksSinceKeyPress % 3 == 0) {
                SabSounds.playSound(SabSounds.BLIP);
                settings[index].next();
            }
        }

        ticksSinceKeyPress++;
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        g.scalableDraw(g.imageProvider.getImage("settings_screen_background.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        int finalY = 0;
        for (int i = 0; i < settings.length; i++) {
            Setting<?> setting = settings[i];
            finalY = i * -58 + 192;
            Utils.drawButton(g, 0, finalY, setting.name + " - " + setting.display(), Game.getDefaultFontScale() * 1.5f, i == index, 0);
        }
        finalY -= 58;
        Utils.drawButton(g, 0, finalY, "Back", Game.getDefaultFontScale() * 1.5f, index == settings.length, 0);

        g.drawText("[!] = Restart Required", g.imageProvider.getFont(Settings.localSettings.font.asRawValue()), 0, -320, Game.getDefaultFontScale(), Color.WHITE, 0);
    }

    @Override
    public void close() {
        Settings.localSettings.save();
    }
}
