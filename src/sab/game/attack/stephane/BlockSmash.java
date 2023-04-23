package sab.game.attack.stephane;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class BlockSmash extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.damage = 12;
        attack.knockback = new Vector2(0, 8);
        attack.hitCooldown = 30;
        attack.life = 4;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox = new Rectangle(0, 0, 64, 64);
        attack.hitbox.setCenter(attack.owner.getCenter().add(0, -32));
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}