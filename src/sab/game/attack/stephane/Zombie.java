package sab.game.attack.stephane;

import com.badlogic.gdx.math.Vector2;

import sab.game.*;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Zombie extends AttackType implements Hittable {
    private Attack attack;
    private Animation walkAnimation;
    private int hurtTime;

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        this.attack = attack;
        attack.imageName = "zombie.png";
        attack.basedOffCostume = false;

        attack.life = 400;
        attack.frameCount = 5;
        attack.hitbox.width = 28;
        attack.hitbox.height = 56;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.frame = 0;
        attack.damage = 2;
        attack.hitCooldown = 40;
        attack.parryable = true;
        attack.reflectable = false;
        attack.collideWithStage = true;

        walkAnimation = new Animation(0, 3, 15, true);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.y -= .96f;
        attack.velocity.scl(.95f);
        attack.knockback.set(attack.direction * 2, 3);

        attack.drawRect.x += 2 * attack.direction;
        attack.drawRect.y += 4;

        if (attack.collisionDirection == Direction.DOWN) {
            attack.velocity.y = 0;
        }

        if (--hurtTime >= 0) {
            attack.frame = 4;
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
        SabSounds.playSound("hit.mp3");
        return true;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return true;
    }
}