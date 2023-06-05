package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.net.Keys;

public class SussyVent extends AttackType {
    private boolean bigManMode;
    private int ventCloseTime;
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
        ventCloseTime = 10;
    }

    @Override
    public void update(Attack attack) {
        if (ventCloseTime > 0) {
            ventCloseTime--;
            attack.frame = 1;
            if (attack.life >= 395) {
                attack.frame = 2;
            }
            if (bigManMode) attack.owner.frame = 36;
            else attack.owner.frame = 6;
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
                if (bigManMode) attack.velocity.y += 1f;
            }
            if (attack.owner.keys.isPressed(Keys.DOWN)) {
                attack.velocity.y -= 1f;
                if (bigManMode) attack.velocity.y -= 1f;
            }
            if (attack.owner.keys.isPressed(Keys.LEFT)) {
                attack.velocity.x -= 1f;
                if (bigManMode) attack.velocity.x -= 1f;
            }
            if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                attack.velocity.x += 1f;
                if (bigManMode) attack.velocity.x += 1f;
            }
            if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
                attack.life = 11;
            }

            attack.velocity.scl(.8f);
        } else {
            attack.velocity.set(0, 0);
            if (bigManMode) attack.owner.velocity.y = 14;
            else attack.owner.velocity.y = 5;
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
        if (data != null) {
            if (data[0] == 0) {
                attack.owner.velocity.y = 5;
                attack.life = 20;
            } else {
                attack.owner.velocity.y = 10;
                bigManMode = true;
                attack.life += 7;
            }
        } else {
            attack.owner.velocity.y = 5;
        }
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.reveal();
    }
}
