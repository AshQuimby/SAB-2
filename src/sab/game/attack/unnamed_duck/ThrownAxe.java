package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ThrownAxe extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "axe.png";
        attack.frameCount = 8;
        attack.hitbox = new Rectangle(0, 0, 48, 48);
        attack.drawRect = new Rectangle(0, 0, 48, 48);
        attack.life = 180;
        attack.damage = 20;
        attack.canHit = true;
        attack.parryable = true;
        attack.reflectable = true;
        attack.collideWithStage = true;
        attack.directional = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection == Direction.DOWN) {
            attack.alive = false;
        }

        attack.velocity.y -= .5f;
        if (attack.life % 4 == 0) {
            if (++attack.frame == 8) attack.frame = 0;
        }

        attack.knockback = new Vector2(attack.direction * 6, attack.velocity.y);
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.owner.fighter.itemOffset.x * attack.direction, attack.owner.fighter.itemOffset.y + 16));
        attack.velocity = new Vector2(attack.owner.direction * 9, 5);
    }
}

