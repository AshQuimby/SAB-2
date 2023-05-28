package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.SABSounds;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;

public class EmptySoldierSlash extends MeleeAttackType {
    int swingDirection;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "screw.png";
        attack.drawAbovePlayers = true;
        attack.life = 6;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 72;
        attack.hitbox.height = 48;
        attack.drawRect.width = 13 * 4;
        attack.drawRect.height = 16 * 4;
        attack.damage = 5;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 7;
        attack.reflectable = false;

        offset = new Vector2(30, 0);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        attack.frame = Math.min(4, 6 - attack.life);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        swingDirection = data[0];
        attack.knockback = new Vector2(3 * attack.owner.direction, 4);
        SABSounds.playSound("swish.mp3");
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
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

