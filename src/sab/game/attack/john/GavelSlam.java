package sab.game.attack.john;

import com.badlogic.gdx.math.Vector2;

import com.seagull_engine.Seagraphics;
import sab.game.SabSounds;
import sab.game.attack.Attack;
import sab.game.attack.MeleeAttackType;

public class GavelSlam extends MeleeAttackType {
    private float swingSpeed;
    private boolean hitGround;
    @Override
    public void setDefaults(Attack attack) {
        attack.imageName = "gavel_with_arm.png";
        attack.basedOffCostume = true;
        attack.life = 30;
        attack.frameCount = 1;
        attack.velocity = new Vector2();
        attack.hitbox.width = 64;
        attack.hitbox.height = 64;
        attack.drawRect.width = 80;
        attack.drawRect.height = 80;
        attack.damage = 18;
        attack.hitCooldown = 10;
        attack.reflectable = false;
        attack.collideWithStage = true;
        offset = new Vector2();
        killWhenPlayerStuck = true;

        swingSpeed = 0.1f;
    }

    @Override
    public void onSpawn(Attack attack, int[] data) {
        updatePosition(attack);
        attack.knockback = new Vector2(attack.owner.direction * 9, 4);
        attack.direction = attack.owner.direction;
        attack.rotation = 60 * attack.direction;
    }

    private void setRootPosition(Attack attack, Vector2 rootPosition) {
        Vector2 position = rootPosition.cpy();
        Vector2 corner = new Vector2(attack.hitbox.width / 2 * attack.direction + 4 * -attack.direction, -attack.hitbox.height / 2 + 4);
        position.sub(corner.rotateDeg(attack.rotation - 45 * attack.direction));
        attack.hitbox.setCenter(position);
    }

    private void updatePosition(Attack attack) {
        attack.velocity = new Vector2(1, 0).setAngleDeg(attack.rotation + 90);
        setRootPosition(attack, attack.owner.getCenter().add(-8 * attack.owner.direction, 4));
    }

    @Override
    public void update(Attack attack) {
        super.update(attack);
        attack.canHit = swingSpeed > 2;
        if (hitGround) {
            updatePosition(attack);
        } else if (attack.collisionDirection.isVertical()) {
            createAttack(new JohnStar(), null, attack.owner);
            attack.life = 10;
            attack.collideWithStage = false;
            SabSounds.playSound("crash.mp3");
            attack.getBattle().shakeCamera(10);
            hitGround = true;
            updatePosition(attack);
        } else {
            attack.rotation -= swingSpeed * attack.direction;
            updatePosition(attack);
            swingSpeed += 0.25f * swingSpeed;
        }
    }

    @Override
    public void lateUpdate(Attack attack) {
    }

    @Override
    public void onKill(Attack attack) {
        attack.owner.frame = 0;
        attack.owner.resetAction();
    }

    @Override
    public void render(Attack attack, Seagraphics g) {
    }

    @Override
    public void lateRender(Attack attack, Seagraphics g) {
        super.render(attack, g);
    }
}
