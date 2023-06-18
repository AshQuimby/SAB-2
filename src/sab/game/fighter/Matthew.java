package sab.game.fighter;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.DamageSource;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.ai.AI;
import sab.game.ai.BaseAI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.matthew.*;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;
import sab.util.SABRandom;

public class Matthew extends FighterType {
    private static final int FULL_SLOWDOWN_DURATION = 480;
    private static final int FULL_SLOWDOWN_STRENGTH = 3;

    private static final int PARTIAL_SLOWDOWN_DURATION = 120;
    private static final int PARTIAL_SLOWDOWN_STRENGTH = 2;

    private Animation swingAnimation;
    private Animation bigSwingAnimation;
    private Animation parryAnimation;
    private Animation failedParryAnimation;
    private Animation verticalThrustAnimation;
    private int counterCharge;
    private int slowdownTimeTime;

    @Override
    public void setDefaults(Fighter fighter) {
        fighter.id = "matthew";
        fighter.name = "Matthew";
        fighter.hitboxWidth = 40;
        fighter.hitboxHeight = 60;
        fighter.renderWidth = 64;
        fighter.renderHeight = 64;
        fighter.imageOffsetX = 0;
        fighter.imageOffsetY = 2;
        fighter.frames = 15;
        fighter.jumpHeight = 160;
        fighter.friction = .2f;
        fighter.mass = 4.8f;
        fighter.airDodgeSpeed = 9.5f;
        fighter.airJumps = 1;
        fighter.walkAnimation = new Animation(0, 3, 6, true);
        fighter.description = "God is dead.\nMatthew killed him.";
        fighter.debut = "Our Sports Resort";

        parryAnimation = new Animation(new int[] {13, 14, 14, 14, 14, 13, 7}, 4, true);
        failedParryAnimation = new Animation(new int[] {13, 13, 7}, 8, true);
        swingAnimation = new Animation(new int[] {4, 5, 6, 7}, 4, true);
        bigSwingAnimation = new Animation(new int[] {12, 12, 13, 4, 4, 5, 6, 7, 7}, 5, true);
        verticalThrustAnimation = new Animation(new int[] {9}, 60, true);
        fighter.freefallAnimation = new Animation(new int[]{8}, 1, true);
        fighter.knockbackAnimation = new Animation(new int[]{10}, 1, true);
        fighter.ledgeAnimation = new Animation(new int[] {11}, 1, true);
        fighter.costumes = 3;
        counterCharge = 2;
    }

