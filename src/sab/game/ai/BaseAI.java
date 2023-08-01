package sab.game.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.ai.pathfinding.Navigator;
import sab.game.ai.pathfinding.Node;
import sab.game.attack.Attack;
import sab.game.stage.Ledge;
import sab.game.stage.Platform;
import sab.net.Keys;
import sab.util.SabRandom;

public class BaseAI extends AI {
    protected int mashCooldown;
    protected float preferredHorizontalDistance;
    private boolean movingToCenter;
    private boolean dontChase;
    private int moveToCenterTime;
    private Platform platformToCenterOn;
    private final Navigator navigator;

    public BaseAI(Player player, int difficulty, float preferredHorizontalDistance) {
        this(player, difficulty);
        this.preferredHorizontalDistance = preferredHorizontalDistance;
    }

    public BaseAI(Player player, int difficulty) {
        super(player, difficulty);
        navigator = new Navigator(player.battle.getStage());
    }

    protected void setMashCooldown() {
        mashCooldown = 30 - difficulty * 3;
    }

    protected void frozen() {
        if (mashCooldown == 0) {
            pressKey(Keys.ATTACK);
            setMashCooldown();
        }
    }

    protected void grabbingLedge() {
        pressKey(Keys.UP);
    }

    // Run when the player is in danger of falling into the void
    protected void recover(Platform targetPlatform, Ledge targetLedge) {
        dontChase = true;
        Vector2 center = player.hitbox.getCenter(new Vector2());


        if (targetPlatform != null && player.hitbox.y > targetPlatform.hitbox.y + targetPlatform.hitbox.height) {
            if (center.x < targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2) {
                pressKey(Keys.RIGHT);
            } else if (center.x > targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2) {
                pressKey(Keys.LEFT);
            }

            if (player.hitbox.y - (targetPlatform.hitbox.y + targetPlatform.hitbox.height) < player.hitbox.height && player.velocity.y <= -difficulty / 10f) {
                if (player.getRemainingJumps() > 0) {
                    releaseKey(Keys.ATTACK);
                    lockKey(Keys.ATTACK);
                } else {
                    pressKey(Keys.ATTACK);
                }
                pressKey(Keys.UP);
            }

            Attack threat = getNearestEnemyAttack();
            if (threat != null && (threat.parryable && player.touchingStage || difficulty < 3)) {
                FutureCollision collision = getFutureCollision(threat, difficulty * 8);
                if (collision == null) return;

                if (difficulty > 1) {
                    if (difficulty > 3 || SabRandom.random() < 0.5f)
                        pressKey(Keys.PARRY);
                }
            }

            return;
        }

        if (targetLedge != null) {
            if (player.hitbox.x + player.hitbox.width < targetLedge.grabBox.x) {
                pressKey(Keys.RIGHT);
            } else if (player.hitbox.x > targetLedge.grabBox.x + targetLedge.grabBox.width) {
                pressKey(Keys.LEFT);
            }
        }
        if (player.velocity.y <= -difficulty / 5f) {
            if (player.getRemainingJumps() == 0) {
                pressKey(Keys.UP);
                pressKey(Keys.ATTACK);
                mashCooldown = 20 - difficulty * 3;
            } else {
                releaseKey(Keys.ATTACK);
                lockKey(Keys.ATTACK);
                pressKey(Keys.UP);
            }
        }
    }

    protected FutureCollision getFutureCollision(Attack attack, int maxFramesAhead) {
        Rectangle futureHitbox = new Rectangle(player.hitbox);
        Vector2 futureVelocity = player.velocity.cpy();

        Rectangle futureAttackHitbox = new Rectangle(attack.hitbox);
        for (int i = 0; i < maxFramesAhead; i++) {
            futureHitbox.x += futureVelocity.x;
            futureHitbox.y += futureVelocity.y;
            if (!player.touchingStage)
                futureVelocity.y -= .96f;

            futureAttackHitbox.x += attack.velocity.x;
            futureAttackHitbox.y += attack.velocity.y;

            if (futureHitbox.overlaps(futureAttackHitbox)) {
                return new FutureCollision(futureHitbox.getCenter(new Vector2()), i);
            }
        }

        return null;
    }

    protected void navigateTo(Vector2 position) {
        if (player.hitbox.x <= position.x) {
            pressKey(Keys.RIGHT);
        } else if (player.hitbox.x + player.hitbox.width >= position.x) {
            pressKey(Keys.LEFT);
        }

        if (player.hitbox.y < position.y && player.velocity.y <= 0) {
            pressKey(Keys.UP);
            if (player.getRemainingJumps() == 0 && !player.touchingStage) pressKey(Keys.ATTACK);
        } else if (player.hitbox.y > position.y) {
            pressKey(Keys.DOWN);
        }
    }

    protected void attack(Vector2 center, Player target, Vector2 targetCenter) {

    }

    protected void parry(Attack attack) {
        pressKey(Keys.PARRY);
    }

    protected boolean shouldUseFinalAss(Player target) {
        return true;
    }

    protected void moveToCenter() {
        movingToCenter = true;
        moveToCenterTime = 0;
        platformToCenterOn = getNearestPlatform();
    }
    
