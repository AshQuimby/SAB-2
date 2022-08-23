package sab.game.fighters;

import sab.game.Player;

public abstract class FighterType { 
    public void setDefaults(Fighter fighter) {
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
}