package sab.game.attack.big_seagull.god_seagull;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import jdk.jshell.execution.Util;
import sab.game.Player;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.Utils;

public class GodLaser extends AttackType {
    @Override
    public void setDefaults(Attack attack) {
        attack.hitbox.width = 0;
        attack.hitbox.height = 0;
        attack.frameCount = 6;
        attack.hitCooldown = 5;
        attack.life = 45;
        attack.damage = 8;
        attack.knockback = new Vector2(6, 0);
        attack.imageName = "seagull_laser.png";
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.setCenter(new Vector2(data[0], data[1]));
        attack.rotation = data[2];
    }

    @Override
    public void update(Attack attack) {
        attack.rotation += (attack.getNearestOpponent(-1).getCenter().sub(attack.getCenter()).angleDeg() - attack.rotation) / 8f;
        attack.frame = Math.abs(3 - attack.life / 4 % 4);
        attack.knockback.setAngleDeg(attack.rotation);
    }

    @Override
    public boolean canHit(Attack attack, GameObject hit) {
        return Utils.raycast(attack.getCenter(), attack.rotation, 1280, hit.hitbox);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        Vector2 laserJointStep = new Vector2(64, 0).rotateDeg(attack.rotation);
        for (int i = 0; i < 40; i++) {
            g.usefulDraw(g.imageProvider.getImage(attack.imageName), attack.hitbox.x + laserJointStep.x * i - 32, attack.hitbox.y + laserJointStep.y * i - 32, 64, 64, attack.frame, 4, attack.rotation + 90, false, false);
            if (!attack.getStage().getSafeBlastZone().contains(new Vector2(attack.hitbox.x + laserJointStep.x * i - 32, attack.hitbox.y + laserJointStep.y * i - 32))) break;
        }
        g.usefulDraw(g.imageProvider.getImage("god_laser_origin.png"), attack.hitbox.x - 32, attack.hitbox.y - 32, 64, 64, 0, 1, 0, false, false);
    }
}
