package sab.game.fighter;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.emperor_evil.*;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.SabRandom;

public class EmperorEvil extends FighterType {
    private Animation shootAnimation;
    private Animation chompAnimation;
    private Animation barrel;
    private Animation chargeAnimation;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "emperor_evil";
        fighter.name = "Emperor E. Vile";
        fighter.hitboxWidth = 64;
        fighter.hitboxHeight = 80;
        fighter.renderWidth = 80;
        fighter.renderHeight = 88;
        fighter.imageOffsetX = -4;
        fighter.imageOffsetY = 4;
        fighter.frames = 14;
        fighter.jumpHeight = 90;
        fighter.doubleJumpMultiplier = 0.8f;
        fighter.speed = 9.2f;
        fighter.acceleration = .35f;
        fighter.friction = .22f;
        fighter.mass = 7.5f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 7, true);
        fighter.description = "Emperor E. Vile is a big mean alligator who steals all of the bananas from the monkeys. He doesn't even eat bananas, nobody knows why he takes them in the first place.";
        fighter.debut = "King Kong City";
        fighter.airDodgeSpeed = 5;

        shootAnimation = new Animation(new int[] {9, 5}, 18, true);
        chompAnimation = new Animation(new int[] {4, 5}, 6, true);
        barrel = new Animation(new int[] {10}, 60, true);
        chargeAnimation = new Animation(new int[] {11, 12, 13, 11, 12, 13}, 8, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 100) {
            boolean suck;
            Rectangle suckBounds = new Rectangle(0, 0, 116, 108);

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (player.hasAction() && suck) {
                    pressKey(Keys.ATTACK);
                }

                if (player.touchingStage) {
                    suckBounds.setCenter(center);
                    suckBounds.x += 84 * player.direction;

                    Rectangle futureTargetHitbox = new Rectangle(target.hitbox);
                    Vector2 futureTargetVelocity = target.velocity.cpy();
                    int ticksUntilCollision = 0;
                    for (int i = 0; i < 20 + difficulty * 10; i++) {
                        futureTargetHitbox.x += futureTargetVelocity.x;
                        futureTargetHitbox.y += futureTargetVelocity.y;
                        if (!target.touchingStage) futureTargetVelocity.y -= .96f;

                        if (suckBounds.overlaps(futureTargetHitbox)) {
                            ticksUntilCollision = i;
                            break;
                        }
                    }

                    if (ticksUntilCollision >= 18 && ticksUntilCollision <= 36) {
                        useNeutralAttack();
                        suck = true;
                        return;
                    }
                }

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x) && SabRandom.random() * 25 < difficulty) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);
                    if (horizontalDistance > 90) {
                        useNeutralAttack();
                        suck = false;
                    } else {
                        useSideAttack();
                    }
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
            shootAnimation.reset();
            player.startAttack(new Cannonball(), shootAnimation, 18, 18, false);
            player.velocity.x *= 0.2f;
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            chompAnimation.reset();
            player.startAttack(new Chomp(), chompAnimation, 4, 40, false);
        }
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            barrel.reset();
            player.velocity.x = 0;
            player.velocity.y = 0;
            player.startAttack(new ExplosiveBarrel(), barrel, 1, 30, false);
            player.removeJumps();
            player.usedRecovery = true;
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 15, 120);
        }
    }

    @Override
    public void chargeAttack(sab.game.fighter.Fighter fighter, Player player, int charge) {
        if (!player.usedRecovery) {
            chargeAnimation.reset();
            player.startAttack(new MagicBanana(), chargeAnimation, 7, 10, false, new int[]{charge});
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
            for (int i = 0; i < 8; i++) {
                player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()), new Vector2(2 * SabRandom.random(-1f, 1f), 4 * SabRandom.random()), 64, 64, 0, "bananafire.png"));
            }
        }
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 15 == 0) {
            for (int i = 0; i < 12; i++) {
                player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()).add(0, 32).add(new Vector2(96 * charge / 120 / 2, 0).rotateDeg(i * 30f)), new Vector2(), 32, 32, "bananafire.png"));
            }
        }
        player.frame = chargeAnimation.stepLooping();
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            chargeAnimation.reset();
            player.setIFrames(120);
            player.startAttack(new BananaRain(), chargeAnimation, 8, 32, false);
            return true;
        }
        return false;
    }
}
