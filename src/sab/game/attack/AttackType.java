package sab.game.attack;

import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Game;
import sab.game.Hittable;
import sab.game.Player;

public abstract class AttackType implements Cloneable {
    public void setDefaults(Attack attack) {
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

    public void onKill(Attack attack) {
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

    public boolean canHit(Attack attack, GameObject hit) {
        return hit.hitbox.overlaps(attack.hitbox);
    }

    public void lateRender(Attack attack, Seagraphics g) {
    }

    public void onParry(Attack attack) {
        attack.alive = false;
    }

    public static Attack createAttack(AttackType type, int[] data, Player owner) {
        Attack attack = new Attack(type, owner);
        owner.battle.addAttack(attack, data);
        return attack;
    }
}
