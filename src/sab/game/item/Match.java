package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.unnamed_duck.DuckItem;
import sab.game.attack.unnamed_duck.ThrownMatch;

public class Match extends Item {
    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "match.png";
        frameCount = 1;
        hitbox.width = 16;
        hitbox.height = 16;
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
        holder.startAttack(new ThrownMatch(), 1, 0, true);
    }
}

