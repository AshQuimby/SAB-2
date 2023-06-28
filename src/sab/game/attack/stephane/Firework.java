package sab.game.attack.stephane;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Direction;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SabRandom;

public class Firework extends AttackType {

    private boolean exploded;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "firework.png";
        attack.life = 60;
        attack.frameCount = 0;
        attack.hitbox.width = 20;
        attack.hitbox.height = 20;
        attack.drawRect.width = 36;
        attack.drawRect.height = 20;
        attack.damage = 12;
        attack.hitCooldown = 1;
        attack.collideWithStage = true;
        attack.reflectable = true;
        exploded = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2), 0));
        attack.velocity = new Vector2(20 * attack.owner.direction, 0);
        attack.knockback = new Vector2(4 * attack.owner.direction, 1.5f);
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        if (!exploded) {
            attack.knockback.scl(2);
            exploded = true;
            attack.clearHitObjects();
        }
    }

    @Override
    public void onKill(Attack attack) {
        if (!exploded) {
            attack.alive = true;
            exploded = true;
        } else {
            for (int i = 0; i < 6 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 64, 64, 0, "fire.png"));
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 96, 96, 0, "smoke.png"));
            }
            SabSounds.playSound("explosion.mp3");
        }
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y += 0.3f;

        if (exploded) {
            attack.alive = false;
            attack.drawRect = new Rectangle(0, 0, 0, 0);
            attack.resize(120, 120);
            attack.damage = 16;
            attack.velocity = new Vector2();
            attack.knockback.scl(2);
        }

        if (attack.collisionDirection != Direction.NONE || attack.life == 1) {
            exploded = true;
        }

        if (SabRandom.random() > 0.5f) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(0, SabRandom.random(-.2f, .2f)), 16, 16, 20, "smoke.png"));
        }
        attack.rotation = attack.velocity.angleDeg();
    }

    // @Override
    // public void render(Attack attack, Seagraphics g) {
        
    // }
}
