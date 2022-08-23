package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;


public class Frostball extends AttackType {
    boolean bounced;

    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "frostball.png";
        attack.life = 90;
        attack.frameCount = 1;
        attack.hitbox.width = 36;
        attack.hitbox.height = 36;
        attack.drawRect.width = 36;
        attack.drawRect.height = 36;
        attack.damage = 10;
        attack.directional = true;
        bounced = false;
    }

    @Override
    public void update(Attack attack) {
        Direction collisionDirection = CollisionResolver.movingResolve(attack, attack.owner.battle.getPlatforms());


        if (bounced) {
            attack.rotation += (1 + (90 - attack.life) / 60) * Math.signum(attack.velocity.x) * 12;
            attack.velocity.scl(0.92f);
        } else {
            attack.rotation += (1 + (90 - attack.life) / 60) * Math.signum(attack.velocity.x);
            attack.velocity.y -= 0.25f;
        }
        
        if (collisionDirection != Direction.NONE) {
            if (collisionDirection == Direction.UP || collisionDirection == Direction.DOWN) {
                attack.velocity.x *= 0.9f;
                attack.velocity.y *= -0.86f;
            } else if (collisionDirection == Direction.RIGHT || collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -1;
            }
            if (attack.life > 30) attack.life = 30;
            bounced = true;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void kill(Attack attack) {
        for (int i = 0; i < 8 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(2 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 64, 64, 0, "frostfire.png"));
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 48));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
        attack.knockback = new Vector2(8 * attack.owner.direction, 4);
        attack.damage = data[0];
        attack.resize(attack.hitbox.width + data[0] / 2, attack.hitbox.height + data[0] / 2);
        attack.drawRect.set(attack.hitbox);
    }
}
