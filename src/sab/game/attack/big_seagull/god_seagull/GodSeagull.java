package sab.game.attack.big_seagull.god_seagull;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import jdk.jfr.Percentage;
import sab.game.Direction;
import sab.game.Game;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.SabRandom;
import sab.util.Utils;

public class GodSeagull extends AttackType implements Deity {
    private static final int cloudTransitionTime = 120;
    private Vector2 godSeagullPos;
    private float godRotationSpeed;
    private float godRotation;
    private int idleTime;
    private int bulletsShot;
    private int timeLeft;
    private Vector2 godSeagullTarget;
    private Vector2 laserTarget;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.life = 2048;
        attack.hitbox.y = 352;
        attack.hitbox.width = 1;
        attack.hitbox.height = 1;
        attack.drawRect.width = 620;
        attack.drawRect.height = 620;
        attack.damage = 12;
        attack.directional = true;
        attack.collideWithStage = true;
        attack.canHit = false;
        godSeagullPos = new Vector2(0, Game.game.window.resolutionY);
        godSeagullTarget = new Vector2(0, 0);
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.velocity.y = -352 / 32f;
    }

    @Override
    public void update(Attack attack) {
        attack.velocity.scl(31f / 32f);
        if (attack.life == cloudTransitionTime) {
            attack.velocity.y = 352 / 32f;
        }

        godSeagullPos.add(godSeagullTarget.cpy().sub(godSeagullPos).scl(0.05f));

        if (timeLeft > 0) {
            timeLeft--;
            if (timeLeft == 60) godSeagullTarget = new Vector2(godSeagullPos.x,  Game.game.window.resolutionY);
            if (timeLeft == 0) attack.alive = false;
            return;
        }

        if (idleTime > 0) {
            idleTime--;
            if (idleTime == 60) {
                if ((bulletsShot + 1) % 3 == 0) {
                    if ((bulletsShot + 1) % 6 == 0) {
                        createAttack(new GodLaser(), new int[]{(int) godSeagullPos.x, (int) godSeagullPos.y, (int) laserTarget.sub(godSeagullPos).angleDeg() }, attack.owner);
                        timeLeft = 120;
                    } else {
                        for (int i = 0; i < 3; i++) createAttack(new GodEye(), new int[]{(int) godSeagullPos.x, (int) godSeagullPos.y}, attack.owner);
                    }
                } else {
                    createAttack(new GodBolt(), new int[]{(int) godSeagullPos.x, (int) godSeagullPos.y}, attack.owner);
                }
                bulletsShot++;
            }
            if (idleTime == 0) {
                godSeagullTarget = Utils.randomPointInRect(attack.getStage().getSafeBlastZone()).scl(0.8f);
            }
        } else {
            godRotationSpeed += (godSeagullTarget.x - godSeagullPos.x) / 100;
            if (godSeagullPos.dst2(godSeagullTarget) < 64) {
                laserTarget = attack.getNearestOpponent(-1).getCenter();
                idleTime = 120;
            }
        }
        godRotation -= godRotationSpeed;
        godRotationSpeed *= 0.99f;
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        Vector2 backgroundCloudPosition = new Vector2(-attack.life * 0.5f % 1280 - 640, attack.getStage().getStageEdge(Direction.UP));
        backgroundCloudPosition.y -= MathUtils.cos(attack.life / 40f) * 5 - 16 + 720 - 48;

        for (int i = -1; i < 2; i++) {
             g.usefulDraw(g.imageProvider.getImage("dust_cloud_background.png"), backgroundCloudPosition.x + 1280 * i, backgroundCloudPosition.y, 1280, 720, 0, 1, 0, false, false);
        }

        backgroundCloudPosition.y += attack.hitbox.y;

        int godFrame = 5 - attack.life / 4 % 6;
        godRotation = 0;
        for (int i = 0; i < 3; i++) {
            float wingRotation = MathUtils.sinDeg(attack.life / 1.5f + i * 120f) * 8f;
            g.usefulDraw(g.imageProvider.getImage("golden_wing.png"), godSeagullPos.x + 64 - 1360 + 680 - (i == 1 ? 128 : 0), godSeagullPos.y + 64 * (i - 1) - 680, 1360, 1360, 0, 1, 45 * i + wingRotation, false, false);
            g.usefulDraw(g.imageProvider.getImage("golden_wing.png"), godSeagullPos.x - 64 - 680 + (i == 1 ? 128 : 0), godSeagullPos.y + 64 * (i - 1) - 680, 1360, 1360, 0, 1, -45 * i - wingRotation, true, false);
        }
        int ultimateGraceExtraSize = 320;
        g.usefulDraw(g.imageProvider.getImage("ultimate_grace.png"), godSeagullPos.x - attack.drawRect.width / 2 - ultimateGraceExtraSize / 2, godSeagullPos.y - attack.drawRect.height / 2 - ultimateGraceExtraSize / 2, (int) attack.drawRect.width + ultimateGraceExtraSize, (int) attack.drawRect.height + ultimateGraceExtraSize, 0, 1, 0, false, false);
        g.usefulDraw(g.imageProvider.getImage("heartbeat.png"), godSeagullPos.x - attack.drawRect.width / 2, godSeagullPos.y - attack.drawRect.height / 2, (int) attack.drawRect.width, (int) attack.drawRect.height, godFrame, 6, godRotation, false, false);
        g.usefulDraw(g.imageProvider.getImage("god_heart.png"), godSeagullPos.x - attack.drawRect.width / 2, godSeagullPos.y - attack.drawRect.height / 2, (int) attack.drawRect.width, (int) attack.drawRect.height, godFrame, 6, 0, false, false);
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        Vector2 cloudPosition = new Vector2(-attack.life % 1280 - 640, attack.getStage().getStageEdge(Direction.UP));
        cloudPosition.y -= MathUtils.sin(attack.life / 20f) * 5 + 720 - 48;

        cloudPosition.y += attack.hitbox.y;

        for (int i = -1; i < 2; i++) {
            g.usefulDraw(g.imageProvider.getImage("dust_cloud.png"), cloudPosition.x + 1280 * i, cloudPosition.y, 1280, 720, 0, 1, 0, false, false);
        }
    }

    @Override
    public void PRAISE() {
        System.out.println("PRAISE BIG SEAGULL");
    }
}
