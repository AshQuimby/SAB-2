package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Chaser extends AttackType {
    private Player target;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "chaser.png";
        attack.life = 180;
        attack.hitbox.width = 12;
        attack.hitbox.height = 12;
        attack.drawRect.width = 20;
        attack.drawRect.height = 20;
        attack.damage = 2;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.staticKnockback = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 0.5f, 0.25f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(12 * attack.owner.direction, 0);
        target = attack.getNearestOpponent(-1);
    }

    @Override
    public void update(Attack attack) {
        attack.velocity = attack.velocity.add(target.getCenter().sub(attack.getCenter()).nor().scl(0.5f)).scl(15 / 16f);
        attack.rotation = attack.velocity.angleDeg();
        if (attack.collisionDirection != Direction.NONE) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
