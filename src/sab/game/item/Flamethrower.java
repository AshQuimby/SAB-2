package sab.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;

import sab.game.Player;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.unnamed_duck.FlammableLiquid;
import sab.net.Keys;
import sab.util.SabRandom;

public class Flamethrower extends Item {
    private boolean firing;

    @Override
    public void setDefaults() {
        super.setDefaults();
        imageName = "flamethrower.png";
        frameCount = 4;
        offset.x += 20;
        offset.y += 8;
        hitbox.width = 120;
        hitbox.height = 24;
        drawRect = new Rectangle(hitbox);
        uses = 90;
        firing = false;
    }

    @Override
    public void updateHeld(Player holder) {
        if (holder.battle.getBattleTick() % 4 == 0 && ++frame >= 4) frame = 0;

        if (holder.isReady() && firing && holder.keys.isPressed(Keys.ATTACK)) onUse(holder);
        else firing = false;
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        if (!firing) {
            firing = true;
        } else {
            SabSounds.playSound("fire.mp3");
            FlammableLiquid flames = new FlammableLiquid();
            holder.battle.addAttack(new Attack(flames, holder), new int[]{
                    Float.floatToIntBits(getCenter().x + 48 * holder.direction),
                    Float.floatToIntBits(getCenter().y + 8),
                    Float.floatToIntBits(24 * holder.direction * SabRandom.random(0.5f, 1.2f)),
                    Float.floatToIntBits(0.5f * holder.direction * SabRandom.random(-1f, 1f)),
                    0,
                    Color.ORANGE.toIntBits(),
                    1,
                    20,
                    0,
                    1
            });
            flames.onFire = true;
            if (--uses == 0) toss(holder);
        }
    }
}
