package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class VentSlap extends MeleeAttackType {
    private int previousFrame;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.life = 36;
        attack.velocity = new Vector2();
        attack.hitbox.width = 80;
        attack.hitbox.height = 24;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 8;
        attack.reflectable = false;
        attack.parryable = false;
        attack.staticKnockback = true;

        offset = new Vector2(40, -48);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.frame = attack.owner.frame;
        if (previousFrame != attack.frame) {
            if (attack.frame == 42) SabSounds.playSound("vent_open.mp3");
            else if (attack.frame == 45) SabSounds.playSound("vent_close.mp3");
        }
        attack.canHit = attack.frame == 42 || attack.frame == 45;
        if (attack.frame == 45) {
            attack.hitbox.width = 80;
            attack.hitbox.height = 96;
            offset = new Vector2(20, 4);
            moveToPlayer(attack);
            attack.staticKnockback = false;
            attack.knockback = new Vector2(14 * attack.direction, -6);
        }
        previousFrame = attack.frame;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.owner.battle.shakeCamera(8);
        SabSounds.playSound("crunch.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(-8 * attack.direction, 24);
        attack.owner.battle.shakeCamera(5);
        if (data != null) attack.getBattle().addAttack(new Attack(new ProjectileGus(), attack.owner), new int[] { (int) attack.getCenter().x, (int) attack.getCenter().y });
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}