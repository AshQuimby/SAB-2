package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.Player;
import sab.game.SabSounds;
import sab.game.animation.Animation;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.SabRandom;

public class EvilSuck extends MeleeAttackType {
    private Player trappedPlayer;
    private int chompTime;
    private Animation suckAnimation = new Animation(new int[]{4, 5}, 8, true);

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "evil_suck.png";
        attack.hitbox.width = 116;
        attack.hitbox.height = 108;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.parryable = false;
        attack.frameCount = 4;
        attack.life = 360;
        trappedPlayer = null;
        attack.hitCooldown = 1;
        chompTime = 0;

        offset = new Vector2(84, 0);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.direction = attack.owner.direction;
    }

    @Override
    public void onKill(Attack attack) {
        if (trappedPlayer != null) {
            trappedPlayer.invulnerable = false;
            trappedPlayer.reveal();
            trappedPlayer = null;
        }
        attack.owner.resetAction();
    }

    @Override
    public void update(Attack attack) {  
        super.update(attack);     
        if (attack.life % 8 == 0) attack.frame++;
        if (attack.frame >= 4) attack.frame = 0;

        if (attack.owner.isStuck()) attack.alive = false;

        if (trappedPlayer != null) {
            attack.life = 2;
            trappedPlayer.hide();
            trappedPlayer.velocity.scl(0);
            trappedPlayer.knockback.scl(0);
            trappedPlayer.stun(2);
            trappedPlayer.invulnerable = true;
            chompTime--;
            if (chompTime % 15 == 0) {
                SabSounds.playSound("chomp.mp3");
                trappedPlayer.onHit(attack);
            }
            if (!attack.owner.keys.isPressed(Keys.ATTACK) && chompTime > 5) {
                chompTime = 5;
            }
            if (chompTime == 0) {
                attack.damage = 12;
                attack.knockback = new Vector2(10 * attack.owner.direction, 6);
                trappedPlayer.onHit(attack);
                trappedPlayer.reveal();
                trappedPlayer.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
                trappedPlayer.invulnerable = false;
                trappedPlayer = null;   
            }
            attack.owner.direction = attack.direction;
            attack.owner.startAnimation(0, new Animation(new int[]{4, 5}, 8, true), 12, false);
        } else {
            if (!attack.owner.keys.isPressed(Keys.ATTACK)) {
                attack.alive = false;
            }
            if (attack.owner.getAnimation() != null && attack.owner.getAnimation().isDone()) attack.owner.getAnimation().reset();
            attack.owner.startAnimation(0, suckAnimation, 1000000, false);
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        if (trappedPlayer == null || attack.life > 6) super.render(attack, g);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (trappedPlayer == null && hit instanceof Player && hit != null) {
            trappedPlayer = (Player) hit;
            trappedPlayer.stun(2);
            trappedPlayer.hide();
            chompTime = 60;
            attack.canHit = false;
            attack.damage = 4;
            attack.knockback = new Vector2();
            attack.hitCooldown = 1000;
            for (int i = 0; i < 8 ; i++) {
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 32, 32, 0, "smoke.png"));
            }
        }
    }
}
