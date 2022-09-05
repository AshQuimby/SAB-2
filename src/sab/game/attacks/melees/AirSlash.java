package sab.game.attacks.melees;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Player;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.net.Keys;

public class AirSlash extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "air_slash.png";
        attack.life = 40;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 120;
        attack.hitbox.height = 64;
        attack.drawRect.width = 120;
        attack.drawRect.height = 64;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 4;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.owner.velocity.y += 1.5f;
        attack.owner.velocity.x *= 0.95f;
        attack.owner.velocity.y *= 0.98f;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 4));

        if (attack.owner.touchingStage) {
            attack.alive = false;
            attack.owner.resetAction();
        }

        attack.owner.frame = 6;
        if (attack.life % 2 == 0) attack.frame++;
        if (attack.life % 3 == 0) attack.owner.direction *= -1;
        if (attack.frame >= 6) attack.frame = 0;

        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) attack.owner.velocity.y += 5;
        if (attack.owner.keys.isPressed(Keys.RIGHT)) attack.owner.velocity.x += 0.5f;
        if (attack.owner.keys.isPressed(Keys.LEFT)) attack.owner.velocity.x -= 0.5f;

    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        hit.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2().add(MathUtils.sin(attack.frame * 2) * 4f, 0)));
        if (hit instanceof Player) ((Player) hit).stun(5); 
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.owner.velocity.y *= 0.05f;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 4));
        attack.owner.touchingStage = false;
        attack.knockback = new Vector2(0, 1);
    }
}
