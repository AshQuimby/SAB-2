package sab.game.attack.john;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class GavelSpin extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "gavel.png";
        attack.life = 15;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.drawRect.width = 40;
        attack.drawRect.height = 52;
        attack.damage = 14;
        attack.hitCooldown = 10;
        attack.reflectable = false;
        offset = new Vector2();
        killWhenPlayerStuck = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback = new Vector2(attack.owner.direction * 9, 4);
        attack.direction = attack.owner.direction;
    }

    @Override
    public void update(Attack attack) {
        if (attack.owner.touchingStage) {
            attack.owner.resetAction();
            attack.alive = false;
        }
        attack.owner.velocity.y *= 0.8f;
        attack.rotation = attack.life / 15f * 360 * attack.direction;
        attack.owner.rotation = attack.rotation - 180;
        attack.hitbox.setCenter(attack.owner.getCenter().add(MathUtils.sinDeg(-attack.rotation) * 40, MathUtils.cosDeg(-attack.rotation) * 40));
        attack.knockback.setAngleDeg(attack.rotation + 90);
    }

    @Override
    public void lateUpdate(Attack attack) {
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.rotation = 0;
        attack.owner.frame = 0;
    }
}
