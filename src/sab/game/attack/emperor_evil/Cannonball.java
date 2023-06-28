package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.SabRandom;

public class Cannonball extends AttackType {

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "cannonball.png";
        attack.life = 90;
        attack.hitbox.width = 32;
        attack.hitbox.height = 32;
        attack.drawRect.width = 32;
        attack.drawRect.height = 32;
        attack.damage = 22;
        attack.directional = true;
        attack.collideWithStage = true;
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.directional = true;

        if (attack.collisionDirection == Direction.UP || attack.collisionDirection == Direction.DOWN) {
            attack.velocity.x *= 0.96f;
            attack.velocity.y *= -0.5f;
            if (attack.life > 60) attack.life = 60;
        } else if (attack.collisionDirection == Direction.RIGHT || attack.collisionDirection == Direction.LEFT) {
            attack.velocity.x *= -1;
            attack.knockback.x *= -1;
            if (attack.life > 60) attack.life = 60;
        }

        if (attack.life <= 60) {
            attack.velocity.y -= 0.5f;
            attack.velocity.x *= 0.96f;
            attack.rotation -= 8 * attack.velocity.x / 4;
        }
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
        SabSounds.playSound("explosion.mp3");
    }

    @Override
    public void onKill(sab.game.attack.Attack attack) {
        for (int i = 0; i < 6 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 48, 48, 0, "smoke.png"));
        }
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        CollisionResolver.moveWithCollisions(attack.owner, new Vector2(attack.owner.direction * -8, 0), attack.owner.battle.getSolidStageObjects());
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(8 * attack.owner.direction, 20));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
        attack.knockback = new Vector2(8 * attack.owner.direction, 5.5f);
        if (attack.owner.keys.isPressed(Keys.ATTACK)) attack.owner.battle.addAttack(new Attack(new EvilSuck(), attack.owner), data);
        SabSounds.playSound("gunshot.mp3");
    }
}
