package sab.game.attack.stephane;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.*;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;

public class Wraith extends AttackType implements Hittable {
    private Attack attack;
    private Animation flyAnimation;
    private int hurtTime;
    private int swoopTime;

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        this.attack = attack;
        attack.imageName = "wraith.png";
        attack.basedOffCostume = false;

        attack.life = 300;
        attack.frameCount = 6;
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 64;
        attack.drawRect.height = 64;
        attack.frame = 0;
        attack.damage = 6;
        attack.hitCooldown = 40;
        attack.parryable = true;
        attack.reflectable = false;
        attack.collideWithStage = true;
        attack.directional = true;

        flyAnimation = new Animation(0, 5, 10, true);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.scl(.995f);
        attack.knockback.set(attack.direction * 4, 2);

        if (--hurtTime >= 0) return;

        attack.frame = flyAnimation.stepLooping();

        Player target = attack.getNearestOpponent(600);
        if (target != null) {
            Vector2 toTarget = target.getCenter().sub(attack.getCenter());
            if (swoopTime > 0 && toTarget.len() < 64) swoopTime = 0;
            if (swoopTime <= 0) toTarget.add(0, 300);
            attack.velocity.add(toTarget.nor().scl(.3f));
        } else {
            attack.velocity.x += .3f * attack.direction;
        }

        if (--swoopTime <= -120) {
            swoopTime = 60;
        }

        for (Attack other : attack.getBattle().getAttacks()) {
            if (attack != other && attack.owner != other.owner) other.attemptHit(attack, this);
        }
    }

    @Override
    public void onSpawn(sab.game.attack.Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(attack.owner.direction * 20, 4));
        attack.direction = attack.owner.direction;
        attack.velocity.set(attack.direction * 2, 0);
    }

    @Override
    public void onKill(Attack attack) {
    }

    @Override
    public boolean onHit(DamageSource source) {
        attack.life -= source.damage;
        attack.velocity.add(source.knockback);
        hurtTime = 30;
        SABSounds.playSound("hit.mp3");
        return true;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return true;
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        Color color = hurtTime >= 0 ? Color.RED : Color.WHITE;
        g.usefulTintDraw(g.imageProvider.getImage(attack.imageName),
                attack.drawRect.x,
                attack.drawRect.y,
                (int) attack.drawRect.width,
                (int) attack.drawRect.height,
                attack.frame,
                6,
                0,
                attack.direction == 1,
                false,
                color);
    }
}