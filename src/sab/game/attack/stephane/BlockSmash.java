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
        attack.damage = 24;
        attack.knockback = new Vector2(0, 4);
        attack.hitCooldown = 30;
        attack.life = 30;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox = new Rectangle(0, 0, 64, 64);
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.hitbox.height += 192;
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}