package sab.game.attack.walouis;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import com.seagull_engine.Seagraphics;
import com.sun.jdi.VMDisconnectedException;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SABRandom;

public class Note extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "note.png";
        attack.life = 30;
        attack.frameCount = 4;
        attack.hitbox.width = 30;
        attack.hitbox.height = 30;
        attack.drawRect.width = 40;
        attack.drawRect.height = 40;
        attack.frame = new Random().nextInt(4);
        attack.damage = 8;
        attack.collideWithStage = true;
    }

    @Override
    public void update(Attack attack) {
        if (attack.collideWithStage && attack.collisionDirection != Direction.NONE) {
            attack.alive = false;
        }
        if (attack.life % 10 == 0) {
            attack.direction *= -1;
        }
        attack.velocity.y += Math.sin(attack.life / 2.5f) * 0.75f;
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
        for (int i = 0; i < 8 ; i++) {
            attack.owner.battle.addParticle(new Particle(attack.hitbox.getCenter(new Vector2()), new Vector2(4 * SABRandom.random(), 0).rotateDeg(SABRandom.random() * 360), 32, 32, 0, "note_particle.png"));
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(8 * attack.owner.direction, -8));
        attack.direction = attack.owner.direction;
        attack.velocity = new Vector2(0, -5);
        if (data[0] == 0) {
            attack.owner.battle.addAttack(new Attack(attack.type, attack.owner), new int[]{1});
            attack.owner.battle.addAttack(new Attack(attack.type, attack.owner), new int[]{2});
        } else if (data[0] == 1) {
            attack.velocity.x = 2;
        } else if (data[0] == 2) {
            attack.velocity.x = -2;
        } else if (data[0] == 3) {
            attack.reflectable = false;
            attack.parryable = false;
            attack.life = 640;
            attack.collideWithStage = false;
            attack.hitbox.setCenter(attack.owner.getCenter());
            attack.hitbox.y += 20;
            attack.velocity = new Vector2(0, 5).rotateDeg(data[1] * 45 + attack.getBattle().getBattleTick());
        }
        else if (data[0] == 4) {
            attack.reflectable = false;
            attack.parryable = false;
            attack.life = 640;
            attack.collideWithStage = false;
            attack.hitbox.setCenter(attack.owner.getCenter());
            attack.hitbox.y += 20;
            attack.velocity = attack.getNearestOpponent(-1).getCenter().sub(attack.getCenter()).nor().scl(5);
        }
        attack.knockback = attack.velocity.cpy().scl(0.5f);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        super.render(attack, g);
    }
}