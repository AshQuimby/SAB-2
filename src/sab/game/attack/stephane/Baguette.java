package sab.game.attack.stephane;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Baguette extends AttackType {
    private float swing;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "baguette.png";
        attack.life = 10;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 48;
        attack.drawRect.height = 48;
        attack.damage = 7;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;

        swing = 0;
    }

    @Override
    public void update(Attack attack) {
        attack.rotation = -90 - (swing - 45) * attack.direction;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(12 * attack.direction, 0));
        swing += 16;

        attack.hitbox.x += MathUtils.cos((attack.rotation + 45) * MathUtils.degreesToRadians) * 12;
        attack.hitbox.y += MathUtils.sin((attack.rotation + 45) * MathUtils.degreesToRadians) * 12;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(16 * attack.owner.direction, -4));
        attack.knockback = new Vector2(attack.owner.direction * 7, 3);
    }
}
