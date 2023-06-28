package sab.game.attack.snas;

import com.badlogic.gdx.math.Vector2;

import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class BoneSpike extends AttackType {
    private boolean head;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "bone.png";
        attack.life = 60;
        attack.hitbox.width = 28;
        attack.hitbox.height = 56;
        attack.drawRect.width = 28;
        attack.drawRect.height = 56;
        attack.damage = 18;
        attack.hitCooldown = 15;
        attack.directional = false;
        attack.collideWithStage = false;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life % 15 == 0) {
            if (head) {
                attack.owner.battle.addAttack(new Attack(new BoneSpike(), attack.owner), new int[] {1, Float.floatToIntBits(attack.hitbox.x), Float.floatToIntBits(attack.hitbox.y), attack.direction});
                attack.hitbox.x += attack.direction * attack.hitbox.width * 1.5f;
            } else {
                attack.hitbox.y -= attack.hitbox.height / 2;
            }
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        if (data[0] == 0) {
            head = true;
            attack.direction = attack.owner.direction;
            attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(8 * attack.direction, -12));
            attack.knockback.set(attack.direction * 5, 2);
            for (int i = 0; i < 10; i++) {
                if (CollisionResolver.moveWithCollisions(attack, new Vector2(0, -56), attack.owner.battle.getSolidStageObjects()) == Direction.NONE) {
                    if (i == 9) attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(8 * attack.direction, -12));
                } else {
                    break;
                }
            }
        } else {
            head = false;
            attack.hitbox.x = Float.intBitsToFloat(data[1]);
            attack.hitbox.y = Float.intBitsToFloat(data[2]);
            attack.direction = data[3];
            attack.life = 30;
            attack.knockback.set(0, 4);
        }
    }
}