    @Override
    public final void update() {
        super.update();

        dontChase = false;
        releaseAllKeys();
        unlockAllKeys();

        if (player.frozen()) {
            frozen();
            return;
        }

        if (player.grabbingLedge()) {
            grabbingLedge();
            return;
        }

        if (mashCooldown > 0) mashCooldown--;

        Player target = getNearestOpponent();
        Platform targetPlatform = getNearestPlatform();
        if (target == null) return;

        if (getPlatformBelow(target) == null && target.inFreeFall() && target.getRemainingJumps() > 0) {
            moveToCenter();
        }

        Vector2 targetPosition = target.hitbox.getCenter(new Vector2());
        Vector2 center = player.hitbox.getCenter(new Vector2());

        Platform platformBelow = getPlatformBelow();
        Ledge nearestLedge = getNearestLedge();
        boolean shouldRecover = platformBelow == null && (player.getRemainingJumps() == 0 || nearestLedge.grabBox.getCenter(new Vector2()).dst(center) > 480f || (nearestLedge != null && nearestLedge.grabBox.getCenter(new Vector2()).y > center.y + 128 && nearestLedge.grabBox.getCenter(new Vector2()).dst(player.getCenter()) > 256f));
        if (shouldRecover) {
            recover(targetPlatform, getNearestLedge());
            return;
        }

        if (movingToCenter) {
            if (center.x < platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2) {
                pressKey(Keys.RIGHT);
            }
            if (center.x > platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2) {
                pressKey(Keys.LEFT);
            }

            if (++moveToCenterTime >= 120 || Math.abs(center.x - (platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2)) < player.hitbox.width) {
                moveToCenterTime = 0;
                movingToCenter = false;
                platformToCenterOn = null;
            }

            return;
        }
        if (platformBelow != null) {
            // The minimum distance to the edge of the platform the player can get before it tries to get to the center.
            float minDistanceToEdge = Math.min(player.hitbox.width * 2, Math.max(platformBelow.hitbox.width - preferredHorizontalDistance, 0));
            if (Math.abs(center.x - targetPosition.x) < preferredHorizontalDistance) { // && isDirectlyHorizontal(target.hitbox)
                //if (center.x < targetPosition.x) pressKey(Keys.LEFT);
                //if (center.x > targetPosition.x) pressKey(Keys.RIGHT);

                if (Math.abs(center.x - platformBelow.hitbox.x) < minDistanceToEdge && player.getRemainingJumps() == 0) {
                    releaseKey(Keys.LEFT);
                    pressKey(Keys.RIGHT);
                    pressKey(Keys.UP);
                    moveToCenter();
                    platformToCenterOn = platformBelow;
                }
                if (Math.abs(center.x - (platformBelow.hitbox.x + platformBelow.hitbox.width)) < minDistanceToEdge && player.getRemainingJumps() == 0) {
                    releaseKey(Keys.RIGHT);
                    pressKey(Keys.LEFT);
                    pressKey(Keys.UP);
                    moveToCenter();
                    platformToCenterOn = platformBelow;
                }
            }
        }
        if (!dontChase) {
            navigator.setPosition(center);
            navigator.setGoal(targetPosition);
            navigator.update();

            boolean followingPath = false;
            if (!navigator.hasLosToGoal()) {
                Node node = navigator.currentNode();
                if (node != null) {
                    targetPosition = node.position;
                    if (player.hitbox.contains(node.position)) {
                        navigator.next();
                    }
                    followingPath = true;
                }
            }

            if (followingPath) {
                navigateTo(targetPosition);
                return;
            }

            if (player.hitbox.x + player.hitbox.width + preferredHorizontalDistance < target.hitbox.x || (center.x < targetPosition.x && player.direction == -1)) {
                pressKey(Keys.RIGHT);
            } else if (player.hitbox.x - preferredHorizontalDistance > target.hitbox.x + target.hitbox.width || (center.x > targetPosition.x && player.direction == 1)) {
                pressKey(Keys.LEFT);
            }

            if (getPlatformBelow(target) != null && target.hitbox.y > player.hitbox.y && !target.takingKnockback() && player.velocity.y <= 0) {
                pressKey(Keys.UP);
            }
        }

        Attack nearestAttack = getNearestEnemyAttack();
        if (nearestAttack != null) {
            FutureCollision collision = getFutureCollision(nearestAttack, difficulty * 5);

            if (collision != null) {
                if (collision.ticksUntil() <= 20 && SabRandom.random() * 10 < difficulty) {
                    parry(nearestAttack);
                    return;
                }
                if (center.x > collision.position().x) {
                    releaseKey(Keys.LEFT);
                    pressKey(Keys.RIGHT);
                } else {
                    releaseKey(Keys.RIGHT);
                    pressKey(Keys.LEFT);
                }
                if (collision.position().y <= player.hitbox.y + player.hitbox.height && player.velocity.y <= 0) {
                    if (player.getRemainingJumps() == 0 && difficulty >= 3) {
                        pressKey(Keys.PARRY);
                    } else pressKey(Keys.UP);
                }
            }
        }

        if (target.hasAction() && difficulty >= 4) {
            if (Math.abs(center.x - targetPosition.x) < (player.hitbox.width / 2 + target.hitbox.width / 2) * 3) {
                if (player.velocity.y <= 0) {
                    pressKey(Keys.UP);
                }
            }
        }

        if (targetPlatform != null && !targetPlatform.isSolid() && center.y > targetPosition.y) {
            pressKey(Keys.DOWN);
        }

        if (player.isAssCharged() && shouldUseFinalAss(target)) {
            releaseAllKeys();
            useNeutralAttack();
            return;
        }
        attack(center, target, targetPosition);
    }
}