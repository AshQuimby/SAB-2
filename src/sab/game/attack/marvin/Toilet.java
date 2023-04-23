package sab.game.attack.marvin;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;

public class Toilet extends AttackType {
    private boolean playerLaunched;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "toilet.png";
        attack.life = 320;
        attack.frameCount = 2;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 80;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 60;
        attack.reflectable = false;
        attack.collideWithStage = true;
    }

    @Override
    public void update(Attack attack) {
        attack.drawRect.y += 20;

        if (attack.collisionDirection == Direction.DOWN) {
            if (attack.life > 60 && playerLaunched) attack.life = 60;
            attack.velocity.y = 0;
        }

        if (attack.life == 290) {
            attack.owner.velocity.y = 24;
            attack.frame = 1;
            playerLaunched = true;
            attack.owner.usedRecovery = true;
            attack.owner.removeJumps();
        }

        if (playerLaunched) {
            attack.velocity.y -= 0.25f;
            if (attack.life % 8 == 0) {
                attack.owner.battle.addParticle(new Particle(attack.drawRect.getCenter(new Vector2()).add(0, 12), new Vector2(2 * (MathUtils.random() - 0.5f), 5 * (MathUtils.random() + 0.5f)), 32, 48, "water.png"));
            }
        } else {
            attack.velocity.y -= 0.015f;
            attack.owner.velocity.scl(0);
            attack.owner.hitbox.setCenter(attack.hitbox.getCenter(new Vector2()).add(0, 36));
            attack.owner.frame = 6;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(0, -1);
        attack.knockback = new Vector2(0, -6.5f);
        SABSounds.playSound("toilet.mp3");
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {

    }
}
