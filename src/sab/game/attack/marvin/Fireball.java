package sab.game.attack.marvin;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class Fireball extends AttackType {
    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "fireball.png";
        attack.life = 180;
        attack.frameCount = 4;
        attack.hitbox.width = 32;
        attack.hitbox.height = 32;
        attack.drawRect.width = 32;
        attack.drawRect.height = 32;
        attack.damage = 10;
        attack.directional = true;
        attack.collideWithStage = true;
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.y -= 1;

        attack.rotation -= (attack.velocity.x * (Math.abs(attack.velocity.y) + 0.1f)) / 3f;

        if (attack.collisionDirection != Direction.NONE) {
            if (attack.collisionDirection == Direction.UP || attack.collisionDirection == Direction.DOWN) {
                attack.velocity.x *= 0.9f;
                attack.velocity.y *= -0.86f;
            } else if (attack.collisionDirection == Direction.RIGHT || attack.collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -1;
                attack.knockback.x *= -1;
            }
        }
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onKill(sab.game.attack.Attack attack) {
        for (int i = 0; i < 4 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 32, 32, 0, "fire.png"));
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
        attack.knockback = new Vector2(8 * attack.owner.direction, 4);
    }
}
