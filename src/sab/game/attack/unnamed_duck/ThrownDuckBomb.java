package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SabRandom;

public class ThrownDuckBomb extends AttackType {
    private volatile boolean exploded;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "duck_bomb_item.png";
        attack.life = 240;
        attack.frameCount = 1;
        attack.hitbox.width = 28;
        attack.hitbox.height = 28;
        attack.drawRect.width = 28;
        attack.drawRect.height = 36;
        attack.damage = 5;
        attack.directional = true;
        attack.collideWithStage = true;
        exploded = false;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.3f;
        attack.velocity.x *= 0.99f;

        attack.rotation += 6 * attack.direction;

        if (exploded) {
            attack.alive = false;
            attack.drawRect = new Rectangle(0, 0, 0, 0);
            attack.resize(100, 100);
            attack.damage = 10;
            attack.velocity = new Vector2();
            attack.knockback.scl(4);
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
        } else {
            attack.alive = false;
        }
    }

    @Override
    public void onKill(Attack attack) {
        for (int i = 0; i < 6 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 64, 64, 0, "fire.png"));
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 96, 96, 0, "smoke.png"));
        }
        SabSounds.playSound("explosion.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.owner.fighter.itemOffset.x * attack.direction, attack.owner.fighter.itemOffset.y));
        attack.velocity = new Vector2(5 * attack.owner.direction, 10);
        attack.knockback = new Vector2(2 * attack.owner.direction, 3);
    }
}

