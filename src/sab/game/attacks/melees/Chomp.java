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

public class Chomp extends AttackType {
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "bite.png";
        attack.hitbox.width = 76;
        attack.hitbox.height = 64;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.life = 38;
        attack.frameCount = 5;
        attack.hitCooldown = 8;
        attack.damage = 24;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(52 * attack.owner.direction, 8));
    }

    @Override
    public void update(Attack attack) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(52 * attack.owner.direction, 8));
        attack.direction = attack.owner.direction;
        attack.canHit = attack.frame == 1;
        if (attack.life % 8 == 0) attack.frame++;
        if (attack.life == 32) SABSounds.playSound("chomp.mp3");
        attack.knockback = new Vector2(6 * attack.owner.direction, 4);
    }
}
