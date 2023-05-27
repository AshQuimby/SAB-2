package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.unnamed_duck.BigBullet;

public class BigGun extends Item {

    private int spinny;

    public void setDefaults() {
        super.setDefaults();
        imageName = "revolver.png";
        frameCount = 1;
        hitbox.width = 56;
        hitbox.height = 28;
        offset = new Vector2(12, 8);
        drawRect = new Rectangle(hitbox);
        uses = 6;
    }

    @Override
    public void updateHeld(Player holder) {
        if (spinny > 0) {
            spinny--;
        }
        rotation = spinny * spinny / 2 * holder.direction;
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        holder.fighter.idleAnimation.reset();
        holder.startAttack(new BigBullet(), holder.fighter.idleAnimation, 1, 15, false);
        spinny = 15;
    }
}
