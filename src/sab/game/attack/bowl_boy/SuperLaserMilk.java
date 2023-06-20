package sab.game.attack.bowl_boy;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.util.Utils;

public class SuperLaserMilk extends MeleeAttackType {
    private Animation spinAnimation;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "super_laser_milk.png";
        attack.hitbox.setSize(145 * 4, 20 * 4);
        attack.drawRect.setSize(145 * 4, 20 * 4);
        attack.frameCount = 4;
        attack.life = 100;
        attack.hitCooldown = 2;
        attack.damage = 1;
        attack.reflectable = false;
        attack.parryable = false;
        usePlayerDirection = true;
        offset = new Vector2(145 * 2 + 28, 12);

        spinAnimation = new Animation(21, 24, 3, false);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.owner.velocity.scl(0);
        if (attack.owner.getAnimation() != null && attack.owner.getAnimation().isDone()) attack.owner.getAnimation().reset();
        if (attack.life % 5 == 0) attack.frame = Utils.loop(attack.frame, 1, 3, 0);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.staticKnockback = attack.life > 2;
        attack.knockback.set(attack.owner.direction * 12, 1);
        if (hit instanceof Player player) {
            player.move(new Vector2(attack.direction, 0));
            player.stun(2);
        }
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.resetAction();
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.owner.startAnimation(0, spinAnimation, 100000, true);
        SABSounds.playSound("slurp.mp3");
    }
}
