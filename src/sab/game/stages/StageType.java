package sab.game.stages;

import com.seagull_engine.Seagraphics;

import sab.game.Battle;
import sab.game.DamageSource;
import sab.game.Player;

public abstract class StageType {
    public abstract void init(Stage stage);

    public void update(Battle battle, Stage stage) {
    }

    public void onPlayerHit(Stage stage, Player player, DamageSource damageSource, boolean finishingBlow) {
    }

    public void renderBackground(Stage stage, Seagraphics g) {
    }

    public void renderOverlay(Stage stage, Seagraphics g) {
    }
}