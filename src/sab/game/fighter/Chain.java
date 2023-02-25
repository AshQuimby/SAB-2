package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import sab.game.Player;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.chain.AirSlash;
import sab.game.attack.chain.ChainSlash;
import sab.game.attack.chain.BoomerangKnife;
import sab.game.attack.chain.FallingKnife;
import sab.net.Keys;

public class Chain extends FighterType {
    private Animation swingAnimation;
    private Animation flyingAnimation;

    private Attack boomerangKnife;
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
        boomerangKnife = null;
        fallingKnife = null;
        swingAnimation = new Animation(new int[] {4, 5, 3}, 5, true);
        flyingAnimation = new Animation(new int[] {6}, 5, true);
        fighter.freefallAnimation = new Animation(new int[]{3}, 1, false);
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 24) {
            private static final int SLASH_DISTANCE = 40;

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (Math.random() * 20 > difficulty) return;

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= SLASH_DISTANCE) {
                        useNeutralAttack();
                    } else {
                        useSideAttack();
                    }
                } else if (isDirectlyBelow(target.hitbox) && Math.abs(center.y - targetPosition.y) > 32 && Math.random() * 20 < difficulty) {
                    useDownAttack();
                } else {
                    useUpAttack();
                }
            }
        };
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
            if (boomerangKnife == null || !boomerangKnife.alive) {
                swingAnimation.reset();
                boomerangKnife = new Attack(new BoomerangKnife(), player);
                player.startAttack(boomerangKnife, swingAnimation, 6, 12, false);
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