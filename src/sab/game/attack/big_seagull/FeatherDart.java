package sab.game.attack.big_seagull;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SabRandom;

public class FeatherDart extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "feather_dart.png";
        attack.life = 24;
        attack.frameCount = 1;
        attack.hitbox.width = 30;
        attack.hitbox.height = 30;
        attack.drawRect.width = 56;
        attack.drawRect.height = 20;
        attack.damage = 12;
        attack.collideWithStage = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.collisionDirection != Direction.NONE) {
            attack.alive = false;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
        attack.alive = false;
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {

    }

    @Override
    public void onKill(Attack attack) {
        for (int i = 0; i < 3; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()),
                    new Vector2(2 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 32, 32, 3,
                    "smoke.png"));
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.direction = attack.owner.direction;

        if (data[0] == 0) {
            attack.owner.battle.addAttack(new Attack(attack.type, attack.owner), new int[] { 1 });
            attack.owner.battle.addAttack(new Attack(attack.type, attack.owner), new int[] { 2 });
        }

        attack.velocity = new Vector2(8, 0).rotateDeg((data[0] - 1) * 12 * attack.direction);
        attack.velocity.x *= attack.direction;
        attack.rotation = (data[0] - 1) * 24;
        
        attack.knockback.set(attack.velocity.cpy().scl(.5f));
    }
}