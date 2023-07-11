package sab.modloader;

import com.seagull_engine.Seagraphics;
import sab.game.Battle;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.ass_ball.AssBall;
import sab.game.attack.Attack;
import sab.game.stage.StageObject;

public abstract class ModBattle {
    protected final Battle battle;
    public ModBattle(Battle battle) {
        this.battle = battle;
    }
    public void onStart() {
    }
    public void update(boolean timeFrozen) {
    }
    public void onGameEnd() {
    }
    public void onPlayerSpawn(Player player) {
    }
    public void onParry(DamageSource source, Player player) {
    }

    /**
     * Runs when a player is hit by an attack or other damage source
     * @param source
     * The damage source. Owner is not guaranteed to be non-null
     * @param player
     * The player that was hit
     * @return
     * False to prevent the player from taking damage. Default true.
     */
    public boolean preOnPlayerHit(DamageSource source, Player player) {
        return true;
    }
    /**
     * Runs when a player is hit by an attack or other damage source
     * @param source
     * The damage source. Owner is not guaranteed to be non-null
     * @param player
     * The player that was hit
     */
    public void onPlayerHit(DamageSource source, Player player) {
    }
    public void onPlayerKilled(Player player) {
    }
    public void onAssBallSpawn(AssBall assBall) {
    }
    public void onAttackSpawned(Attack attack) {
    }
    public void onStageObjectSpawned(StageObject stageObject) {
    }
    public void onScreenShatter() {
    }
    /**
     * Renders after the background, by default uses static camera
     * @param g
     * The graphics object
     */
    public void renderBackground(Seagraphics g) {
    }
    /**
     * Renders before attacks, by default uses dynamic camera
     * @param g
     * The graphics object
     */
    public void renderBeforeAttacks(Seagraphics g) {
    }
    /**
     * Renders before players, by default uses dynamic camera
     * @param g
     * The graphics object
     */
    public void renderBeforePlayers(Seagraphics g) {
    }
    /**
     * Renders before platforms, by default uses dynamic camera
     * @param g
     * The graphics object
     */
    public void renderBeforePlatforms(Seagraphics g) {
    }
    /**
     * Renders before particles, by default uses dynamic camera
     * @param g
     * The graphics object
     */
    public void renderBeforeParticles(Seagraphics g) {
    }
    /**
     * Renders before the UI & HUD elements, by default uses static camera
     * @param g
     * The graphics object
     */
    public void renderBeforeUI(Seagraphics g) {
    }
    /**
     * Renders after the UI & HUD elements, by default uses static camera
     * @param g
     * The graphics object
     */
    public void renderAfterUI(Seagraphics g) {
    }

    /**
     * Renders after everything else, by default uses static camera
     * @param g
     * The graphics object
     */
    public void renderAfterAll(Seagraphics g) {
    }
}
