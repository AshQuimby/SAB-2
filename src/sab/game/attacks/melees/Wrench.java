package sab.game.attacks.melees;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Wrench extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "wrench.png";
        attack.life = 15;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 48;
        attack.drawRect.height = 48;
        attack.damage = 10;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 16;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(16 * attack.owner.direction, -4));
        attack.rotation += 6f * attack.direction;

        Vector2 center = attack.hitbox.getCenter(new Vector2());
        float wrenchAngle = (float) Math.toRadians(attack.rotation + 45);
        float wrenchLength = (float) Math.sqrt(attack.hitbox.width * attack.hitbox.width + attack.hitbox.height * attack.hitbox.height);

        attack.hitbox.setCenter(center.x + MathUtils.cos(wrenchAngle) * wrenchLength / 2, 
                center.y + MathUtils.sin(wrenchAngle) * wrenchLength / 2);
        
        for (GameObject gameObject : attack.owner.battle.getGameObjects()) {
            if (Attack.class.isAssignableFrom(gameObject.getClass()) && ((Attack) gameObject).reflectable) {
                if (((Attack) gameObject).owner != attack.owner && ((Attack) gameObject).hitbox.overlaps(attack.hitbox)) {
                    ((Attack) gameObject).velocity.scl(-1);
                    ((Attack) gameObject).owner = attack.owner;
                }
            }
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        hit.velocity.scl(-3);
        hit.direction *= -1;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(16 * attack.owner.direction, -4));
        attack.knockback = new Vector2();
    }
}
