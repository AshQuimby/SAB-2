package sab.game.attack.big_seagull.god_seagull;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class GodBolt extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.drawRect.width = 56;
        attack.drawRect.height = 56;
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.frameCount = 6;
        attack.hitCooldown = 30;
        attack.damage = 8;
        attack.knockback = new Vector2(6, 0);
        attack.imageName = "god_bolt.png";
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(data[0], data[1]));
    }

    @Override
    public void update(Attack attack) {
        Player target = attack.getNearestOpponent(-1);
        attack.velocity.add(target.getCenter().sub(attack.getCenter()).limit(0.5f));
        attack.knockback.setAngleDeg(attack.velocity.angleDeg());
        attack.frame = Math.abs(5 - attack.life / 3 % 6);
    }
}
