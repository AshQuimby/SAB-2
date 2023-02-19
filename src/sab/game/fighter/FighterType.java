package sab.game.fighter;

import sab.game.Player;

public abstract class FighterType { 
    public void setDefaults(sab.game.fighter.Fighter fighter) {
    }

    public void update(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void chargeAttack(sab.game.fighter.Fighter fighter, Player player, int charge) {

    }

    public void neutralSpecial(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void sideSpecial(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void downSpecial(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void upSpecial(sab.game.fighter.Fighter fighter, Player player) {

    }

    public void charging(sab.game.fighter.Fighter fighter, Player player, int charge) {

    }

    public void onHit(Fighter fighter, Player player) {

    }
}