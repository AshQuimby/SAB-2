package sab.game.attack.chain;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.SabSounds;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;
import sab.game.particle.Particle;
import sab.util.Utils;

public class ChainSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "chain_slash.png";
        attack.life = 8;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;

        offset = new Vector2(32, 4);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 2 == 0) attack.frame++;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(8 * attack.owner.direction, 4);
        SabSounds.playSound("swish.mp3");
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        for (int i = 0; i < 8; i++) {
            attack.owner.battle.addParticle(new Particle(0.1f, hit.getCenter(), Utils.randomParticleVelocity(8), 32, 32, 0.9f, 0, "blood.png"));
        }
    }
}
