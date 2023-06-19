package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.john.JohnBall;
import sab.game.particle.Particle;
import sab.util.SABRandom;

public class AngrySoulSpawner extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.canHit = false;
        attack.reflectable = false;
        attack.parryable = false;
        attack.life = 30;
    }

    @Override
    public void update(Attack attack) {
        Vector2 particleSpawnPos = attack.getCenter().add(new Vector2(SABRandom.random(-24, 24), SABRandom.random(-24, 24)));
        Vector2 particleVelocity = attack.getCenter().sub(particleSpawnPos).scl(0.025f);
        attack.getBattle().addParticle(new Particle(particleSpawnPos, particleVelocity, 16, 16, "spirit_bubble.png"));
    }

    @Override
    public void onKill(Attack attack) {
        Vector2 center = attack.getCenter();
        attack.getBattle().createAttack(new AngrySoul(), attack.owner, new int[] {
                Float.floatToIntBits(center.x),
                Float.floatToIntBits(center.y),
                attack.direction
        });
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.direction * -70 - SABRandom.random(0f, 72f) * attack.direction, SABRandom.random(-80f, 80f)));
    }
}
