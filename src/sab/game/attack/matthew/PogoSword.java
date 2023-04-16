package sab.game.attack.matthew;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.net.Keys;

public class PogoSword extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "pogo_sword.png";
        attack.basedOffCostume = true;
        attack.life = 40;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 12;
        attack.hitbox.height = 52;
        attack.drawRect.width = 12;
        attack.drawRect.height = 52;
        attack.damage = 12;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 10;
        attack.reflectable = false;
        attack.drawAbovePlayers = true;

        offset = new Vector2(0, 0);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.move(new Vector2(0, -52));
        if (attack.collisionDirection == Direction.DOWN) {
            attack.alive = false;
            attack.owner.startAnimation(1, attack.owner.fighter.freefallAnimation, 6, false);
        }
        if (attack.owner.keys.isPressed(Keys.LEFT)) {
            attack.owner.velocity.x -= 0.1f;
        }
        if (attack.owner.keys.isPressed(Keys.RIGHT)) {
            attack.owner.velocity.x += 0.1f;
        }
        if (attack.life < 20 && attack.owner.keys.isJustPressed(Keys.UP)) {
            attack.alive = false;
            attack.owner.startAnimation(1, attack.owner.fighter.freefallAnimation, 6, false);   
        }
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.life = 8;
        attack.owner.startAnimation(1, attack.owner.fighter.freefallAnimation, 8, false);
        attack.owner.velocity.y = 18;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        if (attack.owner.touchingStage) {
            attack.owner.velocity.y = 14;
        } else if (attack.owner.velocity.y > -4) {
            attack.owner.velocity.y = -4;
        }
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(0, -10);
    }
}
