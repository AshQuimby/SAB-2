package sab.game.fighter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.marvin.*;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;

public class Marvin extends FighterType {
    private Animation swingAnimation;
    private Animation squatAnimation;
    private Animation chargeAnimation;
    private Animation throwAnimation;
    private Attack toilet;

    @Override
    public void setDefaults(sab.game.fighter.Fighter fighter) {
        fighter.id = "marvin";
        fighter.name = "Marvin";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 14;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 5f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 5, true);
        fighter.description = "Retired Albany plumber now princess saving daydreamer, Marvin is a troubled man who is always distracted by his dreams about the things he could be. He can't seem to escape from his brother's success as a musician.";
        fighter.debut = "Super Marvin Plumber";
        fighter.parryAnimation = new Animation(new int[]{ 12, 0, 0 }, 10, false);
        fighter.airDodgeAnimation = new Animation(new int[]{ 13 }, 1, false);
        swingAnimation = new Animation(new int[] {4, 5, 0}, 7, true);
        squatAnimation = new Animation(new int[] {6}, 4, true);
        chargeAnimation = new Animation(new int[] {9}, 4, true);
        throwAnimation = new Animation(new int[] {10, 11}, 6, true);
        fighter.freefallAnimation = new Animation(new int[]{7}, 1, true);
        fighter.costumes = 3;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 150) {
            private static final int WRENCH_DISTANCE = 48;
            private static final int FROSTBALL_DAMAGE_REQUIRED = 60;

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (player.getCharge() > Math.random() * 50 + 10) {
                    releaseKey(Keys.ATTACK);
                    return;
                }

                if (Math.random() * 25 > difficulty) return;

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= WRENCH_DISTANCE) {
                        useSideAttack();
                    } else {
                        if (target.damage >= FROSTBALL_DAMAGE_REQUIRED && Math.random() * 20 - (target.damage / 30f) < difficulty) {
                            useDownAttack();
                        } else {
                            useNeutralAttack();
                        }
                    }
                } else if ((isDirectlyAbove(target.hitbox) || isDirectlyBelow(target.hitbox) && Math.abs(center.y - targetPosition.y) > 32) && Math.random() * 20 < difficulty) {
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
            player.startAttack(new Fireball(), swingAnimation, 8, 14, false);
            player.velocity.y /= 3;
            player.velocity.x *= 0.9f;
        }
    }

    @Override
    public void sideAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            swingAnimation.reset();
            player.startAttack(new Wrench(), swingAnimation, 4, 18, false);
        }
    }

    @Override
    public void upAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            if (toilet == null || !toilet.alive) {
                squatAnimation.reset();
                player.startAttack(new Toilet(), squatAnimation, 4, 30, false);
                player.removeJumps();
                player.usedRecovery = true;
            }
        }
    }

    @Override
    public void downAttack(sab.game.fighter.Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.startChargeAttack(new PlayerAction(5, true, 0), 5, 90);
        }
    }

    @Override
    public void chargeAttack(sab.game.fighter.Fighter fighter, Player player, int charge) {
        throwAnimation.reset();
        player.startAttack(new Frostball(), throwAnimation, 7, 10, false, new int[]{ charge });
        player.velocity.y /= 3;
        player.velocity.x *= 0.9f;
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            player.setIFrames(8);
            squatAnimation.reset();
            player.startIndefiniteAttack(new Pipe(), squatAnimation, 1, false, new int[0]);
            return true;
        }
        return false;
    }

    @Override
    public void charging(Fighter fighter, Player player, int charge) {
        if (Game.game.window.getTick() % 5 == 0) {
            for (int i = 0; i < 4; i++)
            player.battle.addParticle(new Particle(player.hitbox.getCenter(new Vector2()).add(new Vector2(charge / 4f + 36 + MathUtils.sin(Game.game.window.getTick() / 8f) * 8f, 0).rotateDeg(i * 90 + Game.game.window.getTick() * 4)).add(0, 48), new Vector2(-1, 0).rotateDeg(i * 90 + Game.game.window.getTick() * 2), 32, 32, "frostfire.png"));
        }
        if (player.keys.isPressed(Keys.LEFT) ^ player.keys.isPressed(Keys.RIGHT)) {
            if (player.keys.isPressed(Keys.LEFT)) {
                if (!player.keys.isPressed(Keys.RIGHT)) player.direction = -1;
            }
            if (player.keys.isPressed(Keys.RIGHT)) {
                if (!player.keys.isPressed(Keys.LEFT)) player.direction = 1;
            }
        }
        player.velocity.y *= 0.9f;
        player.frame = chargeAnimation.stepLooping();
    }

    @Override
    public String getVictorySongId(Fighter fighter, Player player) {
        if (player.costume != 1929) {
            return super.getVictorySongId(fighter, player);
        }
        return "starvin_victory.mp3";
    }

    @Override
    public int getRandomCostume(Fighter fighter) {
        if (Utils.aprilFools()) {
            if (MathUtils.randomBoolean(0.25f)) {
                return 1929;
            }
        }
        return super.getRandomCostume(fighter);
    }
}
