package sab.game.attacks.melees;

import com.badlogic.gdx.math.Vector2;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class Peck extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "peck.png";
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

        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * 24, 0));
        attack.direction = attack.owner.direction;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * 24, 0));
    }
}