package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Wrench;
import sab.game.attacks.projectiles.Fireball;

public class Walouis extends FighterType {
    private Animation swingAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "walouis";
        fighter.name = "Walouis";
        fighter.costumes = 0;
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 11;
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

        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
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
