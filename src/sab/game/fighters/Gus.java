package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Tongue;
import sab.game.attacks.projectiles.Bullet;

public class Gus extends FighterType {
    private Animation shootAnimation;
    private Animation tongueAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "gus";
        fighter.name = "Gus";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 48;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 8;
        fighter.frames = 12;
        fighter.speed = 0.5f;
        fighter.jumpHeight = 128;
        fighter.friction = .1f;
        fighter.mass = 1f;
        fighter.jumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 7, true);
        fighter.description = "Sus";

        shootAnimation = new Animation(9, 10, 5, true);
        tongueAnimation = new Animation(4, 5, 7, true);
        fighter.costumes = 4;
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        shootAnimation.reset();
        player.startAttack(new Attack(new Bullet(), player), shootAnimation, 5, 5, false);
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        tongueAnimation.reset();
        player.startAttack(new Attack(new Tongue(), player), tongueAnimation, 5, 10, false);
    }
}