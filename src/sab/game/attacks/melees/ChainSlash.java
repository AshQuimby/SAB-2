package sab.game.attacks.melees;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class ChainSlash extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "chain_slash.png";
        attack.life = 8;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(32 * attack.owner.direction, 4));

        if (attack.life % 2 == 0) attack.frame++;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(32 * attack.owner.direction, 4));
        attack.knockback = new Vector2(8 * attack.owner.direction, 4);
    }
}
