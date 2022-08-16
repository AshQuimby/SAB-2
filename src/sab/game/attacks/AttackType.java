package sab.game.attacks;

import com.seagull_engine.GameObject;

public class AttackType implements Cloneable {
    public void onCreate(Attack attack) {

    }

    public void update(Attack attack) {
    }

    public AttackType copy() {
        try {
            return (AttackType) this.clone();
        } catch (CloneNotSupportedException e) {
        }
        return null;
    }

    public void sucessfulHit(Attack attack, GameObject hit) {
    }

    public void hit(Attack attack, GameObject hit) {
    }

    public void kill(Attack attack) {
    }

    public void onSpawn(Attack attack, int data) {
    }
}
