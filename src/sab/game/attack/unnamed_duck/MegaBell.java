package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.MeleeAttackType;

public class MegaBell extends AttackType {
    private int dingTime = 0;
    private int dongTime = 0;
    private float rotationSpeed;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "burrito_bell.png";
        attack.hitbox = new Rectangle(0, 0, 300, 300);
        attack.drawRect = new Rectangle(0, 0, 320, 336);
        attack.life = -1;
        attack.frameCount = 1;
        attack.hitCooldown = 10;
        attack.damage = 0;
        rotationSpeed = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(0, attack.getStage().getStageEdge(Direction.UP) + 336));

    }

    @Override
    public void update(Attack attack) {
        if (attack.hitbox.y + attack.hitbox.height > attack.getStage().getStageEdge(Direction.UP) - 128) {
            attack.hitbox.y -= 2;
            if (attack.hitbox.y + attack.hitbox.height <= attack.getStage().getStageEdge(Direction.UP) - 128) {
                attack.life = 720;
                dingTime = 60;
            }
        }
        if (dingTime > 0) {
            dingTime--;
            rotationSpeed += attack.direction / 100f;
            if (dingTime == 0) {
                SABSounds.playSound("burrito_bong.mp3");
                dongTime = 120;
            }
        } else if (dongTime > 0) {
            dongTime--;
            rotationSpeed -= attack.direction / 100f;
            if (dongTime == 0) {
                dingTime = 120;
                attack.direction *= -1;
            }
        }

        attack.rotation += rotationSpeed;
    }
}
