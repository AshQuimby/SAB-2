package sab.game.item;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.unnamed_duck.DuckItem;
import sab.game.attack.unnamed_duck.FlammableLiquid;
import sab.net.Keys;
import sab.util.SABRandom;

import java.security.Key;

public class Flamethrower extends Item {
    private Animation swing;
    private boolean firing;
    private Animation animation;

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
        swing = new Animation(1, 6, 2, true);
        animation = new Animation(new int[]{0}, 1, true);
        uses = 120;
        firing = false;
    }

    @Override
    public void updateHeld(Player holder) {
        frame = animation.step();

        if (holder.isReady() && firing && holder.keys.isPressed(Keys.ATTACK)) onUse(holder);
        else firing = false;
        super.updateHeld(holder);
    }

    @Override
    public void onUse(Player holder) {
        if (!firing) {
            firing = true;
        } else {
            SABSounds.playSound("fire.mp3");
            FlammableLiquid flames = new FlammableLiquid();
            holder.battle.addAttack(new Attack(flames, holder), new int[]{
                    Float.floatToIntBits(getCenter().x + 48 * holder.direction),
                    Float.floatToIntBits(getCenter().y + 8),
                    Float.floatToIntBits(24 * holder.direction * SABRandom.random(0.5f, 1.2f)),
                    Float.floatToIntBits(0.5f * holder.direction * SABRandom.random(-1f, 1f)),
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
