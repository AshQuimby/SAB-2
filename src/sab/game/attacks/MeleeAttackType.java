package sab.game.attacks;

import com.badlogic.gdx.math.Vector2;

public class MeleeAttackType extends AttackType {
    protected Vector2 offset;
    protected boolean usePlayerDirection;

    private void moveToPlayer(Attack attack) {
        if (usePlayerDirection)
            attack.direction = attack.owner.direction;
        attack.hitbox.x = attack.owner.hitbox.x + attack.owner.hitbox.width / 2 + attack.direction * offset.x - attack.hitbox.width / 2;
        attack.hitbox.y = attack.owner.hitbox.y + attack.owner.hitbox.height / 2 + offset.y - attack.hitbox.height / 2;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        moveToPlayer(attack);
    }

    @Override
    public void update(Attack attack) {
        moveToPlayer(attack);
    }
}