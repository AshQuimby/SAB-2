package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.item.Jerrycan;
import sab.net.Keys;

public class KeroseneEmitter extends MeleeAttackType {
    private Jerrycan jerrycan;

    public KeroseneEmitter(Jerrycan jerrycan) {
        this.jerrycan = jerrycan;
    }

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.frameCount = 1;
        attack.hitbox = new Rectangle(0, 0, 0, 0);
        attack.drawRect = new Rectangle(0, 0, 0, 0);
        attack.life = 2;
        attack.canHit = false;
        attack.damage = 0;
        attack.parryable = false;
        attack.reflectable = false;
        usePlayerDirection = true;
        offset = new Vector2(0, 0);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = attack.owner.direction;
        attack.hitbox.setCenter(attack.owner.getCenter());
        attack.knockback = new Vector2(0, 0);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.owner.occupy(1);

        Vector2 spawnPosition = attack.owner.getCenter();
        spawnPosition.x += (attack.owner.fighter.itemOffset.x + 24) * attack.direction;
        spawnPosition.y += attack.owner.fighter.itemOffset.y + 12;
        Vector2 velocity = new Vector2(MathUtils.random(4f, 7f) * attack.owner.direction, MathUtils.random(3f, 5f)).add(attack.owner.velocity);

        attack.getBattle().addAttack(new Attack(new FlammableLiquid(), attack.owner), new int[] {
                Float.floatToIntBits(spawnPosition.x),
                Float.floatToIntBits(spawnPosition.y),
                Float.floatToIntBits(velocity.x),
                Float.floatToIntBits(velocity.y),
                0,
                Color.YELLOW.toIntBits()
        });

        attack.life = 2;
        jerrycan.fuel--;
        if (attack.owner.isStuck() || !attack.owner.keys.isPressed(Keys.ATTACK) || jerrycan.fuel == 0) {
            attack.alive = false;
        }
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}
