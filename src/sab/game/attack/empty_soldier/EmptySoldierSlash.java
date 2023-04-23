package sab.game.attack.empty_soldier;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.SABSounds;
import sab.game.attack.MeleeAttackType;
import sab.game.attack.Attack;
import sab.game.particle.Particle;
import sab.util.Utils;

public class EmptySoldierSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "pixel.png";
        attack.life = 6;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 48;
        attack.drawRect.width = 0;
        attack.drawRect.height = 0;
        attack.damage = 5;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 7;
        attack.reflectable = false;

        offset = new Vector2(16, 0);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(3 * attack.owner.direction, 4);
        SABSounds.playSound("swish.mp3");
    }
}

