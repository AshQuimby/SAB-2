package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ThrownMolotov extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "molotov.png";
        attack.frameCount = 8;
        attack.hitbox = new Rectangle(0, 0, 44, 44);
        attack.drawRect = new Rectangle(0, 0, 56, 56);
        attack.life = 180;
        attack.canHit = true;
        attack.parryable = false;
        attack.reflectable = true;
        attack.collideWithStage = true;
        attack.directional = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection == Direction.DOWN) {
            SABSounds.playSound("crash.mp3");
            attack.alive = false;
        }

        attack.velocity.y -= .96f;
        if (attack.life % 5 == 0) {
            if (++attack.frame == 7) attack.frame = 0;
        }

        //attack.owner.battle.addParticle(new Particle(Utils.randomPointInRect(attack.drawRect), new Vector2(2 * MathUtils.random(), 0).rotateDeg(MathUtils.random() * 360), 8, 8, 0, "fire.png"));
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        SABSounds.playSound("crash.mp3");
        attack.alive = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.owner.fighter.itemOffset.x * attack.direction, attack.owner.fighter.itemOffset.y));
        attack.velocity = new Vector2(attack.owner.direction * 12, 10);
        attack.knockback = new Vector2(attack.direction * 5, 2);
    }

    @Override
    public void onKill(Attack attack) {
        for (int i = 0; i < 10; i++) {
            Vector2 spawnPosition = attack.getCenter();
            Vector2 velocity = new Vector2(MathUtils.random(-7f, 7f), MathUtils.random(3f, 5f)).add(attack.velocity.x / 5, 0);

            attack.getBattle().addAttack(new Attack(new FlammableLiquid(), attack.owner), new int[] {
                    Float.floatToIntBits(spawnPosition.x),
                    Float.floatToIntBits(spawnPosition.y),
                    Float.floatToIntBits(velocity.x),
                    Float.floatToIntBits(velocity.y),
                    1,
                    Color.WHITE.toIntBits()
            });
        }
    }
}

