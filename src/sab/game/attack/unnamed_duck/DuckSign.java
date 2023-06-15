package sab.game.attack.unnamed_duck;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import sab.game.Direction;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.util.SABRandom;

public class DuckSign extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "no_ducks.png";
        attack.life = 120;
        attack.frameCount = 1;
        attack.frame = 0;
        attack.hitbox.width = 44;
        attack.hitbox.height = 68;
        attack.drawRect.width = 80;
        attack.drawRect.height = 68;
        attack.damage = 4;
        attack.hitCooldown = 8;
        attack.reflectable = false;
        attack.collideWithStage = true;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.direction = 1;
        attack.hitbox.setCenter(attack.owner.getCenter().sub(new Vector2(0, 32)));
        attack.velocity = new Vector2(0, -8);
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.battle.addParticle(new Particle(1, attack.getCenter(), new Vector2(SABRandom.random(-2f, 2f), 5), 80, 64, attack.direction, 1, "no_ducks.png"));
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.y -= 0.1f;
        if (attack.collisionDirection == Direction.NONE) {
            attack.knockback = new Vector2(0, -6);
            attack.hitCooldown = 12;
        } else {
            attack.knockback = new Vector2(0, 3);
            attack.hitCooldown = 8;
        }
    }
}
