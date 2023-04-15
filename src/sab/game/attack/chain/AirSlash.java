package sab.game.attack.chain;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.CollisionResolver;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.net.Keys;

public class AirSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "air_slash.png";
        attack.life = 40;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 96;
        attack.hitbox.height = 48;
        attack.drawRect.width = 120;
        attack.drawRect.height = 64;
        attack.damage = 4;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 4;
        attack.reflectable = false;

        offset = new Vector2(0, 8);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.owner.velocity.y += 1.5f;
        attack.owner.velocity.x *= 0.95f;
        attack.owner.velocity.y *= 0.93f;

        if (attack.owner.touchingStage || attack.owner.isStuck()) {
            attack.alive = false;
            attack.owner.resetAction();
        }

        attack.owner.frame = 6;
        if (attack.life % 2 == 0) attack.frame++;
        if (attack.life % 3 == 0) attack.owner.direction *= -1;
        if (attack.frame >= 6) attack.frame = 0;

        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) attack.owner.velocity.y += 1.5f;
        if (attack.owner.keys.isPressed(Keys.RIGHT)) attack.owner.velocity.x += 0.5f;
        if (attack.owner.keys.isPressed(Keys.LEFT)) attack.owner.velocity.x -= 0.5f;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        CollisionResolver.moveWithCollisions(hit, attack.owner.hitbox.getCenter(new Vector2()).sub(hit.hitbox.getCenter(new Vector2())).scl(0.75f), attack.owner.battle.getSolidStageObjects());
        if (hit instanceof Player) ((Player) hit).stun(4);
        if (attack.life <= 4) {
            attack.knockback = new Vector2(0, 8).rotateDeg(MathUtils.random(-1f, 1f) * 16);
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.owner.velocity.y *= 0.05f;
        attack.owner.touchingStage = false;
        attack.knockback = new Vector2(0, 1);
    }
}
