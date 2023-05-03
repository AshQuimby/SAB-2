    package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class StrongCharge extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "strong_charge.png";
        attack.life = 90;
        attack.hitbox.width = 16;
        attack.hitbox.height = 16;
        attack.drawRect.width = 40;
        attack.drawRect.height = 16;
        attack.damage = 24;
        attack.hitCooldown = 20;
        attack.frameCount = 8;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 8f, 3f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(14 * attack.owner.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.frame = attack.life / 8 % 8;
        attack.rotation = attack.velocity.angleDeg();
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}