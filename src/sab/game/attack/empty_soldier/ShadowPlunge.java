package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;
import sab.util.SABRandom;

public class ShadowPlunge extends MeleeAttackType {
    private boolean hitGround;

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
        offset = new Vector2(0, 0);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.owner.velocity.y -= 2.5f;
        attack.owner.velocity.y *= 0.98f;
        attack.frame = (attack.life / 5) % 5;
        attack.owner.frame = 6;
        attack.drawRect.y += 28;

        if (attack.owner.touchingStage && !hitGround) {
            attack.hitbox.width = 160;
            attack.hitbox.height = 80;
            attack.hitbox.setCenter(attack.owner.getCenter());
            attack.life = 3;
            attack.clearHitObjects();

            hitGround = true;
        }

        if (attack.life % 3 == 0) {
            Vector2 particlePosition = attack.getCenter().add(SABRandom.random(-8f, 8f), SABRandom.random(-8f, 8f));
            Vector2 particleVelocity = new Vector2(SABRandom.random(-3f, 3f), SABRandom.random(1f, 4f));
            Particle particle = new Particle(particlePosition, particleVelocity, 32, 32, 6, 5, particleVelocity.x > 0 ? 1 : -1, "shadowling.png");
            attack.owner.battle.addParticle(particle);
        }

        if (attack.life == 3) {
            shadowExplosion(attack);
        }
    }

    private void shadowExplosion(Attack attack) {
        for (int i = 0; i < 12; i++) {
            Vector2 particlePosition = new Vector2(SABRandom.random(attack.owner.hitbox.x, attack.owner.hitbox.x + attack.owner.hitbox.width), attack.owner.hitbox.y);
            Vector2 particleVelocity = new Vector2(SABRandom.random(-7f, 7f), SABRandom.random(-2f, 3f));
            Particle particle = new Particle(particlePosition, particleVelocity, 32, 32, 6, 5, particleVelocity.x > 0 ? 1 : -1, "shadowling.png");
            attack.owner.battle.addParticle(particle);
        }
        attack.getBattle().shakeCamera(8);
        SABSounds.playSound("crash.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.owner.velocity.y = 4;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        Vector2 direction = hit.hitbox.getCenter(new Vector2()).sub(attack.hitbox.getCenter(new Vector2())).nor();
        if (hitGround) {
            attack.knockback.set(direction.x * 12, -4);
        } else {
            if (hit instanceof Player) {
                Player hitPlayer = (Player) hit;
                if (!hitPlayer.touchingStage) {
                    attack.alive = false;
                    attack.owner.velocity.y = 14;
                    shadowExplosion(attack);
                }
            }
            attack.knockback.set(0, -14);
        }

        super.hit(attack, hit);
    }
}
