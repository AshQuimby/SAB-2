package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Spread extends BowlBoyShot {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "spread.png";
        attack.life = 15;
        attack.hitbox.width = 12;
        attack.hitbox.height = 12;
        attack.drawRect.width = 20;
        attack.drawRect.height = 20;
        attack.damage = 3;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.staticKnockback = true;
        superMeterValue = 1f;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = -attack.owner.direction;
        attack.knockback.set(attack.owner.direction * 0.5f, 0.25f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(0, 16);
        attack.rotation = data[2] * attack.direction;
        attack.velocity.rotateDeg(attack.rotation);
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
