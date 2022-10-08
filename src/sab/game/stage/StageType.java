package sab.game.stage;

import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.DamageSource;
import sab.game.Player;

public abstract class StageType {
    public abstract void init(sab.game.stage.Stage stage);

    public void update(Battle battle, sab.game.stage.Stage stage) {
    }

    public void onPlayerHit(sab.game.stage.Stage stage, Player player, DamageSource damageSource, boolean finishingBlow) {
    }

    public void renderBackground(sab.game.stage.Stage stage, Seagraphics g) {
    }

    public void renderOverlay(Stage stage, Seagraphics g) {
    }
}