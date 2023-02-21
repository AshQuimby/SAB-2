package sab.game.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.stage.Ledge;
import sab.game.stage.PassablePlatform;
import sab.game.stage.Platform;
import sab.net.Keys;

public class BaseAI extends AI {
    private int mashCooldown;
    protected float preferredHorizontalDistance;
    private boolean movingToCenter;
    private int moveToCenterTime;
    private Platform platformToCenterOn;

    public BaseAI(Player player, int difficulty, float preferredHorizontalDistance) {
        super(player, difficulty);
        this.preferredHorizontalDistance = preferredHorizontalDistance;
    }

    public BaseAI(Player player, int difficulty) {
        super(player, difficulty);
    }

    // Run when the player is in danger of falling into the void
    protected void recover(Platform targetPlatform, Ledge targetLedge) {
        Vector2 center = player.hitbox.getCenter(new Vector2());

        if (targetPlatform != null && player.hitbox.y > targetPlatform.hitbox.y + targetPlatform.hitbox.height) {
            if (center.x < targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2) {
                pressKey(Keys.RIGHT);
            } else if (center.x > targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2) {
                pressKey(Keys.LEFT);
            }

            if (player.hitbox.y - (targetPlatform.hitbox.y + targetPlatform.hitbox.height) < player.hitbox.height) {
                pressKey(Keys.UP);
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

        if (player.getRemainingJumps() == 0 && mashCooldown == 0) {
            pressKey(Keys.UP);
            pressKey(Keys.ATTACK);
            mashCooldown = 30 - difficulty * 3;
        } else {
            if (player.velocity.y <= 0) {
                pressKey(Keys.UP);
            }
        }
    }

    private Vector2 getFutureCollision(Attack attack, int maxFramesAhead) {
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
                return futureHitbox.getCenter(new Vector2());
            }
        }

        return null;
    }

    protected void attack(Vector2 center, Player target, Vector2 targetCenter) {

    }

    @Override
    public void update() {
        releaseAllKeys();

        if (player.frozen() && mashCooldown == 0) {
            pressKey(Keys.ATTACK);
            mashCooldown = 30 - difficulty * 3;
            return;
        }

        if (player.grabbingLedge()) {
            pressKey(Keys.UP);
            return;
        }

        if (mashCooldown > 0) mashCooldown--;

        Player target = getNearestOpponent();
        Platform targetPlatform = getNearestPlatform();
        if (target == null) return;

        Vector2 targetPosition = target.hitbox.getCenter(new Vector2());
        Vector2 center = player.hitbox.getCenter(new Vector2());

        Platform platformBelow = getPlatformBelow();
        if (platformBelow == null) {
            recover(targetPlatform, getNearestLedge());
            return;
        }

        if (movingToCenter) {
            pressKey(Keys.UP);
            if (player.getRemainingJumps() == 0) {
                pressKey(Keys.ATTACK);
            }

            if (center.x < platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2) {
                pressKey(Keys.RIGHT);
            }
            if (center.x > platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2) {
                pressKey(Keys.LEFT);
            }

            if (++moveToCenterTime >= 120 || Math.abs(center.x - platformToCenterOn.hitbox.x + platformToCenterOn.hitbox.width / 2) < player.hitbox.width) {
                moveToCenterTime = 0;
                movingToCenter = false;
                platformToCenterOn = null;
            }

            return;
        }

        if (Math.abs(center.x - targetPosition.x) < preferredHorizontalDistance && isDirectlyHorizontal(target.hitbox)) {
            if (center.x < targetPosition.x) pressKey(Keys.LEFT);
            if (center.x > targetPosition.x) pressKey(Keys.RIGHT);

            if (Math.abs(center.x - platformBelow.hitbox.x) < player.hitbox.width * 2) {
                releaseKey(Keys.LEFT);
                pressKey(Keys.RIGHT);
                pressKey(Keys.UP);
                platformToCenterOn = platformBelow;
                movingToCenter = true;
            }
            if (Math.abs(center.x - (platformBelow.hitbox.x + platformBelow.hitbox.width)) < player.hitbox.width * 2) {
                releaseKey(Keys.RIGHT);
                pressKey(Keys.LEFT);
                pressKey(Keys.UP);
                platformToCenterOn = platformBelow;
                movingToCenter = true;
            }
        } else {
            if (player.hitbox.x + player.hitbox.width + preferredHorizontalDistance < target.hitbox.x || (center.x < targetPosition.x && player.direction == -1)) {
                pressKey(Keys.RIGHT);
            } else if (player.hitbox.x - preferredHorizontalDistance > target.hitbox.x + target.hitbox.width || (center.x > targetPosition.x && player.direction == 1)) {
                pressKey(Keys.LEFT);
            }
        }

        Attack nearestAttack = getNearestEnemyAttack();
        if (nearestAttack != null) {
            Vector2 collision = getFutureCollision(nearestAttack, difficulty * 5);

            if (collision != null) {
                if (collision.y > player.hitbox.y + player.hitbox.height) {
                    if (center.x > collision.x) {
                        releaseKey(Keys.LEFT);
                        pressKey(Keys.RIGHT);
                    } else {
                        releaseKey(Keys.RIGHT);
                        pressKey(Keys.LEFT);
                    }
                } else {
                    pressKey(Keys.UP);
                }
            }
        }

        if (target.hasAction() && difficulty >= 4) {
            if (Math.abs(center.x - targetPosition.x) < (player.hitbox.width / 2 + target.hitbox.width / 2) * 3) {
                pressKey(Keys.UP);
            }
        }

        if (targetPlatform != null && !targetPlatform.isSolid() && center.y > targetPosition.y) {
            pressKey(Keys.DOWN);
        }

        attack(center, target, targetPosition);
    }
}