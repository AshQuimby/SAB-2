package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Lobber extends AttackType {
    private int bounces;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "lobber.png";
        attack.life = 180;
        attack.hitbox.width = 28;
        attack.hitbox.height = 28;
        attack.drawRect.width = 28;
        attack.drawRect.height = 28;
        attack.damage = 12;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        bounces = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 3f, 1.25f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(8 * attack.owner.direction, 4);
    }

    @Override
    public void update(Attack attack) {
        attack.rotation -= attack.velocity.x / 2;
        attack.velocity.y -= 0.5f;
        if (attack.collisionDirection != Direction.NONE) {
            if (attack.collisionDirection == Direction.DOWN) {
                attack.velocity.y *= -0.6f;
                attack.velocity.x *= 0.9f;
                bounces++;
            } else {
                attack.alive = false;
            }
        }
        if (bounces > 1) {
            attack.alive = false;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
