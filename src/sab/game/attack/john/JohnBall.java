package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;
import jdk.jshell.execution.Util;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;

public class JohnBall extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "john_ball.png";
        attack.life = 900;
        attack.frameCount = 4;
        attack.velocity = new Vector2();
        attack.hitbox.width = 112;
        attack.hitbox.height = 112;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 10;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 16;
        attack.collideWithStage = true;
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.velocity.y = -1;
    }

    @Override
    public void update(Attack attack) {
        attack.owner.stun(2);
        attack.owner.setIFrames(2);
        attack.owner.knockback = new Vector2();
        attack.owner.hide();
        attack.owner.hitbox.setCenter(attack.getCenter());
        if (attack.owner.keys.isPressed(Keys.LEFT) ^ attack.owner.keys.isPressed(Keys.RIGHT)) {
            attack.direction = attack.owner.keys.isPressed(Keys.LEFT) ? -1 : 1;
        }

        if (attack.owner.keys.isJustPressed(Keys.UP)) {
            attack.velocity.y = 24;
        }

        attack.knockback = new Vector2(12 * attack.direction, attack.velocity.y / 2);

        attack.frame = Math.abs(-attack.life / 2 % 4);

        attack.velocity.x += 0.6f * attack.direction;
        attack.velocity.y -= 0.9f;

        if (attack.collisionDirection.isHorizontal()) {
            SABSounds.playSound("crash.mp3");
            attack.getBattle().shakeCamera(9);
            attack.velocity.x *= -1.1f;
            attack.direction *= -1;
        }
        if (attack.collisionDirection.isVertical()) {
            SABSounds.playSound("crash.mp3");
            attack.getBattle().shakeCamera(9);
            attack.velocity.y *= -1.2f;
        }

        attack.velocity.x *= 0.98f;
        attack.velocity.y *= 0.98f;

        if (attack.owner.getCenter().y < attack.getStage().getStageEdge(Direction.DOWN)) {
            attack.hitbox.y += attack.getStage().getSafeBlastZone().height;
        }
        if (attack.owner.getCenter().x < attack.getStage().getStageEdge(Direction.LEFT)) {
            attack.hitbox.x += attack.getStage().getSafeBlastZone().width;
        } else if (attack.owner.getCenter().x > attack.getStage().getStageEdge(Direction.RIGHT)) {
            attack.hitbox.x -= attack.getStage().getSafeBlastZone().width;
        }
    }

    @Override
    public void onKill(Attack attack) {
        for (int i = 0; i < 24; i++) attack.getBattle().addParticle(new Particle(attack.getCenter(), Utils.randomParticleVelocity(16), 64, 64, "smoke.png"));
        attack.owner.velocity = attack.getStage().getSafeBlastZone().getCenter(new Vector2()).nor().scl(64).limit(attack.getStage().getSafeBlastZone().getCenter(new Vector2()).len());
        attack.owner.reveal();
    }
}
