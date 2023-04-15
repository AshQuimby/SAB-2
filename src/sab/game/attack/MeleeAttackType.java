package sab.game.attack;

import com.badlogic.gdx.math.Vector2;

public class MeleeAttackType extends AttackType {
    protected Vector2 offset;
    protected boolean usePlayerDirection;

    private void moveToPlayer(sab.game.attack.Attack attack) {
        if (usePlayerDirection) attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.hitbox.x += offset.x * attack.owner.direction;
        attack.hitbox.y += offset.y;
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        moveToPlayer(attack);
    }

    @Override
    public void update(Attack attack) {
        moveToPlayer(attack);
    }
}