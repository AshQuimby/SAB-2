package sab.game.attack.chain;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Chain;

public class BoomerangKnife extends AttackType {
    private Player originalOwner;
    private boolean returning;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "chain_knife.png";
        attack.life = -1;
        attack.frameCount = 8;
        attack.hitbox.width = 40;
        attack.hitbox.height = 40;
        attack.drawRect.width = 40;
        attack.drawRect.height = 40;
        attack.damage = 20;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 60;

        originalOwner = attack.owner;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life % 5 == 0) {
            if (++attack.frame >= attack.frameCount) {
                attack.frame = 0;
            }
        }

        if (!returning && attack.life < -48) {
            returning = true;
            attack.clearHitObjects();
        }

        if (returning) {
            Vector2 ownerPosition = attack.owner.hitbox.getCenter(new Vector2());
            Vector2 position = attack.hitbox.getCenter(new Vector2());

            attack.velocity.add(ownerPosition.cpy().sub(position).limit(1));
            attack.velocity.scl(.95f);

            if (position.dst(ownerPosition) <= 20) {
                attack.alive = false;
            }
        }

        attack.knockback.set(attack.velocity.cpy().scl(0.5f));
        attack.knockback.y += 6;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()));
        attack.velocity = new Vector2(10 * attack.owner.direction, 0);
        attack.knockback.set(attack.velocity);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {

    }

    @Override
    public void onKill(Attack attack) {
        ((Chain) originalOwner.fighter.type).hasBoomerang = true;
    }
}
