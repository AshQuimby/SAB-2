package sab.game.screen;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.seagull_engine.Seagraphics;

import sab.game.Game;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.Settings;
import sab.screen.Screen;
import sab.screen.ScreenAdapter;

import java.util.Set;

public class VictoryScreen extends ScreenAdapter {
    public int setupTimer;
    private Player winner;
    private Player loser;
    private String background;

    public VictoryScreen(Player winner, Player loser, String background) {
        this.winner = winner;
        this.loser = loser;
        this.background = background;
        SABSounds.playMusic("leadup.mp3", false);
        setupTimer = -360;
        if (!Settings.getAnticipation() || Settings.getMusicVolume() == 0 || Settings.getMasterVolume() == 0) setupTimer = -60;
        Game.game.window.camera.viewportWidth = Game.game.window.resolutionX;
        Game.game.window.camera.viewportHeight = Game.game.window.resolutionY;
        Game.game.window.camera.position.x = 0;
        Game.game.window.camera.position.y = 0;
    }

    @Override
    public void render(Seagraphics g) {
        setupTimer++;

        if (setupTimer == 0) {
            SABSounds.playMusic(winner.fighter.type.getVictorySongId(winner.fighter, winner), false);
        }

        g.scalableDraw(g.imageProvider.getImage(background), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage("victory_background_layer_1.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        g.scalableDraw(g.imageProvider.getImage(winner.fighter.id + "_render" + (winner.costume == 0 ? "" : ("_alt_" + winner.costume)) + ".png"), Game.game.window.resolutionX / 2 - 512 - 8, -Game.game.window.resolutionY / 2 - 8, 512, 512);

        g.scalableDraw(g.imageProvider.getImage("victory_background_layer_2.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY);

        if (winner.fighter.name.equals("Tie")) {
            g.drawText("TIE GAME!", Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 64, 3 * Game.getDefaultFontScale(), Color.WHITE, 0);
        }else {
            g.drawText(winner.fighter.name.toUpperCase() + " WINS!", Game.getDefaultFont(), 0, Game.game.window.resolutionY / 2 - 64, 3 * Game.getDefaultFontScale(), Color.WHITE, 0);
        }

        g.drawText(winner.gameStats.toString(), Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 32, Game.game.window.resolutionY / 2 - 220, Game.getDefaultFontScale(), Color.WHITE, -1);

        g.drawText(loser.gameStats.toString(), Game.getDefaultFont(), -Game.game.window.resolutionX / 2 + 256 + 64, Game.game.window.resolutionY / 2 - 220 - 128, Game.getDefaultFontScale(), Color.WHITE, -1);
    
        g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), -Game.game.window.resolutionX / 2, -Game.game.window.resolutionY / 2, Game.game.window.resolutionX, Game.game.window.resolutionY, 0, 1, 0, false, false, setupTimer < 0 ? new Color(0, 0, 0, 1) : new Color(1, 1, 1, Math.max(Math.min(1f, ((255 - setupTimer * 15f) / 255f)), 0)));
        
        winner.fighter.type.renderVictoryScreen(winner.fighter, winner, loser, this, g);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.ENTER) {
            if (setupTimer > 0) {
                SABSounds.playSound(SABSounds.BLIP);
                SABSounds.playMusic("lobby_music.mp3", true);
                Game.selectNewTitleScreen();
                Game.game.globalCharacterSelectScreen.start();
                return Game.game.globalCharacterSelectScreen;
            } else {
                setupTimer = -1;
            }
        }
        
        return this;
    }
}
