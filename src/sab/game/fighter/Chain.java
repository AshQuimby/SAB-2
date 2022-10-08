package sab.game.fighter;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.melees.AirSlash;
import sab.game.attack.melees.ChainSlash;
import sab.game.attack.projectiles.BoomerangKnife;
import sab.game.attack.projectiles.FallingKnife;

public class Chain extends FighterType {
    public boolean hasBoomerang;

    private Animation swingAnimation;
    private Animation flyingAnimation;

    private Attack fallingKnife;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "chain";
        fighter.name = "Chain";
        fighter.hitboxWidth = 32;
        fighter.hitboxHeight = 52;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 6;
        fighter.jumps = 1;
        fighter.speed = 10f;
        fighter.acceleration = .4f;
        fighter.jumpHeight = 128;
        fighter.frames = 9;
        fighter.friction = .07f;
        fighter.mass = 4.36f;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.costumes = 3;
        fighter.description = "Raised in the lands of Mydrule, Chain was trained by the royal guard from a young age to be a murder machine. With his trusty knives and a passion for violence Chain proves that children are still dangerous!";
        fighter.debut = "The Legend of the Tri-Knife";

        hasBoomerang = true;
        swingAnimation = new Animation(new int[] {4, 5, 3}, 5, true);
        flyingAnimation = new Animation(new int[] {6}, 5, true);
        fighter.freefallAnimation = new Animation(new int[]{3}, 1, false);
    }

    @Override
    public void update(sab.game.fighter.Fighter fighter, Player player) {

    }

    @Override
    public void neutralAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Attack(new ChainSlash(), player), swingAnimation, 6, 8, false);
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (hasBoomerang) {
                swingAnimation.reset();
                player.startAttack(new Attack(new BoomerangKnife(), player), swingAnimation, 6, 12, false);
                hasBoomerang = false;
            } else {
                neutralAttack(fighter, player);
            }
        }
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startAttack(new Attack(new AirSlash(), player), flyingAnimation, 1, 40, false);
            player.removeJumps();
            player.velocity.y = 12;
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (fallingKnife != null) {
                fallingKnife.alive = false;
                fallingKnife = null;
            }

            fallingKnife = new Attack(new FallingKnife(), player);
            swingAnimation.reset();
            player.startAttack(fallingKnife, swingAnimation, 6, 16, false);
        }
    }
}