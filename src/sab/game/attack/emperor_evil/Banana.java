package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class Banana extends AttackType {
    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "falling_banana.png";
        attack.life = 60;
        attack.frameCount = 1;
        attack.hitbox.width = 36;
        attack.hitbox.height = 36;
        attack.drawRect.width = 36;
        attack.drawRect.height = 36;
        attack.damage = 8;
        attack.directional = true;
        attack.collideWithStage = true;
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.y -= 1;

        attack.rotation -= (attack.velocity.x * (Math.abs(attack.velocity.y) + 0.1f)) / 3f;

        if (attack.collisionDirection != Direction.NONE) {
            if (attack.collisionDirection == Direction.UP || attack.collisionDirection == Direction.DOWN) {
                attack.velocity.x *= 0.8f;
                attack.velocity.y *= -0.5f;
            } else if (attack.collisionDirection == Direction.RIGHT || attack.collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -1;
                attack.knockback.x *= -1;
            }
        }
    }

    @Override
    public void kill(sab.game.attack.Attack attack) {
        for (int i = 0; i < 4 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 32, 32, 0, "smoke.png"));
        }
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(5 * MathUtils.random(-1f, 1f), 24);
        attack.direction = attack.velocity.x > 0 ? 1 : -1;
        attack.knockback = new Vector2(6 * attack.owner.direction, 5);
    }
}
