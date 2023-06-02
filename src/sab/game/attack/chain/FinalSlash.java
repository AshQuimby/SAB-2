package sab.game.attack.chain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.CollisionResolver;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.Utils;

public class FinalSlash extends MeleeAttackType {
    private float swingSpeed;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "chain_final_slash.png";
        attack.life = 360;
        attack.frameCount = 13;
        attack.velocity = new Vector2();
        attack.hitbox.width = 224;
        attack.hitbox.height = 224;
        attack.drawRect.width = 256;
        attack.drawRect.height = 256;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 4;
        attack.reflectable = false;
        attack.parryable = false;
        attack.staticKnockback = true;
        offset = new Vector2(128, 0);
        usePlayerDirection = true;
        swingSpeed = 12;
        killWhenPlayerStuck = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.owner.velocity.y *= 0.05f;
        attack.owner.touchingStage = false;
        attack.knockback = new Vector2(12 * attack.direction, 8f);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.hitCooldown = Math.round(swingSpeed + 4);
        attack.owner.velocity.scl(0);
        attack.owner.stun(2);
        attack.owner.setIFrames(2);
        swingSpeed -= 0.125f;
        attack.owner.frame = 6;
        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) swingSpeed -= 0.125f;
        if (attack.life % Math.ceil(swingSpeed) == 0 && ++attack.frame >= attack.frameCount) attack.frame = 0;
        attack.canHit = attack.frame == 2 || attack.frame == 7 || attack.frame == 11;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        CollisionResolver.moveWithCollisions(hit, attack.owner.hitbox.getCenter(new Vector2()).sub(hit.hitbox.getCenter(new Vector2())).scl(0.75f), attack.owner.battle.getSolidStageObjects());
        if (hit instanceof Player) ((Player) hit).stun(2);
        if (attack.life <= 4) {
            attack.staticKnockback = false;
            attack.knockback = new Vector2(0, 8).rotateDeg(MathUtils.random(-1f, 1f) * 16);
        }
        for (int i = 0; i < 4; i++) {
            attack.owner.battle.addParticle(new Particle(0.1f, hit.getCenter(), Utils.randomParticleVelocity(8), 32, 32, 0.9f, 0, "blood.png"));
        }
    }
}
