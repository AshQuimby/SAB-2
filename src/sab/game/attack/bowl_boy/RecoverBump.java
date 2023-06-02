package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class RecoverBump extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.damage = 4;
        attack.staticKnockback = true;
        attack.hitCooldown = 4;
        attack.life = 30;
        attack.hitbox = new Rectangle(0, 0, 40, 40);
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.knockback = attack.owner.velocity.cpy().scl(3f);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}