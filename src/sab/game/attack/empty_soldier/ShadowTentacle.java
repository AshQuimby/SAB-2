package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.SABRandom;

public class ShadowTentacle extends AttackType {
    private float angularVelocity;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "shadow_tentacle.png";
        attack.hitbox.setSize(0, 0);
        attack.drawRect.setSize(48, 16);
        attack.frameCount = 2;
        attack.frame = 1;
        attack.life = 1000;
        attack.rotation = 90;
        attack.canHit = false;
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void update(Attack attack) {
        angularVelocity += SABRandom.random(-1f, 1f);
        attack.rotation += angularVelocity;
        if (attack.rotation > 135) attack.rotation = 135;
        else if (attack.rotation < 45) attack.rotation = 45;
        angularVelocity *= .9f;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(Float.intBitsToFloat(data[0]), Float.intBitsToFloat(data[1]) - 24));
        attack.getBattle().createAttack(new ShadowTentacleSegment(), attack.owner, new int[] {
                attack.getBattle().getIdByGameObject(attack), 10, SABRandom.randomBoolean(.5f) ? 1 : -1
        });
    }
}
