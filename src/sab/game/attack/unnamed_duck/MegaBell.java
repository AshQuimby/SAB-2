package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.Direction;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class MegaBell extends AttackType {
    private int bongCount;
    private int dingTime = 0;
    private int dongTime = 0;
    private float rotationSpeed;
    private boolean firstBell;
    private Vector2 origin;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "burrito_bell.png";
        attack.hitbox = new Rectangle(0, 0, 256, 256);
        attack.drawRect = new Rectangle(0, 0, 320, 336);
        attack.life = -1;
        attack.frameCount = 1;
        attack.hitCooldown = 10;
        attack.damage = 0;
        attack.rotation = 0;
        rotationSpeed = 0;
        attack.parryable = false;
        attack.reflectable = false;
        firstBell = true;
        bongCount = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(0, attack.getStage().getStageEdge(Direction.UP) + 336));
        origin = new Vector2(attack.hitbox.getCenter(new Vector2()));
    }

    @Override
    public void update(Attack attack) {
        if (origin.y > attack.getStage().getStageEdge(Direction.UP) - 96) {
            origin.y -= 2;
            if (origin.y <= attack.getStage().getStageEdge(Direction.UP) - 96) {
                attack.life = -1;
                dingTime = 86;
            }
        }
//        attack.hitbox.setSize(256, 256);
//        attack.hitbox.width += MathUtils.cosDeg(attack.rotation) * 64;
//        attack.hitbox.height += -MathUtils.cosDeg(attack.rotation) * 64;
        if (dingTime > 0) {
            dingTime--;
            rotationSpeed += attack.direction / 100f;
            if (dingTime == 0) {
                dong(attack);
                bongCount++;
                if (firstBell) {
                    firstBell = false;
                    dongTime = 86;
                } else {
                    dongTime = 120;
                }
            }
        } else if (dongTime > 0) {
            dongTime--;
            rotationSpeed -= attack.direction / 100f;
            if (dongTime == 0) {
                if (bongCount <= 5) {
                    dingTime = 120;
                    attack.direction *= -1;
                }
            }
        }

        if (bongCount >= 5) {
            attack.rotation *= 0.95f;
            rotationSpeed *= 0.95f;
            if (attack.life < 0) attack.life = 240;
            origin.y += 3;
        }

        attack.rotation += rotationSpeed;
        attack.damage = 12;
        attack.knockback = new Vector2(0, -12).rotateDeg(attack.rotation);
        attack.hitbox.setCenter(origin.cpy().sub(new Vector2(-MathUtils.sinDeg(attack.rotation), MathUtils.cosDeg(attack.rotation)).scl(336 / 2)));
    }

    private void dong(Attack attack) {
        SabSounds.playSound("burrito_bong.mp3");
        attack.getBattle().shakeCamera(8);
        for (Player player : attack.getBattle().getPlayers()) {
            if (player == attack.owner) return;
            attack.damage = 4;
            attack.knockback = new Vector2(0, 4);
            attack.forceAttemptHit(player, player);
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        attack.drawRect.setCenter(origin);
        attack.drawRect.setCenter(attack.drawRect.getCenter(new Vector2()).sub(new Vector2(-MathUtils.sinDeg(attack.rotation), MathUtils.cosDeg(attack.rotation)).scl(336 / 2)));
        super.render(attack, g);
    }
}
