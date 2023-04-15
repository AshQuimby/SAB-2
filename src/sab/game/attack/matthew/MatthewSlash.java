package sab.game.attack.matthew;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class MatthewSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "matthew_slash.png";
        attack.life = 14;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 52;
        attack.drawRect.width = 52;
        attack.drawRect.height = 64;
        attack.damage = 14;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 15;
        attack.reflectable = false;

        offset = new Vector2(32, 4);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 3 == 0) attack.frame++;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(8 * attack.owner.direction, 2);
        SABSounds.playSound("swish.mp3");
    }
}
