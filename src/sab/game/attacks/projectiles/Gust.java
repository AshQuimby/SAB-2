package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Gust extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "gust.png";
        attack.life = 12;
        attack.frameCount = 3;
        attack.hitbox.width = 48;
        attack.hitbox.height = 36;
        attack.drawRect.width = 48;
        attack.drawRect.height = 96;
        attack.damage = 4;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life % 4 == 0) {
            attack.frame++;
        }

        for (GameObject gameObject : attack.owner.battle.getGameObjects()) {
            if (Attack.class.isAssignableFrom(gameObject.getClass()) && ((Attack) gameObject).reflectable) {
                if (((Attack) gameObject).owner != attack.owner
                        && ((Attack) gameObject).hitbox.overlaps(attack.hitbox)) {
                    ((Attack) gameObject).velocity.add(attack.direction, 0);
                    ((Attack) gameObject).owner = attack.owner;
                }
            }
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * 24, 0));
    }
}