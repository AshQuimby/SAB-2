package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Fireball extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "fireball.png";
        attack.life = 180;
        attack.frameCount = 4;
        attack.hitbox.width = 32;
        attack.hitbox.height = 32;
        attack.drawRect.width = 32;
        attack.drawRect.height = 32;
        attack.damage = 10;
        attack.directional = true;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 1;
        Direction collisionDirection = CollisionResolver.movingResolve(attack, attack.owner.battle.getPlatforms());

        attack.rotation -= (attack.velocity.x * (Math.abs(attack.velocity.y) + 0.1f)) / 3f;

        if (collisionDirection != Direction.NONE) {
            if (collisionDirection == Direction.UP || collisionDirection == Direction.DOWN) {
                attack.velocity.x *= 0.9f;
                attack.velocity.y *= -0.86f;
            } else if (collisionDirection == Direction.RIGHT || collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -1;
            }
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
        attack.knockback = new Vector2(8 * attack.owner.direction, 4);
    }
}
