package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ChaosOrbit extends AttackType {
    private int circlePosition;
    private int pierce;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "chaos_orbit.png";
        attack.life = 720;
        attack.hitbox.width = 20;
        attack.hitbox.height = 20;
        attack.drawRect.width = 28;
        attack.drawRect.height = 28;
        attack.damage = 8;
        attack.frameCount = 0;
        attack.directional = true;
        attack.hitCooldown = 20;
        pierce = 2;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        if (data[2] == 0) {
            for (int i = 0; i < 3; i++) {
                attack.owner.battle.addAttack(new Attack(new ChaosOrbit(), attack.owner), new int[]{ data[0], data[1], i + 1 });
            }
        }
        circlePosition = data[2];
    }

    @Override
    public void update(Attack attack) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter().add(new Vector2(0, 52).rotateDeg(circlePosition * 90 + attack.life * 20 * attack.direction)));
        attack.rotation = new Vector2(1, 0).rotateDeg(circlePosition * 90 + attack.life * 20 * attack.direction).angleDeg();
        attack.knockback = new Vector2(0, 6).rotateDeg(circlePosition * 90 + attack.life * 20 * attack.direction);
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        if (--pierce < 0) {
            attack.alive = false;
        }
    }
}
