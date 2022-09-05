package sab.game.attacks.projectiles;

import java.util.Random;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Direction;
import sab.game.Player;
import sab.game.attacks.Attack;
import sab.game.attacks.AttackType;

public class TinyNote extends AttackType {

    @Override
    public void onCreate(Attack attack) {
        attack.imageName = "small_note.png";
        attack.life = 20;
        attack.frameCount = 3;
        attack.hitbox.width = 20;
        attack.hitbox.height = 20;
        attack.drawRect.width = 20;
        attack.drawRect.height = 20;
        attack.frame = new Random().nextInt(3);
        attack.damage = 1;
        attack.directional = true;
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
        hit.velocity.scl(0.9f);
        if (hit instanceof Player) {
            ((Player) hit).frame = ((Player) hit).fighter.knockbackAnimation.stepLooping();
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.owner.frame = 10;
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(8 * attack.owner.direction, -8));
        attack.velocity = new Vector2(8 * attack.owner.direction, 0).rotateDeg((MathUtils.random() -0.5f) * 12);
        attack.knockback = new Vector2(0, 0);
    }
}
