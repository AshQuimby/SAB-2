package sab.game.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
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

        if (target == null || targetPlatform == null) return;

        Vector2 targetPosition = null;

        if (opponentPositions.size() > smarts) {
            targetPosition = opponentPositions.get(0).cpy();
            opponentPositions.remove(0);
        }

        if (targetPosition == null) return;

        Vector2 center = player.hitbox.getCenter(new Vector2());

        if (player.grabbingLedge()) {
            pressKey(Keys.UP);
            return;
        }

        if (!isAbovePlatform()) {
            if (player.hitbox.y > targetPlatform.hitbox.y + targetPlatform.hitbox.height) {
                if (targetPosition.x > center.x) {
                    pressKey(Keys.RIGHT);
                } else if (targetPosition.x < center.x) {
                    pressKey(Keys.LEFT);
                }

                pressKey(Keys.ATTACK);
            }

            Ledge ledge = getNearestLedge();
            Vector2 ledgePosition = ledge == null ? Utils.getNearestPointInRect(center, targetPlatform.hitbox): ledge.grabBox.getPosition(new Vector2());

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
        } else {
            if (target.hitbox.y > player.hitbox.y + player.hitbox.height) {
                pressKey(Keys.UP);
            }

            if (player.hitbox.y > target.hitbox.y + target.hitbox.height && Math.abs(center.y - targetPosition.y) < 128) {
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
        }
    }
}
