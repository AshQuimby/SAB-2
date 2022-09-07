package sab.game.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

import sab.game.Game;
import sab.game.Player;
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

        if (player.respawning()) {
            if (Math.random() > 0.1) pressKey(Keys.DOWN);
            return;
        }

        if (player.charging()) {
            if (Math.random() > 0.5 && isAbovePlatform()) pressKey(Keys.ATTACK);
            else releaseKey(Keys.ATTACK);
            return;
        }

        if (target == null || targetPlatform == null) return;

        Vector2 targetPosition = null;

        if (opponentPositions.size() > smarts) {
            targetPosition = opponentPositions.get(0).cpy();
            opponentPositions.remove(0);
        }

        if (targetPosition == null) return;

        Vector2 center = player.hitbox.getCenter(new Vector2());


        if (!isAbovePlatform() && (player.getRemainingJumps() <= 0 || player.usedRecovery)) {
            if (getNearestLedge() != null) {
                targetPosition = getNearestLedge().grabBox.getCenter(new Vector2());
            } else {
                targetPosition = Utils.getNearestPointInRect(center, targetPlatform.hitbox);
            }
            attacking = false;
        }

        float distance = targetPosition.dst(center);

        if (!attacking || distance > 64 || Math.signum(player.direction) != Math.signum(targetPosition.cpy().sub(center).x)) {
            if (targetPosition.x > center.x) {
                pressKey(Keys.RIGHT);
            } else if (targetPosition.x < center.x) {
                pressKey(Keys.LEFT);
            }
        }

        if (player.grabbingLedge()) {
            pressKey(Keys.UP);
        }

        if (targetPosition.y - 128 > center.y && player.velocity.y < 0 + smarts / 10) {
            pressKey(Keys.UP);
            if (player.getRemainingJumps() <= 0 && Game.game.window.getTick() % Math.max(smarts / 12, 1) == 0) pressKey(Keys.ATTACK);
        } else if (targetPosition.y < center.y - 64) {
            pressKey(Keys.DOWN);
        }

        if (attacking) {
            if (distance < 128 || distance < 256 && Math.random() > 0.9) {
                if (Game.game.window.getTick() % Math.max(smarts / 16, 1) == 0) pressKey(Keys.ATTACK);
            }
            if (Math.random() > 1 / (smarts + 1) + 0.7f) releaseKey(Keys.ATTACK);
        }
    }
}
