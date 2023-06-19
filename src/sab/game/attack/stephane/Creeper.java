package sab.game.attack.stephane;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.GameObject;
import sab.game.*;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SABRandom;

public class Creeper extends AttackType implements Hittable {
    private Attack attack;
    private Animation walkAnimation;
    private Animation explodeAnimation;
    private int hurtTime;

    private boolean exploding;
    private boolean exploded;
    private int fuseTime;

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        this.attack = attack;
        attack.imageName = "creeper.png";
        attack.basedOffCostume = false;

        attack.life = 400;
        attack.frameCount = 9;
        attack.hitbox.width = 28;
        attack.hitbox.height = 56;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.frame = 0;
        attack.hitCooldown = 2;
        attack.canHit = false;
        attack.damage = 12;
        attack.parryable = true;
        attack.reflectable = false;
        attack.collideWithStage = true;

        walkAnimation = new Animation(0, 3, 8, true);
        explodeAnimation = new Animation(4, 5, 8, false);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.y -= .96f;
        attack.velocity.scl(.95f);

        attack.drawRect.x += 2 * attack.direction;
        attack.drawRect.y += 4;

        if (attack.collisionDirection == Direction.DOWN) {
            attack.velocity.y = 0;
        }

        if (exploded) {
            attack.alive = false;
            for (int i = 0; i < 6 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SABRandom.random(), 0).rotateDeg(SABRandom.random() * 360), 64, 64, 0, "fire.png"));
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SABRandom.random(), 0).rotateDeg(SABRandom.random() * 360), 96, 96, 0, "smoke.png"));
            }
            SABSounds.playSound("explosion.mp3");
            attack.getBattle().shakeCamera(7);
            return;
        }

        if (exploding) {
            attack.velocity.x = 0;
            attack.frame = explodeAnimation.stepLooping();
            if (++fuseTime > 30) {
                exploded = true;
                attack.canHit = true;
                attack.collideWithStage = false;
                attack.resize(200, 200);
            }

            return;
        }

        if (--hurtTime >= 0) {
            attack.frame = 7;
            return;
        }

        attack.velocity.x += .1f * attack.direction;
        attack.frame = walkAnimation.stepLooping();

        Player target = attack.getNearestOpponent(300);
        if (target != null) {
            float x = attack.hitbox.getCenter(new Vector2()).x;
            float targetX = target.hitbox.getCenter(new Vector2()).x;

            if (targetX > x) {
                attack.direction = 1;
            } else if (targetX < x) {
                attack.direction = -1;
            }

            if (target.hitbox.y > attack.hitbox.y && attack.collisionDirection == Direction.DOWN && Math.abs(targetX - x) < 50) {
                attack.velocity.y += 15;
            }

            if (target.getCenter().dst(attack.getCenter()) < 100) {
                exploding = true;
            }
        }

        for (Attack other : attack.getBattle().getAttacks()) {
            if (attack != other && attack.owner != other.owner) other.attemptHit(attack, this);
        }
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.owner.direction * 20, 4));
        attack.direction = attack.owner.direction;
        attack.velocity.set(attack.direction * 2, 0);
    }

    @Override
    public void onKill(Attack attack) {
    }

    @Override
    public boolean onHit(DamageSource source) {
        attack.life -= source.damage;
        if (attack.life <= 0) attack.alive = false;
        attack.velocity.add(source.knockback);
        hurtTime = 30;
        SABSounds.playSound("hit.mp3");
        return true;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return true;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        Vector2 direction = hit.hitbox.getCenter(new Vector2()).sub(attack.hitbox.getCenter(new Vector2())).nor();
        attack.knockback.set(direction).scl(10).add(0, 3);

        super.hit(attack, hit);
    }
}
