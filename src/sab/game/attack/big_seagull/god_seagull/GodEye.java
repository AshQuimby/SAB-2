package sab.game.attack.big_seagull.god_seagull;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.SabRandom;

public class GodEye extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.drawRect.width = 72;
        attack.drawRect.height = 72;
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.frameCount = 4;
        attack.hitCooldown = 30;
        attack.damage = 6;
        attack.life = 60;
        attack.knockback = new Vector2(6, 0);
        attack.imageName = "eye_of_god.png";
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(data[0], data[1]));
        attack.velocity = new Vector2(10, 0).rotateDeg(SabRandom.random(0, 360));
        attack.knockback = attack.velocity;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.scl(0.95f);
        attack.frame = Math.abs(3 - attack.life / 6 % 4);
    }

    @Override
    public void onKill(Attack attack) {
        for (int i = 0; i < 8; i++) {
            createAttack(new GodBoltMini(), new int[] { (int) attack.getCenter().x, (int) attack.getCenter().y, 45 * i }, attack.owner);
        }
    }
}
