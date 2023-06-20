package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.Player;
import sab.game.attack.unnamed_duck.ThrownIceCube;
import sab.game.attack.unnamed_duck.ThrownMatch;

public class IceCube extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "ice_cube.png";
        frameCount = 1;
        hitbox.width = 40;
        hitbox.height = 44;
        drawRect = new Rectangle(hitbox);
        offset = new Vector2(-8, 8);
        uses = 1;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new ThrownIceCube(), 1, 0, true);
    }
}

