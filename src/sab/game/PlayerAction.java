package sab.game;

import com.badlogic.gdx.math.Vector2;

import sab.game.animation.Animation;
import sab.game.attack.Attack;

public class PlayerAction {
    private int delay;
    private int[] data;
    private int endLag;
    private Attack attack;
    private Animation animation;
    private boolean important;
    
    public PlayerAction(int delay, boolean important, int endLag) {
        this.delay = delay;
        this.important = important;
        attack = null;
        this.endLag = endLag;
        this.data = new int[] {0};
    }

    public PlayerAction(int delay, Animation animation, boolean important, int endLag) {
        this.delay = delay;
        this.important = important;
        this.animation = animation;
        attack = null;
        this.endLag = endLag;
        this.data = new int[] {0};
    }

    public PlayerAction(int delay, Attack attack, boolean important, int endLag, int[] data) {
        this.delay = delay;
        this.important = important;
        this.attack = attack;
        this.endLag = endLag;
        this.data = data;
    }

    public PlayerAction(int delay, Attack attack, Animation animation, boolean important, int endLag, int[] data) {
        this.delay = delay;
        this.important = important;
        this.attack = attack;
        this.animation = animation;
        this.endLag = endLag;
        this.data = data;
    }

    public void update(Player player) {
        if (animation != null) {
            if (!animation.isDone()) {
                player.frame = animation.step();
            }
        }

        if (--delay == 0) {
            if (attack != null) {
                attack.onSpawn(data);
                attack.drawRect.setCenter(attack.hitbox.getCenter(new Vector2()));
                player.battle.addGameObject(attack);
                attack = null;
            }
        }
    }

    public void changeDelay(int toAdd) {
        delay += toAdd;
    }

    public void resetAnimation() {
        animation.reset();
    }

    public boolean usingAnimation(Animation animation) {
        return animation == this.animation;
    }

    public boolean finished() {
        return delay < -endLag;
    }

    public boolean isImportant() {
        return important;
    }

    public int[] getData() {
        return data;
    }
}
