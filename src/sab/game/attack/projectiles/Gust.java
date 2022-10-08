package sab.game.attack.projectiles;

import com.badlogic.gdx.math.Vector2;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Gust extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "gust.png";
        attack.life = 24;
        attack.frameCount = 4;
        attack.hitbox.width = 120;
        attack.hitbox.height = 116;
        attack.drawRect.width = 120;
        attack.drawRect.height = 116;
        attack.damage = 0;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.frame = 4 - (int) (attack.life / 6f);       

        for (Attack other : attack.owner.battle.getAttacks()) {
            if (other.reflectable) {
                if (other == attack) continue;

                if (other.hitbox.overlaps(attack.hitbox)) {
                    other.velocity.add(attack.direction * 2, 0);
                    other.knockback.set(other.velocity);
                    other.owner = attack.owner;
                }
            }
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2), 0));
        attack.direction = attack.owner.direction;
        attack.velocity = new Vector2(4 * attack.direction, 0);
        attack.knockback = new Vector2(10 * attack.direction, 6);
    }
}