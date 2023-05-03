    package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class WeakCharge extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "weak_charge.png";
        attack.life = 45;
        attack.hitbox.width = 8;
        attack.hitbox.height = 8;
        attack.drawRect.width = 20;
        attack.drawRect.height = 8;
        attack.damage = 6;
        attack.hitCooldown = 20;
        attack.frameCount = 4;
        attack.collideWithStage = true;
        attack.staticKnockback = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 0.5f, 0.25f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.frame = (attack.life % 5) / 4;
        attack.rotation = attack.velocity.angleDeg();
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
