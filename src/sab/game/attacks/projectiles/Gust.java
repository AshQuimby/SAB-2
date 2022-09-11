package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Gust extends AttackType {
    @Override
    public void onCreate(Attack attack) {
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

        for (GameObject gameObject : attack.owner.battle.getGameObjects()) {
            if (Attack.class.isAssignableFrom(gameObject.getClass()) && ((Attack) gameObject).reflectable) {
                Attack otherAttack = (Attack) gameObject;
                if (otherAttack.type == this) continue;

                if (otherAttack.hitbox.overlaps(attack.hitbox)) {
                    otherAttack.velocity.add(attack.direction * 2, 0);
                    otherAttack.knockback.set(otherAttack.velocity);
                    otherAttack.owner = attack.owner;
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