package sab.game.attacks.melees;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;
import sab.net.Keys;

public class EvilSuck extends AttackType {
    private Player trappedPlayer;
    private int chompTime;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "suck.png";
        attack.hitbox.width = 116;
        attack.hitbox.height = 108;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.frameCount = 4;
        attack.life = 360;
        trappedPlayer = null;
        attack.hitCooldown = 1;
        chompTime = 0;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(84 * attack.owner.direction, 0));
        attack.direction = attack.owner.direction;
    }

    @Override
    public void update(Attack attack) {        
        if (attack.life % 8 == 0) attack.frame++;
        if (attack.frame >= 4) attack.frame = 0;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(84 * attack.owner.direction, 0));

        if (attack.owner.stuckCondition()) attack.alive = false;

        if (trappedPlayer != null) {
            attack.life = 2;
            trappedPlayer.hide();
            trappedPlayer.velocity.scl(0);
            trappedPlayer.knockback.scl(0);
            trappedPlayer.stun(2);
            trappedPlayer.invulnerable = true;
            chompTime--;
            if (chompTime % 15 == 0) {
                SABSounds.playSound("chomp.mp3");
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
            if (!attack.owner.hasAction()) attack.owner.startAnimation(0, new Animation(new int[]{4, 5}, 8, true), 14, false);
        } else {
            if (!attack.owner.keys.isPressed(Keys.ATTACK)) {
                attack.alive = false;
            }
            if (!attack.owner.hasAction()) attack.owner.startAnimation(0, new Animation(new int[]{4, 5}, 8, true), 12, false);
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
                attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 32, 32, 0, "smoke.png"));
            }
        }
    }
}
