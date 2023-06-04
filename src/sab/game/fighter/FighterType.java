package sab.game.fighter;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.attack.Attack;
import sab.game.screen.VictoryScreen;

public abstract class FighterType {
    public void setDefaults(Fighter fighter) {

    }

    public void start(Fighter fighter, Player player) {

    }

    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty);
    }

    public void update(Fighter fighter, Player player) {

    }

    public void neutralAttack(Fighter fighter, Player player) {

    }

    public void sideAttack(Fighter fighter, Player player) {

    }

    public void downAttack(Fighter fighter, Player player) {

    }

    public void upAttack(Fighter fighter, Player player) {

    }

    public void chargeAttack(Fighter fighter, Player player, int charge) {

    }

    public boolean finalAss(Fighter fighter, Player player) {
        return true;
    }

    public void neutralSpecial(Fighter fighter, Player player) {

    }

    public void sideSpecial(Fighter fighter, Player player) {

    }

    public void downSpecial(Fighter fighter, Player player) {

    }

    public void upSpecial(Fighter fighter, Player player) {

    }

    public void onEndAction(PlayerAction action, Fighter fighter, Player player) {

    }

    public void charging(Fighter fighter, Player player, int charge) {

    }

    // Return true if the hit should be successful (deal damage)
    public boolean onHit(Fighter fighter, Player player, DamageSource source) {
        return true;
    }

    public void onJump(Fighter fighter, Player player, boolean doubleJump) {

    }

    public void useItem(Fighter fighter, Player player) {

    }

    public void onKill(Fighter fighter, Player player) {

    }

    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
    }

    public boolean preRender(Fighter fighter, Player player, Seagraphics g) {
        return true;
    }

    public void render(Fighter fighter, Player player, Seagraphics g) {
        String costumeString = fighter.id + (player.costume == 0 ? "" : "_alt_" + player.costume) + ".png";
        g.usefulTintDraw(g.imageProvider.getImage(costumeString), player.drawRect.x, player.drawRect.y, (int) player.drawRect.width, (int) player.drawRect.height, player.frame, fighter.frames, player.rotation, player.direction == 1, false, player.getIFrames() / 10 % 2 == 0 ? Color.WHITE : new Color(1, 1, 1, 0.5f));
    }

    public void postRender(Fighter fighter, Player player, Seagraphics g) {

    }

    public void renderUI(Fighter fighter, Player player, Seagraphics g) {

    }

    public void onParry(Fighter fighter, Player player) {

    }

    public void onSuccessfulParry(Fighter fighter, Player player, DamageSource parried) {

    }

    public int getRandomCostume(Fighter fighter) {
        return MathUtils.random(fighter.costumes - 1);
    }

    public String getVictorySongId(Fighter fighter, Player player) {
        return fighter.id + "_victory.mp3";
    }

    public void renderVictoryScreen(Fighter fighter, Player player, Player opponent, VictoryScreen screen, Seagraphics g) {
    }

    public byte[] getData() {
        return null;
    }

    public void setData(byte[] data) {

    }
}