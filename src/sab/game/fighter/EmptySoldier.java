package sab.game.fighter;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.chain.ChainSlash;

public class EmptySoldier extends FighterType {
    private int spirit;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "empty_soldier";
        fighter.name = "Empty Soldier";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 64;
        fighter.renderWidth = 64;
        fighter.renderHeight = 68;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.jumps = 1;
        fighter.speed = 12.2f;
        fighter.acceleration = 1f;
        fighter.jumpHeight = 176;
        fighter.frames = 13;
        fighter.friction = .3f;
        fighter.mass = 2.41f;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.costumes = 3;
        fighter.description = "";
        fighter.debut = "Container";
        fighter.freefallAnimation = new Animation(new int[] { 3 }, 1, false);

        spirit = 0;
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startAttack(new Attack(new ChainSlash(), player), null, 6, 8, false);
        }
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (spirit >= 50) {
            player.velocity.y = 50;
            spirit -= 50;
        }
    }
}