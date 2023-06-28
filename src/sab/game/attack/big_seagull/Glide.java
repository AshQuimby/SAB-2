package sab.game.attack.big_seagull;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;
import sab.util.SabRandom;

public class Glide extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.life = 180;
        attack.hitbox.width = 96;
        attack.hitbox.height = 96;
        attack.drawRect.width = 96;
        attack.drawRect.height = 96;
        attack.damage = 8;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 20;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.drawRect.getCenter(new Vector2()));
        attack.direction = attack.owner.direction;
        attack.owner.velocity.add(new Vector2(0, 1.3f * attack.life / 90).rotateDeg(attack.rotation));
        attack.owner.velocity.scl(0.9f);
        attack.owner.velocity.y += 0.5f;
        attack.owner.velocity.x *= 0.925f;
        if (attack.owner.keys.isPressed(Keys.RIGHT)) attack.rotation -= 4;
        if (attack.owner.keys.isPressed(Keys.LEFT)) attack.rotation += 4;
        attack.owner.rotation = attack.rotation;
        attack.owner.frame = 15;
        if (attack.life % 3 == 0) {
            attack.owner.battle.addParticle(new Particle(Utils.randomPointInRect(attack.owner.hitbox), new Vector2(SabRandom.random(-0.5f, 0.5f), SabRandom.random(-0.5f, 0.5f)), 40, 56, 3, "feather.png"));
        }
        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
            attack.alive = false;
            attack.owner.startAnimation(1, attack.owner.fighter.freefallAnimation, 6, false);
        }
        attack.owner.direction = (int) Math.signum(attack.owner.velocity.x);
        if (attack.owner.direction == 0) attack.owner.direction = 1;
        if (attack.owner.touchingStage || attack.owner.grabbingLedge()) {
            attack.owner.resetAction();
            attack.alive = false;
        }
        attack.knockback.set(attack.owner.velocity.cpy().nor().scl(6));
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 + attack.owner.hitbox.width / 2 + 4), 12));
        attack.knockback.set(attack.direction * 5, 5);
    }
}
