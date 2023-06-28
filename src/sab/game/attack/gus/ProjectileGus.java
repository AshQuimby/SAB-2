package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.SabRandom;

public class ProjectileGus extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "mini_gus.png";
        attack.basedOffCostume = true;

        attack.life = 200;
        attack.frameCount = 13;
        attack.hitbox.width = 32;
        attack.hitbox.height = 32;
        attack.drawRect.width = 32;
        attack.drawRect.height = 36;
        attack.frame = 1;
        attack.damage = 6;
        attack.hitCooldown = 86;
        attack.directional = true;
        attack.reflectable = false;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(data[0], data[1]);
        attack.velocity = new Vector2(24 * attack.direction, 4 * SabRandom.random(-1f, 1f));
        attack.knockback = new Vector2(4 * attack.direction, 2);
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.25f;
        attack.velocity.x *= 0.98f;
        attack.rotation -= attack.velocity.x * 0.9f;
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onKill(Attack attack) {
        attack.getBattle().addAttack(new Attack(new MiniGus(), attack.owner), new int[] { (int) attack.getCenter().x, (int) attack.getCenter().y });
    }
}