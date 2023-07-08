package sab.game.attack.snas;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Player;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.util.Utils;

public class SuperBaster extends MeleeAttackType {
    private boolean fired;
    private Player target;

    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "glaster_baster.png";
        attack.hitbox.width = 76;
        attack.hitbox.height = 76;
        attack.drawRect.set(attack.hitbox);
        attack.reflectable = false;
        attack.life = -1;
        attack.hitCooldown = 8;
        attack.damage = 30;
        attack.canHit = false;
        attack.frameCount = 2;
        fired = false;
        offset = new Vector2(86, 8);
        usePlayerDirection = false;
        attack.parryable = false;
        killWhenPlayerStuck = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        super.onSpawn(attack, data);
        attack.direction = 1;
        switch (data[0]) {
            case 1 :
                offset = new Vector2(0, 86);
                break;
            case 2 :
                offset = new Vector2(-86, 8);
                break;
            case 3 :
                offset = new Vector2(0, -86);
                break;
        }
    }

    @Override
    public void update(Attack attack) {
        if (!fired) {
            super.update(attack);
            target = attack.getNearestOpponent(-1);
            Vector2 toTarget = target.getCenter().sub(attack.getCenter());
            if (attack.life > -60) {
                attack.rotation = toTarget.angleDeg();
            }
            attack.knockback = new Vector2(16, 0).setAngleDeg(attack.rotation);
            if (attack.life < -120) {
                fired = true;
                attack.life = 30;
                SabSounds.playSound("glaster_baster.mp3");
            }
        } else {
            attack.frame = 1;
        }
        attack.canHit = fired;
    }

    @Override
    public boolean canHit(Attack attack, GameObject hit) {
        return Utils.raycast(attack.getCenter(), attack.rotation, 500, target.hitbox);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        g.shapeRenderer.setColor(Color.WHITE);
        if (fired) {
            Rectangle laserRect = new Rectangle();
            laserRect.setSize(500, attack.life / 0.9375f);
            laserRect.setCenter(attack.getCenter().add(MathUtils.cosDeg(attack.rotation) * 250, MathUtils.sinDeg(attack.rotation) * 250));
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), laserRect.x, laserRect.y, (int) laserRect.width, (int) laserRect.height, 0, 1, attack.rotation, false, false, new Color(1, 1, 1, 0.5f));
            laserRect.setSize(500, attack.life / 1.875f);
            laserRect.setCenter(attack.getCenter().add(MathUtils.cosDeg(attack.rotation) * 250, MathUtils.sinDeg(attack.rotation) * 250));
            g.usefulTintDraw(g.imageProvider.getImage("pixel.png"), laserRect.x, laserRect.y, (int) laserRect.width, (int) laserRect.height, 0, 1, attack.rotation, false, false, new Color(1, 1, 1, 0.75f));
        }
        super.render(attack, g);
    }
}
