package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;

import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ShadowTentacleGlob extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "shadow_tentacle_glob.png";
        attack.life = 200;
        attack.frameCount = 1;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 40;
        attack.reflectable = false;
        attack.parryable = false;
        attack.canHit = false;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * 50, -16));
        attack.velocity = new Vector2(12 * attack.owner.direction, 4);
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.96f;
        if (attack.collisionDirection == Direction.DOWN) {
            attack.alive = false;
            Vector2 center = attack.getCenter();
            for (int i = 0; i < 3; i++) {
                attack.getBattle().createAttack(new ShadowTentacle(), attack.owner, new int[] {
                        Float.floatToIntBits(center.x),
                        Float.floatToIntBits(center.y)
                });
            }
        }
    }
}

