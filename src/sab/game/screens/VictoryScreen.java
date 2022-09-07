package sab.game.screens;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.Settings;
import sab.game.fighters.Fighter;
import sab.game.fighters.FighterType;
import sab.modloader.ModLoader;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;
import sab.util.Utils;

public class VictoryScreen extends ScreenAdapter {
    private Player winner;
    private Player loser;
    private int setupTimer;

    public VictoryScreen(Player winner, Player loser) {
        this.winner = winner;
        this.loser = loser;
        SABSounds.playMusic("leadup.mp3", false);
        setupTimer = -360;
        if (Settings.getMusicVolume() == 0 || Settings.getMasterVolume() == 0) setupTimer = -60;
    }

    @Override
    public void render(Seagraphics g) {
        setupTimer++;

        if (setupTimer == 0) {
            SABSounds.playMusic(winner.fighter.id + "_victory.mp3", false);
        }

        g.scalableDraw(g.imageProvider.getImage("victory_background_layer_1.png"), -1152 / 2, -704 / 2, 1152, 704);

        g.scalableDraw(g.imageProvider.getImage(winner.fighter.id + "_render.png"), 1152 / 2 - 512 - 8, -704 / 2 - 8, 512, 512);

        g.scalableDraw(g.imageProvider.getImage("victory_background_layer_2.png"), -1152 / 2, -704 / 2, 1152, 704);

        if (winner.fighter.name.equals("Tie")) {
            g.drawText("TIE GAME!", g.imageProvider.getFont("SAB_font"), 0, 704 / 2 - 64, 3, Color.WHITE, 0);
        }else {
            g.drawText(winner.fighter.name.toUpperCase() + " WINS!", g.imageProvider.getFont("SAB_font"), 0, 704 / 2 - 64, 3, Color.WHITE, 0);
        }

        g.drawText(winner.gameStats.toString(), g.imageProvider.getFont("SAB_font"), -1152 / 2 + 32, 704 / 2 - 220, 1, Color.WHITE, -1);

        g.drawText(loser.gameStats.toString(), g.imageProvider.getFont("SAB_font"), -1152 / 2 + 256 + 64, 704 / 2 - 220 - 128, 1, Color.WHITE, -1);
    
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -1152 / 2, -704 / 2, 1152, 704, 0, 1, 0, false, false, setupTimer < 0 ? new Color(0, 0, 0, 1) : new Color(1, 1, 1, Math.max(Math.min(1f, ((255 - setupTimer * 15f) / 255f)), 0)));
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.ENTER) {
            if (setupTimer > 0) {
                SABSounds.playSound(SABSounds.BLIP);
                SABSounds.playMusic("lobby_music.mp3", true);
                return Game.game.globalCharacterSelectScreen;
            } else {
                setupTimer = 0;
            }
        }
        
        return this;
    }
}
