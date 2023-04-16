package sab.game.fighter;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.chain.ChainSlash;
import sab.game.attack.empty_soldier.ShadowPlunge;

public class EmptySoldier extends FighterType {
    private int spirit;
    private Animation swingAnimation;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "empty_soldier";
        fighter.name = "The Soldier";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 64;
        fighter.renderWidth = 64;
        fighter.renderHeight = 68;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.airJumps = 1;
        fighter.speed = 12.2f;
        fighter.acceleration = 1f;
        fighter.jumpHeight = 176;
        fighter.frames = 13;
        fighter.friction = .3f;
        fighter.mass = 4.13f;
        fighter.walkAnimation = new Animation(1, 3, 5, true);
        fighter.costumes = 3;
        fighter.description = "";
        fighter.debut = "Empty Soldier";
        fighter.freefallAnimation = new Animation(new int[] { 7 }, 1, false);

        spirit = 0;
        swingAnimation = new Animation(new int[] {5, 4}, 10, true);
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (!player.hasAction() && !player.touchingStage) {
            player.frame = player.velocity.y > 0 ? 6 : 7;
        }
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new ChainSlash(), swingAnimation, 6, 8, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (spirit >= 50) {
            player.velocity.y = 50;
            spirit -= 50;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.touchingStage) player.startIndefiniteAttack(new ShadowPlunge(), null, 10, false);
    }
}