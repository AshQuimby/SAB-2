package sab.game.item;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.attack.unnamed_duck.KeroseneEmitter;

public class Jerrycan extends Item {
    public int fuel;

    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "jerrycan.png";
        frameCount = 1;
        hitbox.width = 48;
        hitbox.height = 40;
        offset = new Vector2(8, -4);
        drawRect = new Rectangle(hitbox);
        uses = 2;
        fuel = 180;
    }

    @Override
    public void updateHeld(Player holder) {
        super.updateHeld(holder);
        uses = 2;
        if (fuel == 0) {
            toss(holder);
        }
    }

    @Override
    public void onUse(Player holder) {
        holder.startAttack(new KeroseneEmitter(this), null, 1, 0, false, null);
    }

    @Override
    public void onToss(Player holder) {
        Match match = new Match();
        match.setDefaults();
        holder.pickupItem(match);
    }
}
