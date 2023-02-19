package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Arrow extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "arrow.png";
        attack.life = 256;
        attack.frameCount = 0;
        attack.hitbox.width = 12;
        attack.hitbox.height = 12;
        attack.drawRect.width = 28;
        attack.drawRect.height = 12;
        attack.damage = 12;
        attack.hitCooldown = 10;
        attack.reflectable = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2), 0));
        attack.velocity = new Vector2(20 * attack.owner.direction, 2);
        attack.knockback = new Vector2(8 * attack.owner.direction, 5);
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.2f;
        attack.rotation = attack.velocity.angleDeg();
    }

    // @Override
    // public void render(Attack attack, Seagraphics g) {
        
    // }
}
