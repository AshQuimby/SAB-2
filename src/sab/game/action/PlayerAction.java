package sab.game.action;

import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;

public class PlayerAction {
    private int delay;
    private int[] data;
    private int endLag;
    protected Attack attack;
    private Animation animation;
    private boolean important;
    private boolean usedAttack;
    
    public PlayerAction(int delay, boolean important, int endLag) {
        if (delay == 0) this.delay = 1;
        else this.delay = delay;
        this.important = important;
        attack = null;
        this.endLag = endLag;
        this.data = new int[] {0};
    }

    public PlayerAction(int delay, Animation animation, boolean important, int endLag) {
        if (delay == 0) this.delay = 1;
        else this.delay = delay;
        this.important = important;
        this.animation = animation;
        attack = null;
        this.endLag = endLag;
        this.data = new int[] {0};
    }

    public PlayerAction(int delay, Attack attack, boolean important, int endLag, int[] data) {
        if (delay == 0) this.delay = 1;
        else this.delay = delay;
        this.important = important;
        this.attack = attack;
        this.endLag = endLag;
        this.data = data;
    }

    public PlayerAction(int delay, Attack attack, Animation animation, boolean important, int endLag, int[] data) {
        if (delay == 0) this.delay = 1;
        else this.delay = delay;
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

        delay--;
        if (delay == 0) {
            if (!usedAttack && attack != null) {
                attack.onSpawn(data);
                attack.drawRect.setCenter(attack.hitbox.getCenter(new Vector2()));
                player.battle.addGameObject(attack);
                usedAttack = true;
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

    public Animation getAnimation() { return animation; }
}
