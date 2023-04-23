package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class AngrySoul extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "angry_soul.png";
        attack.life = 80;
        attack.frameCount = 4;
        attack.hitbox.width = 64;
        attack.hitbox.height = 24;
        attack.drawRect.width = 64;
        attack.drawRect.height = 24;
        attack.damage = 16;
        attack.hitCooldown = 10;
        attack.reflectable = true;
        attack.parryable = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.owner.direction * (attack.hitbox.width / 2), 0));
        attack.direction = attack.owner.direction;
        attack.velocity = new Vector2(12 * attack.direction, 0);
        attack.knockback = new Vector2(12 * attack.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y = MathUtils.sin(attack.life * MathUtils.PI2 * .05f) * 3;
        attack.frame = (attack.life % 16) / 4;
    }
}
