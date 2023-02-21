package sab.game.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.stage.Ledge;
import sab.game.stage.Platform;
import sab.net.Keys;
import sab.util.Utils;

public class BaseAI extends AI {
    private int mashCooldown;

    public BaseAI(Player player, int difficulty) {
        super(player, difficulty);
    }

    // Run when the player is in danger of falling into the void
    protected void recover(Platform targetPlatform, Ledge targetLedge) {
        Vector2 center = player.hitbox.getCenter(new Vector2());

        if (player.hitbox.y > targetPlatform.hitbox.y + targetPlatform.hitbox.height) {
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

        if (player.hitbox.x + player.hitbox.width < targetLedge.grabBox.x) {
            pressKey(Keys.RIGHT);
        } else if (player.hitbox.x > targetLedge.grabBox.x + targetLedge.grabBox.width) {
            pressKey(Keys.LEFT);
        }

        pressKey(Keys.UP);
        if (player.getRemainingJumps() == 0 && mashCooldown == 0) {
            pressKey(Keys.ATTACK);
            mashCooldown = 30 - difficulty * 3;
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
        if (target == null) return;

        Vector2 targetPosition = target.hitbox.getCenter(new Vector2());
        Vector2 center = player.hitbox.getCenter(new Vector2());

        Platform platformBelow = getPlatformBelow();
        if (platformBelow == null) {
            Platform targetPlatform = getNearestPlatform();
            recover(targetPlatform, getNearestLedge());
            return;
        }

        if (player.hitbox.x + player.hitbox.width < target.hitbox.x || (center.x < targetPosition.x && player.direction == -1)) {
            pressKey(Keys.RIGHT);
        } else if (player.hitbox.x > target.hitbox.x + target.hitbox.width || (center.x > targetPosition.x && player.direction == 1)) {
            pressKey(Keys.LEFT);
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
    }
}