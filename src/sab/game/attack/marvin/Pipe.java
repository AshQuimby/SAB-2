package sab.game.attack.marvin;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.net.Keys;

import java.security.Key;

public class Pipe extends AttackType {
    private boolean head; // (?!)
    private boolean turnt;
    private int pipeDirection;
    private int previousPipeDirection;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "pipe.png";
        attack.life = 360;
        attack.frameCount = 2;
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 60;
        attack.reflectable = false;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter());
        if (data.length == 0) {
            pipeDirection = 0;
            head = true;
        } else {
            attack.life = 60;
            pipeDirection = data[0];
            turnt = data[1] == 1 ? true : false;
            attack.hitbox.x = data[2];
            attack.hitbox.y = data[3];
        }
    }

    @Override
    public void update(Attack attack) {
        attack.rotation = 90 * pipeDirection;
        if (turnt) attack.frame = 1;
        else attack.frame = 0;

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

            if (attack.life % 30 == 0) {
                createAttack(new Pipe(), new int[] { pipeDirection, turnt ? 1 : 0, (int) attack.hitbox.x, (int) attack.hitbox.y }, attack.owner);
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
    public void onKill(Attack attack) {
        if (head) attack.owner.reveal();
    }
}
