package sab.game.attack.melees;

import com.badlogic.gdx.math.Vector2;

import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class Peck extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "peck.png";
        attack.life = 12;
        attack.frameCount = 3;
        attack.hitbox.width = 48;
        attack.hitbox.height = 36;
        attack.drawRect.width = 48;
        attack.drawRect.height = 36;
        attack.damage = 16;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 13;
        attack.reflectable = false;

        offset = new Vector2(64, 12);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.frame = 3 - (int) (attack.life / 4f);
        attack.knockback.set(attack.direction * 5, 3.5f);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback.set(attack.direction * 5, 3.5f);
    }
}