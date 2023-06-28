package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class JohnPunch extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "john_punch.png";
        attack.life = 11;
        attack.frameCount = 3;
        attack.velocity = new Vector2();
        attack.hitbox.width = 40;
        attack.hitbox.height = 52;
        attack.drawRect.width = 20;
        attack.drawRect.height = 64;
        attack.damage = 34;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 12;
        attack.reflectable = false;

        offset = new Vector2(40, 0);
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
        attack.knockback = new Vector2(14 * attack.owner.direction, 6);
        SabSounds.playSound("crunch.mp3");
    }
}