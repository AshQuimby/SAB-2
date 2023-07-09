package sab.game.attack.gus;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import com.seagull_engine.Seagraphics;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.attack.MeleeAttackType;

public class Tongue extends MeleeAttackType {
    private Rectangle tipper;
    
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "tongue.png";
        attack.life = 14;
        attack.frameCount = 2;
        attack.velocity = new Vector2();
        attack.hitbox.width = 64;
        attack.hitbox.height = 8;
        attack.drawRect.width = 64;
        attack.drawRect.height = 8;
        attack.damage = 16;
        attack.hitCooldown = 15;
        attack.reflectable = false;
        tipper = new Rectangle();
        tipper.width = 4;
        tipper.height = 4;
        usePlayerDirection = true;
        offset = new Vector2(attack.owner.hitbox.width, 12);
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        tipper.setCenter(attack.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 - tipper.width / 2), 2));

        if (attack.life <= 5) {
            attack.frame = 1;
            attack.canHit = false;
        }
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        if (hit.hitbox.contains(tipper)) {
            SabSounds.playSound("tongue_splat.mp3");
            attack.knockback = new Vector2(9 * attack.direction, 6);
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(attack.owner.hitbox.getCenter(new Vector2()).add(32 * attack.owner.direction, 4));
        tipper.setCenter(attack.hitbox.getCenter(new Vector2()).add(attack.direction * (attack.hitbox.width / 2 - tipper.width / 2), 2));
        attack.knockback = new Vector2(-3 * attack.owner.direction, 4);
        SabSounds.playSound("tongue_spit.mp3");
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        super.render(attack, g);
        g.shapeRenderer.rect(tipper.x, tipper.y, tipper.width, tipper.height);
    }
}
