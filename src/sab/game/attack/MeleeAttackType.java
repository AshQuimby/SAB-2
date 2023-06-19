package sab.game.attack;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

public class MeleeAttackType extends AttackType {
    protected Vector2 offset;
    protected boolean usePlayerDirection;
    protected boolean killWhenPlayerStuck = true;

    protected final void moveToPlayer(sab.game.attack.Attack attack) {
        if (usePlayerDirection) attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.hitbox.x += offset.x * attack.owner.direction;
        attack.hitbox.y += offset.y;
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        if (offset != null) moveToPlayer(attack);
    }

    @Override
    public void update(Attack attack) {
        if (killWhenPlayerStuck && attack.owner.isStuck()) {
            attack.alive = false;
        }
        if (offset != null) moveToPlayer(attack);
    }

    @Override
    public void lateUpdate(Attack attack) {
    }
}