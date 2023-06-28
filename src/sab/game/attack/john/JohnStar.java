package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class JohnStar extends AttackType {

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "john_star.png";
        attack.life = 30;
        attack.frameCount = 1;
        attack.hitbox.width = 36;
        attack.hitbox.height = 36;
        attack.drawRect.width = 44;
        attack.drawRect.height = 44;
        attack.damage = 12;
        attack.directional = true;
        attack.reflectable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(16 * attack.direction, -12));
        attack.velocity = new Vector2(32 * attack.direction, 0);
        attack.knockback = new Vector2(7 * attack.direction, 4);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.x *= 0.75f;
        attack.rotation -= attack.velocity.x * 16 + 8 * attack.direction;
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void successfulHit(sab.game.attack.Attack attack, GameObject hit) {
        hit.velocity.scl(0.9f);
        if (hit instanceof Player) {
            ((Player) hit).frame = ((Player) hit).fighter.knockbackAnimation.stepLooping();
        }
    }
}
