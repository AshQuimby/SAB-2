package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.Vector2;

import sab.game.Player;
import sab.game.SabSounds;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;
import sab.util.SabRandom;

public class Chomp extends MeleeAttackType {
    private boolean trappedPlayer;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "bite.png";
        attack.hitbox.width = 76;
        attack.hitbox.height = 64;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.life = 38;
        attack.frameCount = 5;
        attack.hitCooldown = 8;
        attack.damage = 24;

        offset = new Vector2(52, 8);
        usePlayerDirection = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.canHit = attack.frame == 1;
        if (attack.life % 8 == 0) attack.frame++;
        if (attack.life == 32) SabSounds.playSound("chomp.mp3");
        attack.knockback = new Vector2(6 * attack.owner.direction, 4);
        if (!attack.canHit) {
            Player target = attack.getNearestOpponent(-1);
            if (target.hitbox.overlaps(attack.hitbox)) {
                if (!trappedPlayer) SabSounds.playSound("crunch.mp3");
                target.frame = target.fighter.knockbackAnimation.stepLooping();
                target.stun(8);
                trappedPlayer = true;
            }
        }
    }
}
