package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class ShadowPlunge extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "shadow_plunge.png";
        attack.reflectable = false;
        attack.parryable = false;
        attack.life = 320;
        attack.frameCount = 5;
        attack.hitbox.width = 64;
        attack.hitbox.height = 128;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 6;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 12;
        attack.collideWithStage = false;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 1f;
        attack.owner.hitbox.setCenter(attack.hitbox.getCenter(new Vector2()));
        attack.frame = (attack.life / 5) % 5;
        attack.owner.frame = 7;
        attack.drawRect.y += 28;

        if (attack.owner.touchingStage) {
            attack.hitbox.width = 128;
            attack.hitbox.height = 64;
            attack.life = 1;
            attack.clearHitObjects();
        }

        if (attack.life == 1) {
            for (int i = 0; i < 20; i++) {
                //attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 128, 128, 0, "shadowling.png"));
            }
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.owner.velocity.y = 5;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        Vector2 direction = hit.hitbox.getCenter(new Vector2()).sub(attack.hitbox.getCenter(new Vector2()));
        attack.knockback.set(direction.nor().scl(attack.life == 1 ? 10 : 1));
        super.hit(attack, hit);
    }
}
