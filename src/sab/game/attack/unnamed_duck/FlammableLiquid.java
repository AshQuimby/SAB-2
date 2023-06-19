package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.Utils;
import sab.util.SABRandom;

public class FlammableLiquid extends AttackType {
    public Color color;
    public boolean onFire;

    @Override
    public void setDefaults(sab.game.attack.Attack attack) {
        attack.imageName = "flammable_liquid.png";
        attack.life = 500;
        attack.frameCount = 2;
        attack.hitbox.width = 8;
        attack.hitbox.height = 8;
        attack.drawRect.width = 16;
        attack.drawRect.height = 8;
        attack.damage = 1;
        attack.directional = true;
        attack.collideWithStage = true;
        attack.canHit = false;
        attack.hitCooldown = 10;
        attack.reflectable = false;
        attack.parryable = false;
        color = new Color(1, 1, 1, 1);
    }

    @Override
    public void update(sab.game.attack.Attack attack) {
        attack.velocity.y -= 1;
        attack.rotation = MathUtils.atan2(attack.velocity.y, attack.velocity.x) * MathUtils.radiansToDegrees;

        if (attack.collisionDirection == Direction.DOWN) {
            attack.velocity.set(0, 0);
            attack.frame = 1;
            attack.rotation = 0;
        }
        if (onFire) {
            attack.canHit = true;
            if (SABRandom.random() < .1f) {
                if (attack.life >= 60) attack.owner.battle.addParticle(new Particle(Utils.randomPointInRect(attack.drawRect), new Vector2(SABRandom.random(-1f, 1f), SABRandom.random(.2f, 2f)), 16, 16, 0, "fire.png"));
                else attack.owner.battle.addParticle(new Particle(Utils.randomPointInRect(attack.drawRect), new Vector2(SABRandom.random(-1f, 1f), SABRandom.random(.2f, 2f)), 16, 16, 0, "smoke.png"));
            }
        } else {
            for (Attack otherAttack : attack.getBattle().getAttacks()) {
                if (otherAttack == attack) continue;

                if (otherAttack.type instanceof FlammableLiquid droplet && droplet.onFire) {
                    if (attack.getCenter().dst2(otherAttack.getCenter()) < 32 * 32) {
                        onFire = true;
                    }
                }
            }
        }
    }

    @Override
    public void hit(sab.game.attack.Attack attack, GameObject hit) {
        attack.life -= 60;
        if (attack.life <= 0) attack.alive = false;
    }

    @Override
    public void onKill(sab.game.attack.Attack attack) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(Float.intBitsToFloat(data[0]), Float.intBitsToFloat(data[1]));
        attack.velocity.set(Float.intBitsToFloat(data[2]), Float.intBitsToFloat(data[3]));
        attack.knockback = new Vector2(0, 1);
        onFire = data[4] == 1;
        Color.abgr8888ToColor(color, data[5]);
        if (data.length >= 7) {
            attack.damage = data[6];
        }
        if (data.length >= 8) {
            attack.life = data[7];
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        g.usefulTintDraw(
                g.imageProvider.getImage(attack.imageName),
                attack.drawRect.x,
                attack.drawRect.y,
                16,
                8,
                attack.frame,
                attack.frameCount,
                attack.rotation,
                attack.direction == 1,
                false,
                color
        );
    }
}

