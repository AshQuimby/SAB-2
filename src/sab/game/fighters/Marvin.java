package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Wrench;
import sab.game.attacks.projectiles.Fireball;

public class Marvin extends FighterType {
    private Animation swingAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "marvin";
        fighter.name = "Marvin";
        fighter.costumes = 0;
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 9;
        fighter.speed = 0.6f;
        fighter.jumpHeight = 160;
        fighter.friction = .05f;
        fighter.mass = 50f;
        fighter.walkAnimation = new Animation(0, 4, 5, true);
        fighter.description = "    Retired Albany plumber now"
        + "\nprincess saving daydreamer,"
        + "\nMarvin is a troubled man "
        + "\nwho is always distracted"
        + "\nby his dreams about the "
        + "\nthings he could be. He can't"
        + "\nseem to escape from his brother's" 
        + "\nsucess as a musician."
        + "\n"
        + "\nDebut: Super Marvin Plumber";

        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new Fireball(), player), swingAnimation, 4, 10);
        player.velocity.y /= 3;
        player.velocity.x *= 0.9f;
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new Attack(new Wrench(), player), swingAnimation, 8, 9);
    }
}
