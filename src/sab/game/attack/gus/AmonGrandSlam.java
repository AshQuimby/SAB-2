package sab.game.attack.gus;

import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;
import sab.game.Direction;
import sab.game.Player;
import sab.game.SABSounds;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;
import sab.game.particle.Particle;
import sab.net.Keys;

public class AmonGrandSlam extends MeleeAttackType {
    private Animation landAnimation;
    private float flipRotation;
    private float flipRotationSpeed;
    private boolean fallCheck;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "none.png";
        attack.basedOffCostume = true;
        attack.life = -1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 72;
        attack.hitbox.height = 72;
        attack.damage = 14;
        attack.direction = attack.owner.direction;
        attack.hitCooldown = 12;
        attack.reflectable = false;
        attack.parryable = false;
        flipRotationSpeed = 30;

        offset = new Vector2(0, 0);
        usePlayerDirection = true;
        landAnimation = new Animation(new int[] { 39, 0 }, 60, true);
    }

    @Override
    public void update(Attack attack) {
        attack.owner.velocity.y += 0.1f;
        if (attack.owner.keys.isPressed(Keys.LEFT)) {
            attack.owner.velocity.x -= 0.1f;
        }
        if (attack.owner.keys.isPressed(Keys.RIGHT)) {
            attack.owner.velocity.x += 0.1f;
        }
        super.update(attack);
        if (attack.owner.velocity.y > 4) {
            flipRotationSpeed -= 0.5f;
            flipRotation -= flipRotationSpeed;
            attack.owner.frame = 36;
            fallCheck = false;
            attack.canHit = false;
        } else {
            attack.canHit = true;
            if (!fallCheck) {
                flipRotation *= -1;
                fallCheck = true;
            }
            attack.knockback = new Vector2(attack.owner.velocity.x, attack.owner.velocity.y).scl(0.5f);
            flipRotation *= 0.6f;
            flipRotationSpeed = 0;
            if (attack.owner.velocity.y < -10) attack.owner.frame = 38;
            else attack.owner.frame = 37;
        }
        attack.owner.rotation = flipRotation * attack.direction + attack.owner.velocity.x;
        if (attack.owner.touchingStage) {
            attack.alive = false;
            attack.owner.startAnimation(1, landAnimation, 24, false);
            attack.getBattle().shakeCamera(12);
            SABSounds.playSound("crash.mp3");
            attack.owner.rotation = 0;
            attack.owner.velocity.scl(0);
            attack.getBattle().addParticle(new Particle(new Vector2(attack.owner.getCenter().x, attack.owner.getCenter().y + attack.owner.hitbox.height / 2 + 8), new Vector2(), 200, 32, 5, 6, -1, "amon_gus_landing.png"));
        }
    }

    @Override
    public void onKill(Attack attack) {
        if (!attack.owner.usingAnimation(landAnimation)) attack.owner.resetAction();
    }

    @Override
    public void successfulHit(Attack attack, GameObject hit) {
        attack.owner.battle.shakeCamera(5);
        if (hit instanceof Player) {
            Player hitPlayer = (Player) hit;
            flipRotationSpeed = 30;
            attack.owner.velocity.y = 20;
        }
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        attack.knockback = new Vector2(0, 12);
        moveToPlayer(attack);
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }
}