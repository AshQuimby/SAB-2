package sab.game.attack.matthew;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class UpwardsSlash extends MeleeAttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "upwards_matthew_slash.png";
        attack.basedOffCostume = true;
        attack.life = 14;
        attack.frameCount = 5;
        attack.velocity = new Vector2();
        attack.hitbox.width = 48;
        attack.hitbox.height = 80;
        attack.drawRect.width = 64;
        attack.drawRect.height = 96;
        attack.damage = 8;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 8;
        attack.reflectable = false;

        offset = new Vector2(8, 64);
        usePlayerDirection = true;
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        if (attack.life % 3 == 0) attack.frame++;
        attack.owner.velocity.y = 12;
    }

    @Override
    public void hit(Attack attack, GameObject hit) {
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.knockback = new Vector2(0, 8);
        SABSounds.playSound("swish.mp3");
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        super.render(attack, g);
    }
}
