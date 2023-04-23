package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class BigBullet extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "bullet.png";
        attack.life = 100;
        attack.frameCount = 1;
        attack.hitbox.width = 8;
        attack.hitbox.height = 8;
        attack.drawRect.width = 8;
        attack.drawRect.height = 8;
        attack.damage = 20;
        attack.directional = true;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.updatesPerTick = 4;
    }

    @Override
    public void update(Attack attack) {
        attack.owner.battle.addParticle(new Particle(attack.getCenter(), new Vector2(0, MathUtils.random(-.2f, .2f)), 16, 16, 20, "smoke.png"));
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.knockback.set(attack.direction * 5, 4);
        CollisionResolver.moveWithCollisions(attack.owner, new Vector2(attack.owner.direction * -4, 0), attack.owner.battle.getSolidStageObjects());
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.direction = attack.owner.direction;
        attack.hitbox.x += attack.direction * 40;
        attack.hitbox.y += 14;
        attack.velocity = new Vector2(12 * attack.owner.direction, 0);

        attack.owner.battle.addParticle(new Particle(
            1.2f,
            attack.hitbox.getCenter(new Vector2()).add(4 * attack.direction, 0),
            new Vector2(MathUtils.random(-1f, 1f),
            MathUtils.random(4f, 10f)),
            16,
            8,
            12,
            "casing.png"));

        SABSounds.playSound("gunshot.mp3");
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}