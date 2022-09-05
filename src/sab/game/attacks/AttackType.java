package sab.game.attacks;

import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

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

    public void hit(Attack attack, GameObject hit) {
    }

    public void successfulHit(Attack attack, GameObject hit) {
    }

    public void kill(Attack attack) {
    }

    public void onSpawn(Attack attack, int[] data) {
    }

    public void render(Attack attack, Seagraphics g) {
        g.usefulDraw(
            g.imageProvider.getImage(attack.imageName),
            attack.drawRect.x,
            attack.drawRect.y,
            (int) attack.drawRect.width,
            (int) attack.drawRect.height,
            attack.frame,
            attack.frameCount,
            attack.rotation,
            attack.direction == 1,
            false);
    }
    
}
