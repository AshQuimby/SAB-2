package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.unnamed_duck.DuckItem;
import sab.game.attack.unnamed_duck.ThrownPlane;

public class Plane extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "plane_item.png";
        frameCount = 1;
        hitbox.width = 76;
        hitbox.height = 32;
        drawRect = new Rectangle(hitbox);
        uses = 1;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new ThrownPlane(), 1, 0, true);
    }
}

