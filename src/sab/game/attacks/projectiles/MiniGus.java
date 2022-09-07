package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import sab.game.Direction;
import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;

public class MiniGus extends AttackType {
    private Animation walkAnimation;
    private Animation ventAnimation;

    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "mini_gus";
        if (attack.owner.costume != 0) {
            attack.imageName += "_alt_" + attack.owner.costume;
        }
        attack.imageName += ".png";
        
        attack.life = 200;
        attack.frameCount = 13;
        attack.hitbox.width = 32;
        attack.hitbox.height = 36;
        attack.drawRect.width = 32;
        attack.drawRect.height = 36;
        attack.frame = 0;
        attack.damage = 8;
        attack.hitCooldown = 86;
        attack.directional = true;
        attack.reflectable = false;
        attack.collideWithStage = true;
        
        walkAnimation = new Animation(0, 3, 7, true);
        ventAnimation = new Animation(4, 12, 5, false);
    }

    @Override
    public void update(Attack attack) {
        if (attack.life <= 50) {
            attack.canHit = false;
            attack.frame = ventAnimation.step();
            attack.velocity.set(0, 0);
        } else {
            attack.frame = walkAnimation.stepLooping();

            Player target = attack.getNearestOpponent(640);
            if (target != null) {
                float x = attack.hitbox.getCenter(new Vector2()).x;
                float targetX = target.hitbox.getCenter(new Vector2()).x;

                if (targetX > x) {
                    attack.velocity.x += .2f;
                } else if (targetX < x) {
                    attack.velocity.x -= .2f;
                }

                if (target.hitbox.y > attack.hitbox.y && attack.collisionDirection == Direction.DOWN && Math.abs(targetX - x) < 50) {
                    attack.velocity.y += 30;
                }
            }

            attack.velocity.y -= 1.2f;
            attack.velocity.scl(.95f);
            attack.knockback.set(attack.direction * 3, 10);
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.owner.direction * 20, 4));
        attack.direction = attack.owner.direction;
        attack.velocity.set(attack.direction * 4, 5);
    }

    @Override
    public void kill(Attack attack) {
        for (int i = 0; i < 5; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()).add(0, -12),
                    new Vector2(MathUtils.random(-1f, 1f), MathUtils.random(0f,
                            1f)), 16, 16, 14,
                    "smoke.png"));
        }
    }
}