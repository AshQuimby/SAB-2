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
        attack.drawRect.height = 36;
        attack.damage = 16;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 13;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        attack.frame = 3 - (int) (attack.life / 4f);

        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2())
                .add(attack.direction * (attack.hitbox.width / 2 + attack.owner.hitbox.width / 2 + 4), 12));
        attack.direction = attack.owner.direction;
        attack.knockback.set(attack.direction * 5, 5);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 + attack.owner.hitbox.width / 2 + 4), 12));
        attack.knockback.set(attack.direction * 5, 5);
    }
}