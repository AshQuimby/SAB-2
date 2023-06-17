package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class RadialBarrage extends BowlBoyShot {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "radial_barrage.png";
        attack.life = 24;
        attack.hitbox.width = 140;
        attack.hitbox.height = 140;
        attack.drawRect.width = 192;
        attack.drawRect.height = 192;
        attack.damage = 28;
        attack.frameCount = 6;
        attack.directional = false;
        attack.hitCooldown = 30;
        attack.reflectable = false;
        superMeterValue = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
    }

    @Override
    public void update(Attack attack) {
        if (attack.life % 4 == 0) attack.frame++;
        attack.canHit = attack.frame == 1;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.knockback = hit.getCenter().sub(attack.getCenter()).nor().scl(18);
    }
}
