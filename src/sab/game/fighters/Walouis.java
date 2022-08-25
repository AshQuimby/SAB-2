package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;

public class Walouis extends FighterType {

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "walouis";
        fighter.name = "Walouis";
        fighter.hitboxWidth = 44;
        fighter.hitboxHeight = 88;
        fighter.renderWidth = 64;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 8;
        fighter.imageOffsetY = 4;
        fighter.frames = 11;
        fighter.jumps = 1;
        fighter.speed = 0.6f;
        fighter.jumpHeight = 160;
        fighter.friction = .05f;
        fighter.mass = 50f;
        fighter.walkAnimation = new Animation(0, 4, 5, true);
        fighter.description = "    Walouis, the world famous jazz"
        + "\nmusician and singer is known"
        + "\nfor his smash hit albums such as:"
        + "\n'Waaht is Love', 'A Wah's Life',"
        + "\nand 'Waaghing in the shadows.'"
        + "\nHe also sees sucess in his"
        + "\nprofessional badminton career." 
        + "\n"
        + "\nDebut: Marvin Badminton";
        fighter.costumes = 3;
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        
    }
}
