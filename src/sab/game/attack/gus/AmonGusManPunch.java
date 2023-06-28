package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class AmonGusManPunch extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.life = 8;
        attack.velocity = new Vector2();
        attack.hitbox.width = 80;
        attack.hitbox.height = 64;
        attack.damage = 20;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 12;
        attack.reflectable = false;
        attack.parryable = false;

        offset = new Vector2(12, 8);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.owner.battle.shakeCamera(5);
        SabSounds.playSound("crunch.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(14 * attack.owner.direction, 8);
        attack.owner.battle.shakeCamera(5);
        SabSounds.playSound("john_step.mp3");
        if (data != null) attack.getBattle().addAttack(new Attack(new ProjectileGus(), attack.owner), new int[] { (int) attack.getCenter().x, (int) attack.getCenter().y });
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}