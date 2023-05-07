package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class MegaBlastLarge extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "mega_blast_large.png";
        attack.life = 100;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 52;
        attack.drawRect.height = 40;
        attack.damage = 24;
        attack.frameCount = 3;
        attack.directional = true;
        attack.hitCooldown = 20;
        attack.collideWithStage = true;
        attack.staticKnockback = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.knockback.set(attack.direction * 10f, 6f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(12 * attack.owner.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.frame = attack.life / 4 % 3;
        if (attack.collisionDirection.isHorizontal()) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
