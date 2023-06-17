package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class JumboRebound extends BowlBoyShot {
    private Vector2 target;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "jumbo_rebound.png";
        attack.life = 455;
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 48;
        attack.drawRect.height = 48;
        attack.damage = 14;
        attack.frameCount = 0;
        attack.directional = true;
        attack.hitCooldown = 30;
        superMeterValue = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback.set(attack.owner.direction * 7f, 3f);
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(14 * attack.owner.direction, 0);
        target = attack.getCenter().add(12 * 6 * attack.owner.direction, 0);
    }

    @Override
    public void update(Attack attack) {
        if (attack.getCenter().dst2(target) < 2304f) {
            target = attack.owner.getCenter();
        }
        attack.knockback.setAngleDeg(attack.velocity.angleDeg());
        attack.velocity = attack.velocity.add(target.cpy().sub(attack.getCenter()).nor()).scl(31 / 32f);
        attack.rotation -= 15 * attack.direction;
    }
}
