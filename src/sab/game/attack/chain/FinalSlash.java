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
        attack.frameCount = 15;
        attack.velocity = new Vector2();
        attack.hitbox.width = 256;
        attack.hitbox.height = 256;
        attack.drawRect.width = 320;
        attack.drawRect.height = 320;
        attack.damage = 2;
        attack.direction = attack.owner.direction;
        attack.reflectable = false;
        attack.parryable = false;
        offset = new Vector2(96, 0);
        usePlayerDirection = true;
        swingSpeed = 5;
        killWhenPlayerStuck = false;
        attack.hitCooldown = 100;
        attack.staticKnockback = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.owner.touchingStage = false;
        attack.knockback = new Vector2(12f * attack.direction, 5f);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.owner.move(new Vector2(0, 0.25f));
        attack.owner.velocity.scl(0);
        attack.owner.stun(2);
        attack.owner.setIFrames(2);
        swingSpeed -= 0.01f;
        attack.owner.frame = 6;
        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) swingSpeed -= 0.05f;
        if (swingSpeed <= 2) swingSpeed = 2;
        if (attack.life % (int) Math.ceil(swingSpeed) == 0) {
            attack.clearHitObjects();
            if (++attack.frame >= attack.frameCount) {
                attack.frame = 0;
            }
        }
        attack.canHit = attack.frame == 2 || attack.frame == 6 || attack.frame == 12;
        if (attack.life < 30) attack.staticKnockback = false;
        if (attack.life == 1 && attack.frame != 14) {
            attack.life += swingSpeed;
        }
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (hit instanceof Player) {
            Player hitPlayer = (Player) hit;
            if (attack.life > 10) hitPlayer.stun(30);
            hitPlayer.hitbox.x += Math.signum(attack.getCenter().x + 64 * attack.direction - hitPlayer.getCenter().x) * 3;
            hitPlayer.hitbox.y += Math.signum(attack.getCenter().y - hitPlayer.getCenter().y) * 3;
        }
    }
}
