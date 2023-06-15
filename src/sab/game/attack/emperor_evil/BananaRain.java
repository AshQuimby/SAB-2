package sab.game.attack.emperor_evil;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import org.lwjgl.system.MathUtil;
import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.SABSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.particle.Particle;
import sab.net.Keys;
import sab.util.SABRandom;

public class BananaRain extends AttackType {
    private static final int cloudTransitionTime = 120;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.life = 840;
        attack.hitbox.y = 352;
        attack.hitbox.width = 1;
        attack.hitbox.height = 1;
        attack.damage = 12;
        attack.directional = true;
        attack.collideWithStage = true;
        attack.canHit = false;
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
        if (attack.life % 10 == 0 && attack.life < 820 - cloudTransitionTime && attack.life > cloudTransitionTime) attack.getBattle().addAttack(new Attack(new Banana(), attack.owner), new int[] { (int) SABRandom.random(attack.getStage().getSafeBlastZone().x, (int) attack.getStage().getStageEdge(Direction.RIGHT)) });
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
        Vector2 backgroundCloudPosition = new Vector2(-attack.life * 0.5f % 1280 - 640, attack.getStage().getStageEdge(Direction.UP));
        backgroundCloudPosition.y -= MathUtils.cos(attack.life / 40f) * 5 - 16 + 720 - 48;

        backgroundCloudPosition.y += attack.hitbox.y;

        for (int i = -1; i < 2; i++) {
            g.usefulDraw(g.imageProvider.getImage("banana_cloud_background.png"), backgroundCloudPosition.x + 1280 * i, backgroundCloudPosition.y, 1280, 720, 0, 1, 0, false, false);
        }
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        Vector2 cloudPosition = new Vector2(-attack.life % 1280 - 640, attack.getStage().getStageEdge(Direction.UP));
        cloudPosition.y -= MathUtils.sin(attack.life / 20f) * 5 + 720 - 48;

        cloudPosition.y += attack.hitbox.y;

        for (int i = -1; i < 2; i++) {
            g.usefulDraw(g.imageProvider.getImage("banana_cloud.png"), cloudPosition.x + 1280 * i, cloudPosition.y, 1280, 720, 0, 1, 0, false, false);
        }
    }
}