    @Override
    public AI getAI(Player player, int difficulty) {
        return new BaseAI(player, difficulty, 150) {
            private static final int SLASH_DISTANCE = 64;
            private final Matthew matthew = (Matthew) player.fighter.type;

            @Override
            public void parry(Attack attack) {
                if (matthew.counterCharge == 2) {
                    useNeutralAttack();
                } else {
                    pressKey(Keys.PARRY);
                }
            }

            @Override
            public void attack(Vector2 center, Player target, Vector2 targetPosition) {
                if (SABRandom.random() * 25 > difficulty) return;

                if (isDirectlyHorizontal(target.hitbox) && isFacing(targetPosition.x)) {
                    float horizontalDistance = Math.abs(center.x - targetPosition.x);

                    if (horizontalDistance <= SLASH_DISTANCE) {
                        useSideAttack();
                    }
                } else if ((isDirectlyBelow(target.hitbox) && Math.abs(center.y - targetPosition.y) > 32) && SABRandom.random() * 20 < difficulty) {
                    useUpAttack();
                } else if (isDirectlyAbove(target.hitbox)) {
                    useDownAttack();
                }

                if (target.grabbingLedge()) {
                    preferredHorizontalDistance = -30f;
                } else {
                    preferredHorizontalDistance = 4f;
                }

                if (player.getRemainingJumps() > 0 && getPlatformBelow(target) == null && isDirectlyAbove(target.hitbox)) {
                    if (player.touchingStage) pressKey(Keys.UP);
                    else useDownAttack();
                }

                if (matthew.counterCharge == 2 && (SABRandom.random(difficulty + 2) > 2 || difficulty >= 5)) {
                    Attack nearestThreat = getNearestEnemyAttack();
                    if (nearestThreat != null) {
                        if (getFutureCollision(nearestThreat, 24) != null) {
                            useNeutralAttack();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void neutralAttack(Fighter fighter, Player player) {
        if (slowdownTimeTime > 0 && counterCharge == 3) {
            verticalThrustAnimation.reset();
            player.startAttack(new DashSlash(), verticalThrustAnimation, 4, 12, true);
            counterCharge = 0;
        } else if (counterCharge >= 2) {
            counterCharge--;
            parryAnimation.reset();
            player.startAnimation(4, parryAnimation, 24, false);
        } else {
            counterCharge = 0;
            failedParryAnimation.reset();
            player.startAnimation(4, failedParryAnimation, 24, false);
        }
    }

    @Override
    public void sideAttack(Fighter fighter, Player player) {
        swingAnimation.reset();
        player.startAttack(new MatthewSlash(), swingAnimation, 4, 12, false);
    }

    @Override
    public void downAttack(Fighter fighter, Player player) {
        verticalThrustAnimation.reset();
        player.startAttack(new PogoSword(), verticalThrustAnimation, 4, 40, false);
    }

    @Override
    public void upAttack(Fighter fighter, Player player) {
        if (!player.usedRecovery) {
            verticalThrustAnimation.reset();
            player.startAttack(new UpwardsSlash(), verticalThrustAnimation, 4, 14, false);
            player.usedRecovery = true;
            player.removeJumps();
        }
    }

    @Override
    public void update(Fighter fighter, Player player) {
        if (player.usingAnimation(bigSwingAnimation)) {
            player.velocity.x = 0;
            player.velocity.y = 0;
        }

        if (slowdownTimeTime > 0) {
            if (slowdownTimeTime <= PARTIAL_SLOWDOWN_DURATION) {
                player.battle.slowdown(PARTIAL_SLOWDOWN_STRENGTH, PARTIAL_SLOWDOWN_STRENGTH);
            } else {
                player.battle.slowdown(FULL_SLOWDOWN_STRENGTH, FULL_SLOWDOWN_STRENGTH);
            }
            slowdownTimeTime--;
            player.ignoreSlowdowns = true;
        } else {
            player.ignoreSlowdowns = false;
            if (counterCharge > 2) counterCharge = 2;
        }
    }

    @Override
    public void hitObject(Fighter fighter, Player player, Attack attack, GameObject hit) {
        if (!(attack.type instanceof MegaCounterSlash)) {
            if (counterCharge < (slowdownTimeTime > 0 ? 3 : 2)) counterCharge++;
        }
    }

    @Override
    public boolean onHit(Fighter fighter, Player player, DamageSource source) {
        if (source.parryable && player.getAnimation() != null && player.getAnimation().getFrame() == 14) {
            source.owner.stun(32);
            bigSwingAnimation.reset();
            SABSounds.playSound("mega_counter.mp3");
            player.battle.freezeFrame(10, 3, 45, true);
            player.frame = 12;
            counterCharge = 0;
            source.owner.frame = source.owner.fighter.knockbackAnimation.stepLooping();
            player.startAttack(new MegaCounterSlash(), bigSwingAnimation, 20, 20, false, new int[] { source.damage });
            player.hitbox.setCenter(source.owner.getCenter().add(source.owner.direction * -64, 16));
            player.direction = (int) Math.signum(source.owner.getCenter().x - player.getCenter().x);
            String teleportImage = Utils.appendCostumeToIdentifier("matthew_teleport", player.costume, "png");
            player.battle.addParticle(new Particle(player.getCenter().add(-6 * player.direction, 12), new Vector2(), 100, 80, 12, 2, player.direction, teleportImage));
            player.setIFrames(10);
            return false;
        }
        return true;
    }

    @Override
    public void renderUI(Fighter fighter, Player player, Seagraphics g) {
        g.usefulDraw(g.imageProvider.getImage("matthew_ui.png"), player.getId() == 0 ? -256 - 56 - 12 : 256 + 4, -256, 64, 48, counterCharge == 0 ? 0 : (counterCharge + 3 * player.costume), 10, 0, false, false);
    }

    @Override
    public boolean finalAss(Fighter fighter, Player player) {
        slowdownTimeTime = FULL_SLOWDOWN_DURATION + PARTIAL_SLOWDOWN_DURATION;
        return true;
    }

    @Override
    public void onKill(Fighter fighter, Player player) {
        slowdownTimeTime = 0;
    }
}
