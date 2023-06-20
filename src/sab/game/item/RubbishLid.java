package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.unnamed_duck.DuckItem;

public class RubbishLid extends Item {
    private int useTime;

    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "rubbish_bin_lid.png";
        hitbox.width = 32;
        hitbox.height = 60;
        drawRect = new Rectangle(hitbox);
        useTime = 0;
        uses = 12;
    }

    @Override
    public void updateHeld(Player holder) {
        if (useTime > 0) {
            useTime--;
            offset.x = 8;
            offset.y = 4;
            for (Attack attack : holder.battle.getAttacks()) {
                if (attack.reflectable && attack.hitbox.overlaps(hitbox) && attack.owner != holder) {
                    attack.velocity.x *= -1;
                    attack.knockback.x *= -1;
                    attack.owner = holder;
                    SABSounds.playSound("shield_bounce.mp3");
                }
            }
            if (useTime == 0) uses--;
        } else {
            offset.x = 0;
            offset.y = 0;
        }
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        uses++;
        useTime = 10;
        Vector2 totalOffset = getTotalOffset(holder);
        holder.fighter.idleAnimation.reset();
        holder.startAttack(new DuckItem(), holder.fighter.idleAnimation, 4, 8, false, new int[] {
                (int) totalOffset.x,
                (int) totalOffset.y,
                (int) hitbox.width,
                (int) hitbox.height,
                8,
                4,
                -40 * holder.direction,
                4,
                10
        });
    }
}
