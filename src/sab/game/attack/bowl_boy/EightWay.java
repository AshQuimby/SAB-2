package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class EightWay extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "eight_way.png";
        attack.life = 120;
        attack.hitbox.width = 36;
        attack.hitbox.height = 36;
        attack.drawRect.width = 40;
        attack.drawRect.height = 48;
        attack.damage = 8;
        attack.frameCount = 0;
        attack.directional = true;
        attack.hitCooldown = 30;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        if (data[2] == 0) {
            for (int i = 0; i < 7; i++) {
                attack.owner.battle.addAttack(new Attack(new EightWay(), attack.owner), new int[]{ data[0], data[1], i + 1 });
            }
        }
        attack.rotation = 45 * data[2];
        attack.hitbox.setCenter(attack.owner.getCenter().add(data[0], data[1]));
        attack.velocity = new Vector2(0, 8).rotateDeg(attack.rotation);
        attack.knockback = new Vector2(0, 6).rotateDeg(attack.rotation);
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection.isNotNone()) attack.alive = false;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }
}
