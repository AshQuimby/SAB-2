package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.*;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class ThrownPlane extends AttackType {
    private Animation flyAnimation;

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "plane.png";
        attack.hitbox.setSize(76, 32);
        attack.drawRect = new Rectangle(attack.hitbox);
        attack.frameCount = 4;
        attack.frame = 0;
        attack.life = 150;
        attack.damage = 12;
        attack.hitCooldown = 30;
        attack.parryable = false;
        attack.reflectable = false;
        attack.directional = true;

        flyAnimation = new Animation(new int[] {1, 2, 3, 0}, 15, true);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.scl(.98f);
        attack.knockback.set(attack.direction * 4, 2);
        attack.frame = flyAnimation.stepLooping();

        Player target = attack.getNearestOpponent(1000);
        if (target != null) {
            Vector2 toTarget = target.getCenter().sub(attack.getCenter());
            attack.velocity.add(toTarget.nor().scl(.3f));
        } else {
            attack.velocity.x += .3f * attack.direction;
        }
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.owner.direction * 32, 8));
        attack.direction = attack.owner.direction;
        attack.velocity.set(attack.direction * 10, 2);
    }

    @Override
    public void onKill(Attack attack) {
        // TODO: Spawn smoke particles
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        super.render(attack, g);
    }
}
