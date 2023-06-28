package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.SabRandom;

public class JohnSuck extends MeleeAttackType {
    private Player trappedPlayer;
    private Animation suckAnimation = new Animation(new int[]{13, 12}, 6, true);
    private boolean spit;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "john_suck.png";
        attack.hitbox.width = 70;
        attack.hitbox.height = 60;
        attack.drawRect.width = 80;
        attack.drawRect.height = 80;
        attack.reflectable = false;
        attack.parryable = false;
        attack.frameCount = 6;
        attack.life = -1;
        trappedPlayer = null;
        attack.hitCooldown = 1;
        spit = false;

        offset = new Vector2(48, 16);
        usePlayerDirection = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
    }

    @Override
    public void onKill(Attack attack) {
        if (!spit && trappedPlayer != null) {
            trappedPlayer.invulnerable = false;
            trappedPlayer.reveal();
            trappedPlayer.velocity.scl(0);
            attack.owner.drawRectOffset = new Vector2();
            for (int i = 0; i < 8 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 32, 32, 0, "smoke.png"));
            }
            trappedPlayer = null;
        }
        attack.owner.resetAction();
    }

    @Override
    public void update(Attack attack) {  
        super.update(attack);     
        if (attack.life % 8 == 0) attack.frame++;
        if (attack.frame >= attack.frameCount) attack.frame = 0;

        if (attack.owner.isStuck()) attack.alive = false;

        if (trappedPlayer != null) {
            if (!attack.owner.keys.isPressed(Keys.ATTACK)) {
                spit = true;
            }
            trappedPlayer.hitbox.setCenter(attack.getCenter());
            trappedPlayer.stun(2);
            attack.owner.occupy(1);
            attack.owner.velocity.x *= 0.96f;
            attack.owner.velocity.y *= 0.96f;
            attack.owner.drawRectOffset = new Vector2(SabRandom.random(-2f, 2f), SabRandom.random(-2f, 2f));
            if (spit) {
                trappedPlayer.invulnerable = false;
                trappedPlayer.reveal();
                trappedPlayer = null;
                attack.knockback = new Vector2(6 * attack.direction, 4);
                attack.clearHitObjects();
                attack.canHit = true;
                attack.life = 1;
            }
        } else {
            if (!attack.owner.keys.isPressed(Keys.ATTACK)) {
                attack.alive = false;
            }

            if (attack.owner.getAnimation() != null && attack.owner.getAnimation().isDone()) attack.owner.getAnimation().reset();
            attack.owner.startAnimation(0, suckAnimation, 360, false);
        }
    }
    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        if (trappedPlayer == null && attack.life < 0) super.render(attack, g);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (attack.life < 0 && trappedPlayer == null && hit instanceof Player && hit != null) {
            attack.owner.resetAction();
            trappedPlayer = (Player) hit;
            trappedPlayer.stun(2);
            trappedPlayer.hide();
            attack.canHit = false;
            attack.damage = 4;
            attack.life = trappedPlayer.damage / 2 + 30;
            attack.knockback = new Vector2();
            attack.hitCooldown = 1000;
            for (int i = 0; i < 8 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 32, 32, 0, "smoke.png"));
            }
        }
    }
}
