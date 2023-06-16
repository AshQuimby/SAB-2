package sab.game.attack.matthew;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;
import sab.util.Utils;

public class DashSlash extends MeleeAttackType {
    private Vector2 startPosition;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "dash_slash.png";
        attack.basedOffCostume = true;
        attack.life = 14;
        attack.frameCount = 7;
        attack.velocity = new Vector2();
        attack.hitbox.width = 128;
        attack.hitbox.height = 128;
        attack.drawRect.width = 192;
        attack.drawRect.height = 192;
        attack.damage = 32;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 16;
        attack.reflectable = false;
        attack.parryable = false;

        offset = new Vector2(16, 0);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 2 == 0) attack.frame++;
        attack.owner.velocity.set(attack.direction * 30, 0);
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.hitbox.setCenter(startPosition);
        attack.owner.velocity.scl(0);
        attack.owner.setIFrames(10);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(20 * attack.owner.direction, 2);
        SABSounds.playSound("swish.mp3");
        startPosition = attack.owner.getCenter();
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        render(attack, g);
    }
}
