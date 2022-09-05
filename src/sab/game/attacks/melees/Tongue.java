package sab.game.attacks.melees;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Player;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Tongue extends AttackType {
    private Rectangle tipper;
    
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "tongue.png";
        attack.life = 14;
        attack.frameCount = 2;
        attack.velocity = new Vector2();
        attack.hitbox.width = 92;
        attack.hitbox.height = 8;
        attack.drawRect.width = 92;
        attack.drawRect.height = 8;
        attack.damage = 16;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 15;
        attack.reflectable = false;

        tipper = new Rectangle();
        tipper.width = 4;
        tipper.height = 4;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(20 * attack.owner.direction, 4));
        tipper.setCenter(attack.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 + tipper.width / 2), 2));

        if (attack.life <= 5)
            attack.frame = 2;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (tipper.overlaps(hit.hitbox) && attack.frame == 0 && hit instanceof Player) {
            ((Player) hit).knockback.setAngleDeg(new Vector2(15 * attack.direction, 4).angleDeg());
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(32 * attack.owner.direction, 4));
        attack.knockback = new Vector2(-6 * attack.owner.direction, 7);
    }
}
