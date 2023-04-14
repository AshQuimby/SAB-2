package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Hittable;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.MeleeAttackType;
import sab.net.Keys;

public class DuckGrab extends MeleeAttackType {
    private int grabDuration;
    private Player grabbedPlayer;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.frameCount = 5;
        attack.hitbox = new Rectangle(0, 0, 36, 36);
        attack.drawRect = new Rectangle(0, 0, 44, 44);
        attack.life = 10;
        attack.hitCooldown = -1;
        attack.damage = 4;
        usePlayerDirection = true;
        offset = new Vector2(36 * attack.direction, 16);
        grabbedPlayer = null;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter().add(new Vector2(32 * attack.direction, 16)));
        attack.knockback = new Vector2(8 * attack.direction, 5);
    }

    @Override
    public void update(Attack attack) {
        if (attack.owner.keys.isJustPressed(Keys.ATTACK)) {
            attack.clearHitObject(grabbedPlayer);
            attack.alive = false;
            grabbedPlayer = null;
            return;
        }
        attack.knockback.x = 8 * attack.direction;
        attack.owner.occupy(1);
        offset = attack.owner.fighter.itemOffset;
        super.update(attack);
        if (grabbedPlayer != null) {
            if (--grabDuration <= 0) {
                attack.alive = false;
            }
            grabbedPlayer.hitbox.setCenter(attack.getCenter());
            grabbedPlayer.fighter.knockbackAnimation.reset();
            grabbedPlayer.startAnimation(1, grabbedPlayer.fighter.knockbackAnimation, 1, true);
            grabbedPlayer.direction = attack.owner.direction;
            attack.life = 4;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        if (hit != attack.owner && grabbedPlayer == null && hit instanceof Player) {
            grabbedPlayer = (Player) hit;
            grabDuration = grabbedPlayer.damage / 2 + 30;
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}
