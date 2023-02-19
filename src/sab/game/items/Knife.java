package sab.game.items;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.melees.DuckItem;

public class Knife extends Item {

    private Animation swing;
    private Animation animation;
    private int uses;

    public void setDefaults() {
        super.setDefaults();
        imageName = "knife_item.png";
        frameCount = 7;
        hitbox.width = 72;
        hitbox.height = 92;
        drawRect = new Rectangle(hitbox);
        swing = new Animation(1, 6, 2, true);
        animation = new Animation(new int[]{0}, 1, true);
        uses = 10;
    }

    @Override
    public void updateHeld(Player holder) {
        frame = animation.step();
        if (animation.isDone()) animation = new Animation(new int[]{0}, 1, true);
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        if (animation == swing) return;
        swing.reset();
        animation = swing;
        Vector2 totalOffset = getTotalOffset(holder);
        holder.fighter.idleAnimation.reset();
        holder.startAttack(new Attack(new DuckItem(), holder), holder.fighter.idleAnimation, 4, 0, false, new int[] {
                (int) totalOffset.x,
                (int) totalOffset.y,
                (int) hitbox.width,
                (int) hitbox.height,
                18,
                8,
                -60 * holder.direction,
                4,
                10
        });
    }
}
