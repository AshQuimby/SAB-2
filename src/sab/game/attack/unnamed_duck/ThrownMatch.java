package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.Utils;
import sab.util.SABRandom;

public class ThrownMatch extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "match.png";
        attack.frameCount = 1;
        attack.hitbox = new Rectangle(0, 0, 16, 16);
        attack.drawRect = new Rectangle(0, 0, 16, 16);
        attack.life = 200;
        attack.canHit = false;
        attack.parryable = false;
        attack.reflectable = true;
        attack.collideWithStage = true;
        attack.directional = true;
    }

    @Override
    public void update(Attack attack) {
        for (Attack otherAttack : attack.getBattle().getAttacks()) {
            if (otherAttack.type instanceof FlammableLiquid droplet && !droplet.onFire) {
                if (attack.getCenter().dst2(otherAttack.getCenter()) < 32 * 32) {
                    droplet.onFire = true;
                    SABSounds.playSound("explosion.mp3");
                    break;
                }
            }
        }
        if (attack.collisionDirection == Direction.DOWN) {
            attack.alive = false;
        }

        attack.velocity.y -= .96f;
        attack.rotation -= attack.velocity.x;

        attack.owner.battle.addParticle(new Particle(Utils.randomPointInRect(attack.drawRect), new Vector2(2 * SABRandom.random(), 0).rotateDeg(SABRandom.random() * 360), 8, 8, 0, "fire.png"));
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.getCenter().add(attack.owner.fighter.itemOffset.x * attack.direction, attack.owner.fighter.itemOffset.y));
        attack.velocity = new Vector2(attack.owner.direction * 10, 5);
    }
}
