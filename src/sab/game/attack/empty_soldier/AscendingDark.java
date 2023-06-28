package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;
import sab.util.SabRandom;

public class AscendingDark extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.life = 100;
        attack.hitbox.width = 128;
        attack.hitbox.height = 128;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 8;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.drawRect.getCenter(new Vector2()));
        attack.direction = attack.owner.direction;
        attack.owner.velocity.add(new Vector2(2f, 0).rotateDeg(attack.rotation));
        attack.owner.velocity.scl(0.9f);
        if (attack.life >= 50) {
            if (attack.rotation < 90) attack.rotation += Math.min(90 - attack.rotation, 2f);
            if (attack.rotation > 90) attack.rotation -= Math.min(attack.rotation - 90, 2f);
        } else {
            attack.rotation -= attack.direction * 2f;
            if (attack.rotation < 0) attack.rotation = 0;
            if (attack.rotation > 180) attack.rotation = 180;
        }
        attack.owner.rotation = attack.rotation + (attack.direction == -1 ? 180 : 0);
        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
            attack.life = Math.min(attack.life, 49);
        }
        if (attack.owner.direction == 0) attack.owner.direction = 1;
        if (attack.owner.touchingStage || attack.owner.grabbingLedge()) {
            attack.owner.resetAction();
            attack.alive = false;
        }
        attack.knockback.set(attack.owner.velocity.cpy().nor().scl(12));

        Vector2 particlePosition = Utils.randomPointInRect(attack.hitbox);
        Vector2 particleVelocity = new Vector2(SabRandom.random(-3f, 3f), -SabRandom.random(1f, 4f));
        Particle particle = new Particle(particlePosition, particleVelocity, 32, 32, 7, 5, particleVelocity.x > 0 ? 1 : -1, "shadowling.png");
        attack.owner.battle.addParticle(particle);
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.rotation = 0;
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 + attack.owner.hitbox.width / 2 + 4), 12));
        attack.knockback.set(attack.direction * 5, 5);
        attack.rotation = attack.owner.direction == -1 ? 180 : 0;
    }
}

