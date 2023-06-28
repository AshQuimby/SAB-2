package sab.game.attack.marvin;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.net.Keys;

public class Pipe extends AttackType {
    private boolean head; // (?!)
    private boolean turnt;
    private int pipeDirection;
    private int previousPipeDirection;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "pipe.png";
        attack.life = 480;
        attack.frameCount = 2;
        attack.hitbox.width = 56;
        attack.hitbox.height = 56;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.damage = 6;
        attack.direction = -1;
        attack.hitCooldown = 30;
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        if (data.length == 0) {
            pipeDirection = 0;
            head = true;
        } else {
            attack.life = 60 - data[5];
            pipeDirection = data[0];
            previousPipeDirection = data[4];
            turnt = data[1] == 1 ? true : false;
            attack.hitbox.x = data[2];
            attack.hitbox.y = data[3];
        }
        attack.knockback = new Vector2(-12, 0);
        SabSounds.playSound("crunch.mp3");
    }

    @Override
    public void update(Attack attack) {
        if (turnt) {
            attack.frame = 1;
            if (pipeDirection == 0 && previousPipeDirection == 1 || pipeDirection == 3 && previousPipeDirection == 2) {
                attack.rotation = 0;
            } else if (pipeDirection == 0 && previousPipeDirection == 3 || pipeDirection == 1 && previousPipeDirection == 2) {
                attack.rotation = 270;
            } else if (pipeDirection == 2 && previousPipeDirection == 3 || pipeDirection == 1 && previousPipeDirection == 0) {
                attack.rotation = 180;
            } else if (pipeDirection == 2 && previousPipeDirection == 1 || pipeDirection == 3 && previousPipeDirection == 0) {
                attack.rotation = 90;
            }
        } else {
            attack.rotation = 90 * pipeDirection;
            attack.frame = 0;
        }

        if (head) {
            attack.owner.hitbox.setCenter(attack.getCenter());
            attack.owner.setIFrames(2);
            attack.owner.hide();
            if (attack.owner.keys.isPressed(Keys.UP)) {
                if (previousPipeDirection != 2) {
                    pipeDirection = 0;
                }
            } else if (attack.owner.keys.isPressed(Keys.DOWN)) {
                if (previousPipeDirection != 0) {
                    pipeDirection = 2;
                }
            } else if (attack.owner.keys.isPressed(Keys.RIGHT)) {
                if (previousPipeDirection != 3) {
                    pipeDirection = 1;
                }
            } else if (attack.owner.keys.isPressed(Keys.LEFT)) {
                if (previousPipeDirection != 1) {
                    pipeDirection = 3;
                }
            }
            turnt = previousPipeDirection != pipeDirection;

            if (attack.life % 12 == 0) {
                createAttack(new Pipe(), new int[] { pipeDirection, turnt ? 1 : 0, (int) attack.hitbox.x, (int) attack.hitbox.y, previousPipeDirection, (480 - attack.life) / 10 }, attack.owner);
                switch (pipeDirection) {
                    case 0 :
                        attack.hitbox.y += 64;
                        break;
                    case 1 :
                        attack.hitbox.x += 64;
                        break;
                    case 2 :
                        attack.hitbox.y -= 64;
                        break;
                    case 3 :
                        attack.hitbox.x -= 64;
                        break;
                }
                previousPipeDirection = pipeDirection;
            }
        }
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.knockback = hit.getCenter().sub(attack.getCenter()).nor().scl(12);
        attack.knockback.y = Math.abs(attack.knockback.y);
    }

    @Override
    public void onKill(Attack attack) {
        if (head) {
            attack.owner.reveal();
            attack.owner.velocity.y = 16;
        }
    }
}
