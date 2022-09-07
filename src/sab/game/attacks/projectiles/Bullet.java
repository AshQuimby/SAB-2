package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.SABSounds;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;

public class Bullet extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "bullet.png";
        attack.life = 100;
        attack.frameCount = 1;
        attack.hitbox.width = 8;
        attack.hitbox.height = 8;
        attack.drawRect.width = 8;
        attack.drawRect.height = 8;
        attack.damage = 4;
        attack.directional = true;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.updatesPerTick = 4;
    }

    @Override
    public void update(Attack attack) {
        attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()),
                new Vector2(0, MathUtils.random(-.2f, .2f)), 16, 16, 20, "smoke.png"));
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.direction * 1, 1);
        CollisionResolver.moveWithCollisions(attack.owner, new Vector2(attack.owner.direction * -4, 0), attack.owner.battle.getPlatforms());
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.hitbox.x += attack.direction * 20;
        attack.velocity = new Vector2(12 * attack.owner.direction, 0);

        attack.owner.battle.addParticle(new Particle(
            1.2f,
            attack.hitbox.getCenter(new Vector2()),
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