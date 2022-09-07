package sab.game.fighters;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.Peck;
import sab.net.Keys;

public class BigSeagull extends FighterType {
    private Animation hoverAnimation;
    private Animation flyingAnimation;
    private Animation peckAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "big_seagull";
        fighter.name = "Big Seagull";
        fighter.hitboxWidth = 72;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 96;
        fighter.renderHeight = 96;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 8;
        fighter.frames = 19;
        fighter.speed = 14f;
        fighter.acceleration = .666f;
        fighter.jumpHeight = 160;
        fighter.doubleJumpMultiplier = .5f;
        fighter.friction = .1f;
        fighter.mass = 6.66f;
        fighter.jumps = 5;
        fighter.walkAnimation = new Animation(1, 6, 6, true);
        fighter.description = "Big Seagull is a True God so powerful that even other deities cannot survive its presence. Although Big Seagull has followers from the Church of Big Seagull, it does not need to be worshipped to sustain itself, unlike many other gods.";
        fighter.debut = "Real Life";
        fighter.costumes = 4;

        flyingAnimation = new Animation(7, 10, 8, true);
        hoverAnimation = new Animation(new int[] {13, 16, 17, 18}, 10, true);
        peckAnimation = new Animation(11, 12, 6, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (Math.abs(player.velocity.x) < 0.1f && player.velocity.y == 0 && player.keys.isPressed(Keys.DOWN)) {
            player.frame = 1;
        }

        if (!player.touchingStage) {
            if (player.velocity.len() < 10) {
                player.frame = hoverAnimation.stepLooping();
            } else {
                player.frame = flyingAnimation.stepLooping();
            }
            float targetRotation = (float) Math.toDegrees(Math.atan2(player.velocity.y, Math.abs(player.velocity.x)))
                    * player.direction * Math.abs(player.velocity.x) / 20;
            player.rotation = player.rotation + (targetRotation - player.rotation) * .1f;
        } else {
            flyingAnimation.reset();
            hoverAnimation.reset();
            player.rotation = 0;
        }

        if (player.velocity.y < 0) player.velocity.y *= 0.95f;
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {

    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        peckAnimation.reset();
        player.startAttack(new Attack(new Peck(), player), peckAnimation, 1, 5, true);
    }
}