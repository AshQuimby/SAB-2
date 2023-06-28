package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class DownPunch extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "down_punch.png";
        attack.life = 13;
        attack.frameCount = 2;
        attack.velocity = new Vector2();
        attack.hitbox.width = 32;
        attack.hitbox.height = 8;
        attack.drawRect.width = 32;
        attack.drawRect.height = 12;
        attack.damage = 8;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 8;
        attack.reflectable = false;

        offset = new Vector2(0, -40);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 4 == 0) attack.frame++;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.owner.battle.shakeCamera(5);
        SabSounds.playSound("crunch.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(0, -7);
    }
}