package sab.game.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.CollisionResolver;
import sab.game.Game;
import sab.game.Player;
import sab.game.attacks.Attack;
import sab.game.stages.Ledge;
import sab.game.stages.Platform;
import sab.net.Keys;
import sab.util.Utils;

public class BaseAI extends AI {
    private List<Vector2> opponentPositions;
    private int smarts;
    private int originalSmarts;
    private int originalLives;

    public BaseAI(Player player, int reactionTime) {
        super(player);
        smarts = reactionTime;
        originalSmarts = smarts;
        originalLives = player.getLives();
        opponentPositions = new ArrayList<>();
    }

    // Run when the player is in danger of falling into the void
    private void recover(Platform targetPlatform, Ledge targetLedge) {
        Vector2 center = player.hitbox.getCenter(new Vector2());

        // If the player is not over the platform but still above it
        if (player.hitbox.y > targetPlatform.hitbox.y + targetPlatform.hitbox.height) {
            // Calculate if the player has enough time to reach the platform before falling
            // below it
            boolean canLandOnPlatform = false;

            Vector2 platformMiddle = new Vector2(targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2,
                    targetPlatform.hitbox.y + targetPlatform.hitbox.height);
            Rectangle futureHitbox = new Rectangle(player.hitbox);
            Vector2 futureVelocity = player.velocity.cpy();
            for (int i = 0; i < 20; i++) {
                futureHitbox.x += futureVelocity.x;
                futureHitbox.y += futureVelocity.y;
                futureVelocity.sub(futureVelocity.cpy().scl(player.fighter.friction));
                futureVelocity.y -= .96f;

                if (platformMiddle.x < futureHitbox.x + futureHitbox.width / 2) {
                    futureVelocity.x += Math.max(-player.fighter.acceleration,
                            -player.fighter.speed - futureVelocity.x);
                } else if (platformMiddle.x > futureHitbox.x + futureHitbox.width / 2) {
                    futureVelocity.x += Math.min(player.fighter.acceleration, player.fighter.speed - futureVelocity.x);
                }

                if (futureHitbox.overlaps(targetPlatform.hitbox)) {
                    canLandOnPlatform = true;
                    break;
                }

                if (futureHitbox.y < platformMiddle.y) {
                    break;
                }
            }

            if (canLandOnPlatform) {
                if (platformMiddle.x > center.x) {
                    pressKey(Keys.RIGHT);
                } else if (platformMiddle.x < center.x) {
                    pressKey(Keys.LEFT);
                }
            }
        } else {
            Ledge ledge = targetLedge;
            Vector2 ledgePosition = ledge == null ? Utils.getNearestPointInRect(center, targetPlatform.hitbox)
                    : ledge.grabBox.getPosition(new Vector2());

            if (ledgePosition.x > center.x) {
                pressKey(Keys.RIGHT);
            } else if (ledgePosition.x < center.x) {
                pressKey(Keys.LEFT);
            }

            if (ledgePosition.y > center.y + player.velocity.y * 2) {
                if (Game.game.window.getTick() % (smarts + 1) == 0) {
                    pressKey(Keys.UP);
                    if (player.getRemainingJumps() == 0) {
                        pressKey(Keys.ATTACK);
                    }
                }
            }
        }
    }

    private Vector2 getFutureCollision(Attack attack) {
        Rectangle futureHitbox = new Rectangle(player.hitbox);
        Vector2 futureVelocity = player.velocity.cpy();

        Rectangle futureAttackHitbox = new Rectangle(attack.hitbox);
        for (int i = 0; i < 20; i++) {
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

    @Override
    public void update() {
        smarts = (int) Math.max(0, originalSmarts - 50 / ((float) player.getLives() / (float) originalLives + 1) + 25);

        releaseKey(Keys.RIGHT);
        releaseKey(Keys.LEFT);
        releaseKey(Keys.UP);
        releaseKey(Keys.DOWN);
        releaseKey(Keys.ATTACK);

        boolean attacking = true;
        Player target = getNearestOpponent();
        opponentPositions.add(target.hitbox.getCenter(new Vector2()));
        Platform targetPlatform = getNearestPlatform();

        if (target == null || targetPlatform == null)
            return;

        Vector2 targetPosition = null;

        if (opponentPositions.size() > smarts) {
            targetPosition = opponentPositions.get(0).cpy();
            opponentPositions.remove(0);
        }

        if (targetPosition == null)
            return;

        Vector2 center = player.hitbox.getCenter(new Vector2());

        if (player.grabbingLedge()) {
            pressKey(Keys.UP);
        }

        if (!isAbovePlatform()) {
            recover(targetPlatform, getNearestLedge());
            return;
        } else {
            Rectangle opponentHitbox = new Rectangle(target.hitbox);
            opponentHitbox.setCenter(opponentHitbox.x + opponentHitbox.width / 2,
                    targetPlatform.hitbox.y + targetPlatform.hitbox.height / 2);

            if (!opponentHitbox.overlaps(targetPlatform.hitbox)) {
                if (center.x < targetPosition.x
                        && center.x < targetPlatform.hitbox.x + targetPlatform.hitbox.width / 2 - 100) {
                    pressKey(Keys.RIGHT);
                }
                if (center.x > targetPosition.x
                        && center.x > targetPlatform.hitbox.x - targetPlatform.hitbox.width / 2 + 100) {
                    pressKey(Keys.LEFT);
                }

                pressKey(Keys.ATTACK);
                return;
            }

            if (target.hitbox.y > player.hitbox.y + player.hitbox.height) {
                pressKey(Keys.UP);
            }

            if (player.hitbox.y > target.hitbox.y + target.hitbox.height
                    && Math.abs(center.y - targetPosition.y) < 128) {
                pressKey(Keys.DOWN);
            }

            if (targetPosition.x > center.x) {
                pressKey(Keys.RIGHT);
            } else if (targetPosition.x < center.x) {
                pressKey(Keys.LEFT);
            }

            if (center.dst(targetPosition) < 128) {
                pressKey(Keys.ATTACK);
            }

            if (target.charging()) {
                if (target.direction == 1 && targetPosition.x < center.x) {
                    pressKey(Keys.UP);
                    pressKey(Keys.LEFT);
                } else if (target.direction == -1 && targetPosition.x > center.x) {
                    pressKey(Keys.UP);
                    pressKey(Keys.RIGHT);
                }

                pressKey(Keys.ATTACK);
            }
        }

        Attack nearestAttack = getNearestEnemyAttack();
        if (nearestAttack != null) {
            Vector2 collision = getFutureCollision(nearestAttack);

            if (collision != null) {
                if (collision.y > player.hitbox.y + player.hitbox.height) {
                    if (center.x > collision.x) {
                        pressKey(Keys.RIGHT);
                    } else {
                        pressKey(Keys.LEFT);
                    }
                } else {
                    pressKey(Keys.UP);
                }
            }
        }

        if (player.charging() && Math.abs(target.hitbox.y + target.hitbox.height - center.y) < player.hitbox.height) {
            releaseKey(Keys.ATTACK);
        }
    }
}
