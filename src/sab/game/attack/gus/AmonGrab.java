package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class AmonGrab extends MeleeAttackType {
    private int suplexTime;
    private Player grabbedPlayer;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "amon_gus_man_torso.png";
        attack.basedOffCostume = true;
        attack.frameCount = 5;
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.life = 12;
        attack.hitCooldown = -1;
        attack.damage = 4;
        attack.parryable = false;
        attack.reflectable = false;
        usePlayerDirection = true;
        offset = new Vector2(32, 8);
        grabbedPlayer = null;
        suplexTime = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter().add(new Vector2(32 * attack.direction, 16)));
        attack.knockback = new Vector2(0, 0);
    }

    @Override
    public void update(Attack attack) {
        attack.owner.occupy(1);
        offset = attack.owner.fighter.itemOffset;
        super.update(attack);
        if (grabbedPlayer != null) {
            if (attack.life > 1) grabbedPlayer.stun(1);
            grabbedPlayer.frame = grabbedPlayer.fighter.knockbackAnimation.stepLooping();
            grabbedPlayer.direction = attack.owner.direction;
            grabbedPlayer.hitbox.setCenter(attack.getCenter().add(new Vector2((18 - suplexTime * 0.75f) * attack.direction, 12 + suplexTime * 1.5f).rotateDeg(getSuplexRotation(attack))));
            grabbedPlayer.rotation = getSuplexRotation(attack);
            if (attack.owner.frame == 49) {
                attack.life = 20;
                attack.owner.startAnimation(1, new Animation(new int[] { 51 }, 100, true), 23, true);
            } else if (attack.owner.frame > 49) {
                suplexTime++;
            }
            if (attack.life == 2) {
                attack.damage = 24;
                SabSounds.playSound("impostor_kill.mp3");
                attack.knockback = new Vector2(16 * attack.owner.direction * -1, 8);
                attack.clearHitObjects();
                attack.canHit = true;
                grabbedPlayer.rotation = 0;
                grabbedPlayer = null;
                attack.life = 8;
            }
        }
    }

    private float getSuplexRotation(Attack attack) {
        if (grabbedPlayer == null && attack.life <= 4) {
            return 6 * attack.life * (attack.life % 2 == 0 ? 1 : -1);
        }
        return suplexTime * suplexTime / 3f * attack.direction;
    }

    @Override
    public void onKill(Attack attack) {
        if (grabbedPlayer != null) {
            grabbedPlayer.rotation = 0;
        }
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (attack.owner.frame > 49) {

        } else if (hit != attack.owner && grabbedPlayer == null && hit instanceof Player) {
            grabbedPlayer = (Player) hit;
            attack.life = 20; 
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        if (attack.owner.frame > 50) {
            g.usefulDraw(g.imageProvider.getImage(attack.imageName), attack.owner.getCenter().x - 32, attack.owner.getCenter().y - 64 - 16, 64, 128, 0, 1, getSuplexRotation(attack), attack.owner.direction == 1, false);
        }
    }
}
