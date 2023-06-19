package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.unnamed_duck.ThrownDuckBomb;
import sab.game.attack.unnamed_duck.ThrownMolotov;

public class DuckBomb extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "duck_bomb_item.png";
        frameCount = 1;
        hitbox.width = 28;
        hitbox.height = 36;
        drawRect = new Rectangle(hitbox);
        offset = new Vector2(0, 0);
        uses = 1;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new ThrownDuckBomb(), 1, 0, true);
    }
}


