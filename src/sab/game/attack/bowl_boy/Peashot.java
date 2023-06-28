package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;

public class Peashot extends BowlBoyShot {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "peashot.png";
        attack.life = 100;
        attack.hitbox.width = 8;
        attack.hitbox.height = 8;
        attack.drawRect.width = 16;
        attack.drawRect.height = 8;
        attack.damage = 4;
        attack.directional = true;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.staticKnockback = true;
        superMeterValue = 1;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.knockback.set(attack.direction * 0.5f, 0.25f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(16 * attack.owner.direction, 0);
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
