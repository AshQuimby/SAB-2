package sab.game.attack.stephane;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Baguette extends AttackType {
    private float swing;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "baguette.png";
        attack.life = 18;
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
        attack.drawAbovePlayers = true;

        swing = 90;
    }

    private void setRootPosition(Attack attack, Vector2 rootPosition) {
        Vector2 position = rootPosition.cpy();
        Vector2 corner = new Vector2(attack.hitbox.width / 2 * attack.direction, -attack.hitbox.height / 2);
        position.sub(corner.rotateDeg(attack.rotation - 90 * attack.direction));
        attack.hitbox.setCenter(position);
    }

    @Override
    public void update(Attack attack) {
        attack.rotation = swing * attack.direction;
        setRootPosition(attack, new Vector2(attack.owner.hitbox.x + attack.owner.hitbox.width / 2 + MathUtils.cosDeg(swing) * 20 * attack.direction - attack.direction * 12, attack.owner.hitbox.y + MathUtils.sinDeg(swing) * 20 + 24));

        swing -= 10;
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
