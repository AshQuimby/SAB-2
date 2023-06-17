package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Kablooey extends BowlBoyShot {
    private int bounces;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "kablooey.png";
        attack.life = 100;
        attack.hitbox.width = 56;
        attack.hitbox.height = 56;
        attack.drawRect.width = 60;
        attack.drawRect.height = 60;
        attack.damage = 34;
        attack.frameCount = 0;
        attack.directional = true;
        attack.hitCooldown = 30;
        attack.collideWithStage = true;
        superMeterValue = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 16f, 6f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(10 * attack.owner.direction, 4);
    }

    @Override
    public void update(Attack attack) {
        attack.knockback.setAngleDeg(attack.velocity.angleDeg());
        attack.rotation -= attack.velocity.x;
        attack.velocity.y -= 0.5f;
        // Potentially make it squishy
        attack.drawRect.width = 60 - (int) (Math.abs(attack.velocity.y) * MathUtils.cosDeg(attack.rotation));
        attack.drawRect.height = 60 - (int) (Math.abs(attack.velocity.y) * MathUtils.sinDeg(attack.rotation));
        if (attack.collisionDirection.isNotNone()) {
            if (bounces < 2 && attack.collisionDirection.isVertical()) {
                attack.velocity.y *= -0.8f;
                attack.velocity.x *= 0.95f;
                bounces++;
            } else {
                attack.alive = false;
            }
        }
    }
}
