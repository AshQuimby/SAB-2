package sab.game.attack.matthew;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.MeleeAttackType;

public class MegaCounterSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "mega_slash.png";
        attack.basedOffCostume = true;
        attack.life = 20;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 128;
        attack.hitbox.height = 100;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 30;
        attack.reflectable = false;

        offset = new Vector2(32, 4);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life % 5 == 0) attack.frame++;
        attack.canHit = attack.frame == 2;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.damage = (int) (data[0] * 1.2f);
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(8 * attack.owner.direction, 5).scl(data[0] / 20f + 1);
        SABSounds.playSound("swish.mp3");
    }
}
