package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Toilet extends AttackType {
    private boolean playerLaunched;

    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "toilet.png";
        attack.life = 320;
        attack.frameCount = 2;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 80;
        attack.damage = 20;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 100;
        attack.reflectable = false;
        attack.collideWithStage = true;
    }

    @Override
    public void update(Attack attack) {
        attack.drawRect.y += 20;

        if (attack.collisionDirection == Direction.DOWN) {
            attack.velocity.y = 0;
        }

        if (attack.life == 290) {
            attack.owner.velocity.y = 14;
            attack.frame = 1;
            playerLaunched = true;
        }

        if (playerLaunched) {
            attack.velocity.y -= 0.4f;
        } else {
            attack.velocity.y -= 0.025f;
            attack.owner.velocity.scl(0);
            attack.owner.hitbox.setCenter(attack.hitbox.getCenter(new Vector2()).add(0, 36));
            attack.owner.frame = 6;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(0, -1);
        attack.knockback = new Vector2(0, -16);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {

    }
}
