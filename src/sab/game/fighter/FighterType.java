package sab.game.fighter;

import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.screen.VictoryScreen;

public abstract class FighterType {
    public void setDefaults(Fighter fighter) {

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

    public void neutralSpecial(Fighter fighter, Player player) {

    }

    public void sideSpecial(Fighter fighter, Player player) {

    }

    public void downSpecial(Fighter fighter, Player player) {

    }

    public void upSpecial(Fighter fighter, Player player) {

    }

    public void charging(Fighter fighter, Player player, int charge) {

    }

    public void onHit(Fighter fighter, Player player) {

    }

    public void useItem(Fighter fighter, Player player) {

    }

    public void onKill(Fighter fighter, Player player) {

    }

    public boolean preRender(Fighter fighter, Player player, Seagraphics g) {
        return true;
    }

    public void render(Fighter fighter, Player player, Seagraphics g) {

    }

    public void postRender(Fighter fighter, Player player, Seagraphics g) {

    }

    public void renderUI(Fighter fighter, Player player, Seagraphics g) {

    }

    public String getVictorySongId(Fighter fighter, Player player) {
        return fighter.id + "_victory.mp3";
    }

    public void renderVictoryScreen(Fighter fighter, Player player, Player opponent, VictoryScreen screen, Seagraphics g) {
    }
}