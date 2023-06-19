package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;

public class Claw extends MeleeAttackType {
    int swingDirection;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "claw.png";
        attack.life = 5;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 24 * 4;
        attack.hitbox.height = 22 * 4;
        attack.drawRect.width = 17 * 4;
        attack.drawRect.height = 22 * 4;
        attack.damage = 16;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 20;
        attack.reflectable = false;

        offset = new Vector2(94, 0);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.frame = 5 - attack.life;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (hit instanceof Player player) {
            player.stun(15);
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        swingDirection = data[0];
        attack.knockback = new Vector2(5 * attack.owner.direction, 4);
        SABSounds.playSound("swish.mp3");
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        g.usefulDraw(
                g.imageProvider.getImage(attack.imageName),
                attack.drawRect.x,
                attack.drawRect.y,
                (int) attack.drawRect.width,
                (int) attack.drawRect.height,
                attack.frame,
                attack.frameCount,
                attack.rotation,
                attack.direction == 1,
                swingDirection == 1);
    }
}


