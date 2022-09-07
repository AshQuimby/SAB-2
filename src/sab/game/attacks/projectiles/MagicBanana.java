package sab.game.attacks.projectiles;

import sab.game.attacks.Attack;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.animation.Animation;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;
import sab.net.Keys;

public class MagicBanana extends AttackType {

    private boolean controlled;
    private Animation anime = new Animation(new int[]{11, 12, 13}, 8, false);

    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "banana.png";
        attack.life = 360;
        attack.frameCount = 1;
        attack.hitbox.width = 96;
        attack.hitbox.height = 96;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 12;
        attack.frameCount = 2;
        attack.hitCooldown = 30;
        attack.directional = false;
        attack.collideWithStage = true;
    }

    @Override
    public void update(Attack attack) {

        attack.rotation -= attack.velocity.x;

        if (attack.collisionDirection != Direction.NONE) {
            if (attack.collisionDirection == Direction.UP || attack.collisionDirection == Direction.DOWN) {
                attack.velocity.y *= -0.9f;
            } else if (attack.collisionDirection == Direction.RIGHT || attack.collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -0.9f;
            }
        }

        if (controlled) {
            if (attack.owner.keys.isPressed(Keys.LEFT)) {
                attack.velocity.x -= 0.6f;
            }
            if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                attack.velocity.x += 0.6f;
            }
            if (attack.owner.keys.isPressed(Keys.UP)) {
                attack.velocity.y += 0.6f;
            }
            if (attack.owner.keys.isPressed(Keys.DOWN)) {
                attack.velocity.y -= 0.6f;
            }

            if (attack.owner.keys.isJustPressed(Keys.ATTACK) || attack.owner.stuckCondition()) {
                attack.velocity.scl(2);
                attack.damage += 8;
                controlled = false;
                if (attack.life > 30) attack.life = 30;
            } else {
                if (attack.owner.hasAction()) attack.owner.startAnimation(2, new Animation(new int[]{11, 12, 13}, 4, false), 2, false);
                attack.owner.frame = anime.stepLooping();
            }
        }

        attack.frame = controlled ? 0 : 1;

        attack.velocity.scl(0.96f);
        attack.knockback = attack.velocity.cpy().scl(0.5f);
    }

    @Override
    public void kill(Attack attack) {
        for (int i = 0; i < 4 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 32, 32, 0, "smoke.png"));
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.velocity.scl(2);
        if (attack.life > 30) attack.life = 30;
        controlled = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 64));
        attack.velocity = new Vector2(0, 5);
        attack.direction = attack.velocity.x > 0 ? 1 : -1;
        attack.knockback = new Vector2(6 * attack.owner.direction, 5);
        attack.damage += data[0] / 4;
        controlled = true;
    }
}
