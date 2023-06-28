package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ShadowTentacleSegment extends AttackType {
    private int parentId;
    private boolean finalSegment;
    private float relativeAngle;
    private float angularVelocity;
    private float previousParentRotation;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "shadow_tentacle.png";
        attack.drawRect.setSize(48, 16);
        attack.life = 2;
        attack.frameCount = 2;
        attack.damage = 3;
        attack.hitCooldown = 10;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        GameObject parent = attack.getBattle().getGameObjectById(parentId);
        if (parent == null) {
            attack.alive = false;
            return;
        } else {
            attack.life = 2;
        }

        attack.hitbox.setCenter(parent.getCenter().add(MathUtils.cosDeg(parent.rotation) * 24, MathUtils.sinDeg(parent.rotation) * 24));
        attack.drawRect.setCenter(attack.getCenter());
        float angleDifference = -relativeAngle;
        angularVelocity += angleDifference / 50;
        relativeAngle += Math.min(5, Math.max(-5, angularVelocity));
        angularVelocity *= .9f;
        angularVelocity += (parent.rotation - previousParentRotation) * .1f;
        attack.rotation = parent.rotation + relativeAngle;

        previousParentRotation = previousParentRotation * .2f + parent.rotation * .8f;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.knockback.set(new Vector2(0, angularVelocity * .2f).rotateDeg(attack.rotation));
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        parentId = data[0];
        finalSegment = data[1] == 0;
        if (!finalSegment) {
            int[] newData = data.clone();
            newData[0] = attack.getBattle().getIdByGameObject(attack);
            newData[1]--;
            attack.getBattle().createAttack(new ShadowTentacleSegment(), attack.owner, newData);
        }
        attack.hitbox.setSize(finalSegment ? 24 : 0, finalSegment ? 24 : 0);
        attack.frame = finalSegment ? 0 : 1;
        attack.canHit = finalSegment;
        attack.parryable = finalSegment;

        relativeAngle = data[2] * 180;
    }
}
