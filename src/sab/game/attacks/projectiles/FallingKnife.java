package sab.game.attacks.projectiles;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;
import sab.game.particles.Particle;
import sab.game.stages.Platform;

public class FallingKnife extends AttackType {
    private boolean onGround;
    
    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "knife_spike.png";
        attack.life = 200;
        attack.frameCount = 1;
        attack.hitbox.width = 16;
        attack.hitbox.height = 60;
        attack.drawRect.width = 16;
        attack.drawRect.height = 60;
        attack.damage = 6;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 24;
        attack.reflectable = false;
    }

    @Override
    public void update(Attack attack) {
        if (CollisionResolver.movingResolve(attack, attack.owner.battle.getPlatforms()) == Direction.UP) {
            attack.velocity.y = 0;
            attack.hitbox.y -= 10;
            onGround = true;
        } else {
            onGround = false;
        }

        if (onGround) {
            attack.knockback.set(0, 10);
            attack.velocity.y = 0;
        } else {
            attack.knockback.set(0, -10);
            attack.velocity.y -= 1.2f;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(0, -1);
        attack.knockback = new Vector2(0, -16);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {

    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        g.usefulDraw(
                g.imageProvider.getImage(attack.imageName),
                attack.drawRect.x,
                attack.drawRect.y,
                (int) attack.drawRect.width,
                (int) attack.drawRect.height,
                attack.frame,
                attack.frameCount,
                attack.rotation,
                attack.direction == 1,
                !onGround);
    }

    @Override
    public void kill(Attack attack) {
        for (int i = 0; i < 5; i++) {
            attack.owner.battle.addParticle(
                    new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(MathUtils.random(-2f, 2f), MathUtils.random(1f, 3f)), 32, 32, 30, "smoke.png"));
        }
    }
}