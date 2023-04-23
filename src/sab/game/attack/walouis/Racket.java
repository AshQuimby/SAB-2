package sab.game.attack.walouis;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.action.PlayerAction;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Racket extends AttackType {

    private Rectangle oldHitbox;
    private boolean swung = false;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "badminton.png";
        attack.life = 90;
        attack.frameCount = 6;
        attack.velocity = new Vector2();
        attack.hitbox.width = 208;
        attack.hitbox.height = 116;
        attack.drawRect.width = 208;
        attack.drawRect.height = 116;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 4;
        attack.reflectable = false;
        attack.canHit = false;
        swung = false;
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setSize(208, 116);
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 24));
        attack.drawRect.setCenter(attack.hitbox.getCenter(new Vector2()));

        if (attack.owner.charging()) {
            attack.life = 24;
            attack.damage = attack.owner.getCharge() / 3 + 12;
            attack.knockback = new Vector2(1 * attack.owner.direction, 0.5f).scl(attack.owner.getCharge() / 12f + 7f);
            attack.owner.frame = 9;
        } else if (!swung) {
            attack.damage = attack.owner.getUsedCharge() / 3 + 12;
            attack.knockback = new Vector2(1 * attack.owner.direction, 0.5f).scl(attack.owner.getCharge() / 12f + 7f);
            attack.owner.startAnimation(24, new Animation(new int[]{9, 5, 4, 0}, 4, false), 8, false);
            swung = true;
        }

        attack.direction = attack.owner.direction;

        if (swung) {
            if (attack.life % 4 == 0) attack.frame++;
        }

        if (attack.frame > 5) attack.alive = false;

        if (attack.frame == 2) {
            attack.hitbox.set(attack.hitbox.x - 64 * attack.direction, attack.hitbox.y + 56, 120, 60);
            if (attack.direction == 1) attack.hitbox.x += 92;
        }

        if (attack.frame == 3) {
            attack.hitbox.set(attack.hitbox.x - 16 * attack.direction, attack.hitbox.y + 20, 52, 68);
            if (attack.direction == 1) attack.hitbox.x += 152;
        }

        if (attack.frame == 2 || attack.frame == 3) {
            for (Attack other : attack.owner.battle.getAttacks()) {
                if (other.reflectable) {
                    if (other.owner == attack.owner) continue;
                    if (other.hitbox.overlaps(attack.hitbox)) {
                        other.velocity.x *= -1;
                        other.knockback.x *= -1;
                        other.owner = attack.owner;
                        other.damage *= 1.5f;
                    }
                }
            }
            attack.canHit = true;
        }
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(0, 4));
        attack.owner.touchingStage = false;
        attack.damage = attack.owner.getCharge();
        attack.knockback = new Vector2(12 * attack.owner.direction, 0);
        attack.owner.startChargeAttack(new PlayerAction(6, new Animation(new int[]{9, 6, 5}, 4, false), false, 8), 12, 90);
        oldHitbox = new Rectangle();
        oldHitbox.set(attack.hitbox);
    }
}