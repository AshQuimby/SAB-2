package sab.game.attack.big_seagull.god_seagull;

import com.badlogic.gdx.math.Vector2;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class GodBoltMini extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.drawRect.width = 32;
        attack.drawRect.height = 32;
        attack.hitbox.width = 24;
        attack.hitbox.height = 24;
        attack.hitCooldown = 10;
        attack.damage = 6;
        attack.knockback = new Vector2(6, 0);
        attack.imageName = "mini_bolt.png";
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(data[0], data[1]));
        attack.velocity = new Vector2(8, 0).setAngleDeg(data[2]);
    }
}
