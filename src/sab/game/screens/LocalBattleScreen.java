package sab.game.screens;

import com.badlogic.gdx.Input;
import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.Game;
import sab.game.SABSounds;
import sab.game.fighters.Fighter;
import sab.game.stages.Stage;
import sab.net.Keys;
import sab.screen.*;

public class LocalBattleScreen extends ScreenAdapter {
    private Battle battle;

    public LocalBattleScreen(Fighter player1, Fighter player2, int[] costumes, Stage stage) {
        battle = new Battle(player1, player2, costumes, stage);
        SABSounds.playMusic(battle.getStage().music, true);
    }

    @Override
    public Screen keyPressed(int keyCode) {
        if (keyCode == Input.Keys.W) {
            battle.getPlayer(0).keys.press(Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            battle.getPlayer(0).keys.press(Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            battle.getPlayer(0).keys.press(Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            battle.getPlayer(0).keys.press(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F) {
            battle.getPlayer(0).keys.press(Keys.ATTACK);
        }

        if (keyCode == Input.Keys.UP) {
            battle.getPlayer(1).keys.press(Keys.UP);
        }
        if (keyCode == Input.Keys.LEFT) {
            battle.getPlayer(1).keys.press(Keys.LEFT);
        }
        if (keyCode == Input.Keys.DOWN) {
            battle.getPlayer(1).keys.press(Keys.DOWN);
        }
        if (keyCode == Input.Keys.RIGHT) {
            battle.getPlayer(1).keys.press(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.M) {
            battle.getPlayer(1).keys.press(Keys.ATTACK);
        }

        if (keyCode == Input.Keys.ESCAPE || keyCode == Input.Keys.SHIFT_RIGHT) battle.togglePause();

        // if (keyCode == Input.Keys.ENTER) if (battle.onSelect()) return Game.game.globalCharacterSelectScreen;

        return this;
    }

    @Override
    public Screen keyReleased(int keyCode) {
        if (keyCode == Input.Keys.W) {
            battle.getPlayer(0).keys.release(Keys.UP);
        }
        if (keyCode == Input.Keys.A) {
            battle.getPlayer(0).keys.release(Keys.LEFT);
        }
        if (keyCode == Input.Keys.S) {
            battle.getPlayer(0).keys.release(Keys.DOWN);
        }
        if (keyCode == Input.Keys.D) {
            battle.getPlayer(0).keys.release(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.F) {
            battle.getPlayer(0).keys.release(Keys.ATTACK);
        }

        if (keyCode == Input.Keys.UP) {
            battle.getPlayer(1).keys.release(Keys.UP);
        }
        if (keyCode == Input.Keys.LEFT) {
            battle.getPlayer(1).keys.release(Keys.LEFT);
        }
        if (keyCode == Input.Keys.DOWN) {
            battle.getPlayer(1).keys.release(Keys.DOWN);
        }
        if (keyCode == Input.Keys.RIGHT) {
            battle.getPlayer(1).keys.release(Keys.RIGHT);
        }
        if (keyCode == Input.Keys.M) {
            battle.getPlayer(1).keys.release(Keys.ATTACK);
        }

        return this;
    }

    @Override
    public Screen update() {
        if (battle.gameEnded) {
            return Game.game.globalCharacterSelectScreen;
        }
        battle.update();
        return this;
    }

    @Override
    public void render(Seagraphics g) {
        battle.render(g);
    }
}