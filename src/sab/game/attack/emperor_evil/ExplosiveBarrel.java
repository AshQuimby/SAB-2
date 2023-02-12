package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;

import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;

public class ExplosiveBarrel extends MeleeAttackType {

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "barrel.png";
        attack.hitbox.width = 64;
        attack.hitbox.height = 80;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.frameCount = 1;
        attack.life = 30;
        attack.hitCooldown = 1;
        attack.damage = 0;
        attack.knockback = new Vector2();
        attack.canHit = false;

        offset = new Vector2();
        usePlayerDirection = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
    }

    @Override
    public void update(Attack attack) {       
        super.update(attack); 
        attack.owner.frame = 10;
        attack.owner.velocity.y *= 0.2f;
    }

    @Override
    public void kill(Attack attack) {
        for (int i = 0; i < 6 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 64, 64, 0, "fire.png"));
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 96, 96, 0, "smoke.png"));
        }
        for (int i = 0; i < 4; i++) {
            attack.owner.battle.addAttack(new Attack(new Banana(), attack.owner), new int[]{0});
        }
        attack.owner.velocity.y = 32;
        attack.owner.usedRecovery = true;
        attack.owner.removeJumps();
        attack.owner.battle.addParticle(new Particle(1, attack.owner.hitbox.getCenter(new Vector2()), new Vector2(MathUtils.random(2f, 2f), 5), 64, 80, 1, "barrel.png"));
        SABSounds.playSound("explosion.mp3");
    }
    
    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}
