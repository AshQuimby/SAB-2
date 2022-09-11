package sab.game.attacks.melees;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.net.Keys;

public class SussyVent extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "vent.png";
        attack.life = 400;
        attack.frameCount = 4;
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.drawRect.width = 64;
        attack.drawRect.height = 96;
        attack.collideWithStage = true;
        attack.reflectable = false;
        attack.canHit = false;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life >= 390) {
            attack.frame = 1;
            if (attack.life >= 395) attack.frame = 2;
            attack.owner.frame = 6;
            if (attack.owner.takingKnockback())
                attack.alive = false;
        } else if (attack.life > 10) {
            attack.owner.hide();
            attack.owner.stun(2);
            attack.owner.invulnerable = true;

            attack.frame = 0;
            attack.owner.usedRecovery = true;
            attack.owner.hitbox.setCenter(attack.hitbox.getCenter(new Vector2()));
            attack.owner.velocity.scl(0);

            if (attack.owner.keys.isPressed(Keys.UP)) {
                attack.velocity.y += 1f;
            }
            if (attack.owner.keys.isPressed(Keys.DOWN)) {
                attack.velocity.y -= 1f;
            }
            if (attack.owner.keys.isPressed(Keys.LEFT)) {
                attack.velocity.x -= 1f;
            }
            if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                attack.velocity.x += 1f;
            }
            if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
                attack.life = 11;
            }

            attack.velocity.scl(.8f);
        } else {
            attack.velocity.set(0, 0);
            attack.owner.velocity.y = 5;
            attack.owner.reveal();
            attack.owner.invulnerable = false;
            if (attack.life > 5) {
                attack.frame = 3;
            } else {
                attack.frame = 2;
            }
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.owner.velocity.x = 0;
        attack.owner.velocity.y = 5;
    }

    @Override
    public void kill(Attack attack) {
        attack.owner.reveal();
    }
}
