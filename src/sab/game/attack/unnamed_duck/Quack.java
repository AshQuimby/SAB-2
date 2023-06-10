package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class Quack extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "quack.png";
        attack.frameCount = 5;
        attack.hitbox = new Rectangle(0, 0, 36, 36);
        attack.drawRect = new Rectangle(0, 0, 44, 44);
        attack.life = 10;
        attack.hitCooldown = 10;
        attack.damage = 8;
        usePlayerDirection = true;
        offset = new Vector2(36, 16);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter().add(new Vector2(32 * attack.direction, 16)));
        attack.knockback = new Vector2(5 * attack.direction, 3);
        SABSounds.playSound("quack.mp3");
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 3 == 0) attack.frame++;
    }
}
