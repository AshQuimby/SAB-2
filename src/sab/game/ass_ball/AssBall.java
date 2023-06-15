package sab.game.ass_ball;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.*;
import sab.game.particle.Particle;
import sab.util.Utils;
import sab.util.SABRandom;

public class AssBall extends GameObject implements Hittable {
    private Color color;
    private Vector2 flyTo;
    private int health;
    private int age;
    private Battle battle;
    private float rotationSpeed;
    private Player lastPlayerToHit;
    private boolean killed;

    public AssBall(Vector2 position, Battle battle) {
        super();
        this.battle = battle;
        imageName = "ass_ball.png";
        hitbox = new Rectangle(position.x, position.y, 40, 40);
        drawRect = new Rectangle(hitbox);
        velocity = new Vector2();
        frameCount = 18;
        flyTo = new Vector2(0, 0);
        health = 50;
        killed = false;
    }

    public void update() {
        if (getCenter().dst2(flyTo) < 1600) flyTo = Utils.randomPointInRect(battle.getStage().getSafeBlastZone());
        rotationSpeed += -velocity.x / 16;
        rotation += rotationSpeed;
        rotationSpeed *= 0.9f;
        color = new Color(1f, 1f, 1f, 0.8f).fromHsv(age % 360, 1f, 1f);
        velocity = velocity.add(flyTo.cpy().sub(getCenter()).nor().scl(0.125f)).scl(63 / 64f);
        frame = age / 8 % 18;
        age++;
    }

    public void kill() {
        if (!killed) {
            for (int i = 0; i < 15; i++) {
                battle.addParticle(new Particle(getCenter(), new Vector2(4 * SABRandom.random(), 0).rotateDeg(SABRandom.random() * 360), 96, 96, 0, "twinkle.png"));
            }
            SABSounds.playSound("shatter.mp3");
            lastPlayerToHit.grantFinalAss();
            killed = true;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    @Override
    public boolean onHit(DamageSource source) {
        if (!killed) {
            velocity = source.knockback.cpy();
            health -= source.damage;
            if (source.owner != null) lastPlayerToHit = source.owner;
            if (health <= 0) {
                kill();
            } else {
                SABSounds.playSound("hit.mp3");
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return true;
    }

    @Override
    public void render(Seagraphics g) {
        // g.usefulDraw(g.imageProvider.getImage("glow.png"), drawRect.x - 20, drawRect.y - 20, (int) drawRect.width + 40, (int) drawRect.height + 40, 0, 1, 0, false, false);
        Color paleTint = new Color(1f, 1f, 1f, 1f).fromHsv(age % 360, 0.85f, 1);
        g.usefulTintDraw(g.imageProvider.getImage("ball_shine.png"), drawRect.x - 70, drawRect.y - 70, (int) drawRect.width + 140, (int) drawRect.height + 140, 0, 1, rotation * 2, false, false, paleTint);
        g.usefulDraw(g.imageProvider.getImage("ball_shine.png"), drawRect.x - 70, drawRect.y - 70, (int) drawRect.width + 140, (int) drawRect.height + 140, 0, 1, rotation, false, false);
        g.usefulTintDraw(g.imageProvider.getImage("ass_ball.png"), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, 0, 1, 0, false, false, color);
        g.usefulDraw(g.imageProvider.getImage("ass_ball_overlay.png"), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, 0, false, false);
    }
}
