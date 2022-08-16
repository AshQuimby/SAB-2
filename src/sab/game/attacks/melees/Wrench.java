package sab.game.attacks.melees;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Wrench extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "wrench.png";
        attack.life = 15;
        attack.frameCount = 2;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 48;
        attack.drawRect.height = 48;
        attack.damage = 10;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 16;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(-48 * attack.owner.direction, 4));
        //attack.rotation += 10f;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(-48 * attack.owner.direction, 4));
        attack.knockback = new Vector2(8 * -attack.owner.direction, 4);
    }
}
