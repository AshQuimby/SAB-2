package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.unnamed_duck.ThrownAxe;

public class Axe extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "axe_item.png";
        frameCount = 1;
        hitbox.width = 48;
        hitbox.height = 48;
        drawRect = new Rectangle(hitbox);
        offset = new Vector2(-4, 16);
        uses = 1;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new ThrownAxe(), 1, 0, true);
    }
}


