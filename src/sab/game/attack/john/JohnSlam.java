package sab.game.attack.john;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.MeleeAttackType;
import sab.net.Keys;

public class JohnSlam extends MeleeAttackType {
    private int jumpTime;
    private float slamSpeed;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.basedOffCostume = true;
        attack.life = -1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 72;
        attack.hitbox.height = 72;
        attack.drawRect.width = 20;
        attack.drawRect.height = 64;
        attack.damage = 4;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 12;
        attack.reflectable = false;
        attack.staticKnockback = true;

        offset = new Vector2(0, 4);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.owner.isStuck()) {
            attack.alive = false;
            return;
        }
        if (attack.life < 0) {
            if (attack.owner.keys.isPressed(Keys.LEFT)) {
                attack.owner.velocity.x -= 0.1f;
            }
            if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                attack.owner.velocity.x += 0.1f;
            }
            attack.move(new Vector2(0, -4));
            if (attack.collisionDirection == Direction.DOWN) {
                attack.life = 8;
                attack.hitbox.width = 168;
                moveToPlayer(attack);
                attack.clearHitObjects();
                SABSounds.playSound("crash.mp3");
                attack.owner.battle.shakeCamera(12);
            } else {
                if (jumpTime > 0) {
                    attack.owner.velocity.y += 0.5f;
                    if (jumpTime < 30) {
                        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
                            jumpTime = -1;
                        }
                    }
                } else {
                    attack.owner.velocity.y -= 1f;
                }
                jumpTime--;
            }
            if (attack.life > 0) {
                attack.owner.frame = 0;
            } else if (attack.owner.velocity.y > 0) {
                attack.owner.frame = 8;
            } else if (attack.owner.velocity.y < 0) {
                attack.owner.frame = 9;
            }
            slamSpeed = Math.max(-attack.owner.velocity.y, slamSpeed);
        }
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.resetAction();
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.owner.battle.shakeCamera(5);
        if (attack.life > 0) {
            attack.knockback = hit.getCenter().sub(attack.getCenter()).nor().scl(12);
            attack.damage = (int) slamSpeed / 2;
            attack.staticKnockback = false;
        } else if (jumpTime < 0) {
            attack.knockback = new Vector2(0, -8);
            attack.staticKnockback = false;
        }
        SABSounds.playSound("crunch.mp3");
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        jumpTime = 30;
        attack.owner.move(new Vector2(0, 4));
        attack.owner.velocity.y = 24;
        attack.knockback = new Vector2(0, 12);
        moveToPlayer(attack);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}