package sab.game.attack.snas;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Direction;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SabRandom;

public class SpinnyBone extends AttackType {
    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "spinny_bone.png";
        attack.basedOffCostume = true;
        attack.life = 60;
        attack.frameCount = 1;
        attack.hitbox.width = 36;
        attack.hitbox.height = 36;
        attack.drawRect.width = 36;
        attack.drawRect.height = 56;
        attack.damage = 14;
        attack.collideWithStage = true;
        attack.directional = false;
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.rotation -= Math.abs(attack.velocity.x) * attack.direction;

        if (attack.collisionDirection != Direction.NONE) {
            if (attack.collisionDirection == Direction.UP || attack.collisionDirection == Direction.DOWN) {
                attack.velocity.y *= -1f;
            } else if (attack.collisionDirection == Direction.RIGHT || attack.collisionDirection == Direction.LEFT) {
                attack.velocity.x *= -1;
            }
        }


        if (attack.life <= 30) {
            attack.velocity.add(attack.owner.hitbox.getCenter(new Vector2()).sub(attack.hitbox.getCenter(new Vector2())).nor().scl(2));
            attack.velocity.scl(0.98f);
            if (attack.hitbox.getCenter(new Vector2()).dst(attack.owner.hitbox.getCenter(new Vector2())) <= 32) {
                attack.alive = false;
            }
        } else {
            attack.velocity.rotateDeg(-8 * attack.direction);
        }


        attack.knockback.setAngleDeg(attack.velocity.angleDeg());
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onKill(sab.game.attack.Attack attack) {
        for (int i = 0; i < 4 ; i++) {
            attack.owner.battle.addParticle(new Particle(1, attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), SabRandom.random(0.5f, 0.5f)).rotateDeg(SabRandom.random() * 360), 24, 24, 2, "bone_particle.png"));
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(4 * attack.owner.direction, 8);
        attack.knockback = new Vector2(0, 8);
        attack.direction = attack.owner.direction;
        SabSounds.playSound("snas.mp3");
    }
}
