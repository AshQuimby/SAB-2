package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Roundabout extends BowlBoyShot {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "roundabout.png";
        attack.life = 180;
        attack.hitbox.width = 20;
        attack.hitbox.height = 16;
        attack.drawRect.width = 28;
        attack.drawRect.height = 16;
        attack.damage = 8;
        attack.frameCount = 6;
        attack.hitCooldown = 20;
        attack.staticKnockback = true;
        attack.collideWithStage = true;
        superMeterValue = 2;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.knockback.set(attack.owner.direction * 5f, 2f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.frame = attack.life / 8 % 6;
        attack.velocity.y += 0.005f;
        attack.velocity.x -= 0.5f * attack.direction;
        attack.knockback.x = Math.abs(attack.knockback.x) * Math.signum(attack.velocity.x + 0.05f);
        if (attack.collisionDirection != Direction.NONE) {
            attack.alive = false;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
