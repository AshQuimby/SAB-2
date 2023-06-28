package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ThrownIceCube extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "ice_cube.png";
        attack.frameCount = 1;
        attack.damage = 12;
        attack.hitbox = new Rectangle(0, 0, 36, 36);
        attack.drawRect = new Rectangle(0, 0, 40, 44);
        attack.life = 200;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.directional = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection.isNotNone()) {
            attack.alive = false;
        }

        attack.velocity.y -= .35f;
        attack.rotation -= attack.velocity.x * 4;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (hit instanceof Player) {
            Player hitPlayer = (Player) hit;
            hitPlayer.freeze(hitPlayer.damage / 2 + 30);
            attack.alive = false;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.owner.fighter.itemOffset.x * attack.direction, attack.owner.fighter.itemOffset.y));
        attack.velocity = new Vector2(attack.owner.direction * 6, 6);
        attack.knockback = new Vector2(4 * attack.owner.direction, 2);
    }
}
