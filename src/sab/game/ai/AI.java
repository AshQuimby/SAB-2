package sab.game.ai;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.Game;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.stage.Ledge;
import sab.game.stage.Platform;
import sab.util.Utils;

public class AI {
    protected final Player player;
    protected int difficulty;

    public AI(Player player, int difficulty) {
        this.player = player;
        this.difficulty = difficulty;
    }

    public void update() {
    }

    // Presses the key in the player object attached to this class
    public final void pressKey(int keyCode) {
        player.keys.press(keyCode);
    }

    // Releases the key in the player object attached to this class
    public final void releaseKey(int keyCode) {
        player.keys.release(keyCode);
    }

    // Presses the key in the player object attached to this class only if it isn't pressed
    public final void tapKey(int keyCode) {
        if (!player.keys.isPressed(keyCode)) player.keys.press(keyCode);
    }

    // Toggles between the states of the key in the player object attached to this class
    public final void toggleKey(int keyCode) {
        if (player.keys.isPressed(keyCode)) player.keys.release(keyCode);
        else player.keys.release(keyCode);
    }

    public final void releaseAllKeys() {
        for (int i = 0; i < 6; i++) player.keys.release(i);
    }

    public boolean isDirectlyHorizontal(Rectangle rect) {
        return player.hitbox.y < rect.y + rect.height && player.hitbox.y + player.hitbox.height > rect.y;
    }

    public boolean isDirectlyAbove(Rectangle rect) {
        return (player.hitbox.x < rect.x + rect.width && player.hitbox.x + player.hitbox.width > rect.x) && player.hitbox.y > rect.y;
    }

    public boolean isDirectlyBelow(Rectangle rect) {
        return (player.hitbox.x < rect.x + rect.width && player.hitbox.x + player.hitbox.width > rect.x) && player.hitbox.y < rect.y;
    }
    
    public final Player getNearestOpponent() {
        float bestDistance = -1;
        Player bestTarget = null;
        for (Player target : player.battle.getPlayers()) {
            if (target != player) {
                float distance = player.hitbox.getCenter(new Vector2()).dst(target.hitbox.getCenter(new Vector2()));
                if (distance <= bestDistance || bestDistance < 0) {
                    bestTarget = target;
                    bestDistance = distance;
                }
            }
        }
        return bestTarget;
    }

    public final Attack getNearestEnemyAttack() {
        float bestDistance = -1;
        Attack bestTarget = null;
        for (Attack target : player.battle.getAttacks()) {
            if (target.owner == player) continue;

            float distance = player.hitbox.getCenter(new Vector2()).dst(target.hitbox.getCenter(new Vector2()));
            if (distance <= bestDistance || bestDistance < 0) {
                bestTarget = target;
                bestDistance = distance;
            }
        }
        return bestTarget;
    }

    public final Platform getNearestPlatform() {
        float bestDistance = -1;
        GameObject bestTarget = null;
        for (GameObject target : player.battle.getPlatforms()) {
            Vector2 point = Utils.getNearestPointInRect(player.hitbox.getCenter(new Vector2()), target.hitbox);
            float distance = player.hitbox.getCenter(new Vector2()).dst(point);
            if (distance <= bestDistance || bestDistance < 0) {
                bestTarget = target;
                bestDistance = distance;
            }
        }
        return (Platform) bestTarget;
    }

    public final Platform getPlatformBelow() {
        float bestVerticalDistance = -1;
        GameObject bestTarget = null;
        for (GameObject target : player.battle.getPlatforms()) {
            if (target.hitbox.x < player.hitbox.x + player.hitbox.width && target.hitbox.x + target.hitbox.width > player.hitbox.x) {
                float verticalDistance = player.hitbox.y - (target.hitbox.y + target.hitbox.height);
                if (verticalDistance < 0) continue;
                if (verticalDistance <= bestVerticalDistance || bestVerticalDistance < 0) {
                    bestTarget = target;
                    bestVerticalDistance = verticalDistance;
                }
            }
        }
        return (Platform) bestTarget;
    }

    public final Ledge getNearestLedge() {
        float bestDistance = -1;
        Ledge bestTarget = null;
        for (Ledge target : player.battle.getStage().getLedges()) {
            Vector2 point = Utils.getNearestPointInRect(player.hitbox.getCenter(new Vector2()), target.grabBox);
            float distance = player.hitbox.getCenter(new Vector2()).dst(point);
            if (distance <= bestDistance || bestDistance < 0) {
                bestTarget = target;
                bestDistance = distance;
            }
        }
        return bestTarget;
    }
}
