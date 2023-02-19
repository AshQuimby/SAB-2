package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class DuckItem extends AttackType {

    private Vector2 offset;

    @Override
    public void setDefaults(Attack attack) {
        attack.hitbox = new Rectangle();
        attack.imageName = "none.png";

    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        offset = new Vector2(data[0], data[1]);
        attack.hitbox = new Rectangle(0, 0, data[2], data[3]);
        attack.hitbox.setCenter(attack.owner.getCenter().add(offset));
        attack.damage = data[4];
        attack.knockback = new Vector2(0, data[5]).rotateDeg(data[6]);
        attack.life = data[7];
        attack.hitCooldown = data[8];
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}
