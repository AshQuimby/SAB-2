package sab.game.attack.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class Bomb extends AttackType {

    private volatile boolean exploded;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "bomb.png";
        attack.life = 240;
        attack.frameCount = 4;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 40;
        attack.damage = 4;
        attack.directional = true;
        attack.collideWithStage = true;
        exploded = false;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.15f;
        attack.velocity.x *= 0.99f;

        attack.rotation -= 8 * attack.direction;
        
        if (exploded) {
            attack.alive = false;
            attack.drawRect = new Rectangle(0, 0, 0, 0);
            attack.resize(120, 120);
            attack.damage = 16;
            attack.velocity = new Vector2();
            attack.knockback.scl(8);
        }
    
        if (attack.collisionDirection != Direction.NONE) {
            exploded = true;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        if (!exploded) {
            exploded = true;
            attack.clearHitObjects();
        }
    }

    @Override
    public void kill(Attack attack) {
        if (!exploded) {
            attack.alive = true;
            exploded = true;
        } else {
            for (int i = 0; i < 6 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 64, 64, 0, "fire.png"));
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 96, 96, 0, "smoke.png"));
            }
            SABSounds.playSound("explosion.mp3");
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(9 * attack.owner.direction, 4);
        attack.knockback = new Vector2(1 * attack.owner.direction, 0.67f);
    }
}
