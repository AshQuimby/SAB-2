package sab.game.items;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.Player;

// NOTE: There are *currently* no plans for these to be extended to other characters and as such will be left out of the mod loader

public abstract class Item extends GameObject {
    protected Vector2 offset;
    protected int uses;
    public void setDefaults() {
        hitbox = new Rectangle(0, 0, 64, 64);
        drawRect = new Rectangle(hitbox);
        offset = new Vector2();
    }

    public final void use(Player holder) {
        if (--uses == 0) {
            toss(holder);
        }
        onUse(holder);
    }

    public void onUse(Player holder) {
    }

    public void onPickup(Player holder) {
    }

    public final void toss(Player holder) {
        holder.tossItem();
    }

    public void onToss(Player holder) {
    }

    public void updateHeld(Player holder) {
        drawRect = new Rectangle(hitbox);
        direction = holder.direction;
        snapToPlayer(holder);
    }

    public Vector2 getTotalOffset(Player holder) {
        return new Vector2(offset.x * direction, offset.y).add(new Vector2(holder.fighter.itemOffset.x * holder.direction, holder.fighter.itemOffset.y));
    }

    public void snapToPlayer(Player holder) {
        Vector2 totalOffset = getTotalOffset(holder);
        hitbox.setCenter(holder.getCenter().add(totalOffset));
    }

    public void renderHeld(Player holder, Seagraphics g) {
        snapToPlayer(holder);
        drawRect.setCenter(hitbox.getCenter(new Vector2()));
        render(g);
    }
}