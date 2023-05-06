package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ViceroyWings extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "viceroy_wings.png";
        attack.reflectable = false;
        attack.parryable = false;
        attack.life = 10;
        attack.frameCount = 5;
        attack.hitbox.width = 128;
        attack.hitbox.height = 128;
        attack.drawRect.width = 128;
        attack.drawRect.height = 128;
        attack.damage = 1;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 1;
        attack.collideWithStage = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        if (attack.life % 3 == 0) {
            if (++attack.frame >= 5) attack.frame = 0;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.owner.velocity.y = 32;
    }
}
