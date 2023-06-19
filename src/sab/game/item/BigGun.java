package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.attack.unnamed_duck.BigBullet;

public class BigGun extends Item {
    private int spinny;

    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "revolver.png";
        frameCount = 1;
        hitbox.width = 56;
        hitbox.height = 28;
        offset = new Vector2(-12, 0);
        drawRect = new Rectangle(hitbox);
        uses = 6;
    }

    @Override
    public void updateHeld(Player holder) {
        if (spinny > 0) {
            spinny--;
        }
        rotation = spinny * spinny / 2f * holder.direction;
        super.updateHeld(holder);
    }

    @Override
    public void renderHeld(Player holder, Seagraphics g) {
        snapToPlayer(holder);
        drawRect.setCenter(hitbox.getCenter(new Vector2()));

        Vector2 handle = new Vector2(-24 * direction, -8f);
        handle.rotateDeg(rotation);
        drawRect.x -= handle.x;
        drawRect.y -= handle.y;

        render(g);
    }

    @Override
    public void onUse(Player holder) {
        holder.fighter.idleAnimation.reset();
        holder.startAttack(new BigBullet(), holder.fighter.idleAnimation, 1, 15, false);
        spinny = 15;
    }
}
