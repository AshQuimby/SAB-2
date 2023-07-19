package sab.game.attack.walouis;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.Seagraphics;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.util.Utils;

public class Saxophone extends AttackType {
    private Vector2 target;
    private Vector2 startPos;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "saxophone.png";
        attack.canHit = false;
        attack.hitbox = new Rectangle(0, 0, 1, 1);
        attack.drawRect = new Rectangle(0, 0, 32, 56);
        attack.life = 740 + 90;
        attack.reflectable = false;
        attack.parryable = false;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.hitbox.x = Utils.randomPointInRect(attack.getStage().getSafeBlastZone()).x / 2;
        attack.hitbox.y = attack.getStage().getSafeBlastZone().y + attack.getStage().getSafeBlastZone().height + 64;
        target = attack.owner.getCenter();
        startPos = attack.getCenter();
        attack.direction = -attack.owner.direction;
    }

    @Override
    public void update(Attack attack) {
        if (attack.life >= 740) {
            Vector2 step = target.cpy().sub(startPos).scl(1 / 91f);
            attack.hitbox.x += step.x;
            attack.hitbox.y += step.y;
        }
        attack.rotation += -12 * attack.direction;
    }

    @Override
    public void onKill(Attack attack) {
        SabSounds.unpauseMusic();
        attack.owner.velocity.scl(0);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        g.getDynamicCamera().targetZoom = attack.getStage().maxZoomOut;
        if (attack.life >= 740) super.render(attack, g);
        int spotlightSize = 3840;
        Color alpha = new Color(1, 1, 1, Math.min(1, attack.life / 30f));
        g.usefulTintDraw(g.imageProvider.getImage("walouis_spotlight.png"), attack.getCenter().x - spotlightSize / 2, attack.getCenter().y - spotlightSize / 2, spotlightSize, spotlightSize, 0, 1, 0, false, false, alpha);
    }
}
