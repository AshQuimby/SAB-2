package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.unnamed_duck.DuckItem;

public class Rake extends Item {

    private Animation swing;
    private Animation animation;

    public void setDefaults() {
        super.setDefaults();
        imageName = "rake_item.png";
        frameCount = 5;
        hitbox.width = 76;
        hitbox.height = 96;
        drawRect = new Rectangle(hitbox);
        swing = new Animation(new int[]{1, 2, 3, 4}, 4, true);
        animation = new Animation(new int[]{0}, 1, true);
        uses = 12;
    }

    @Override
    public void updateHeld(Player holder) {
        frame = animation.step();
        if (animation.isDone()) animation = new Animation(new int[]{0}, 1, true);
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        swing.reset();
        animation = swing;
        holder.fighter.idleAnimation.reset();
        Vector2 totalOffset = getTotalOffset(holder);
        holder.startAttack(new DuckItem(), holder.fighter.idleAnimation, 4, 12, false, new int[] {
                (int) totalOffset.x,
                (int) totalOffset.y,
                (int) hitbox.width,
                (int) hitbox.height,
                18,
                8,
                210 * holder.direction,
                4,
                10
        });
    }
}
