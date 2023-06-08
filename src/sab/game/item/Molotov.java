package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.unnamed_duck.ThrownMolotov;

public class Molotov extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "molotov.png";
        frameCount = 8;
        hitbox.width = 56;
        hitbox.height = 56;
        drawRect = new Rectangle(hitbox);
        offset = new Vector2(-4, 6);
        uses = 1;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
        direction = -holder.direction;
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new ThrownMolotov(), 1, 0, true);
    }
}


