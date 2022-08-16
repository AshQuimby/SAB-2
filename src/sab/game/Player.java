package sab.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.fighters.Fighter;
import sab.game.fighters.FighterType;
import sab.net.Keys;

public class Player extends GameObject implements Hittable {
    public final Battle battle;

    public InputState keys;
    public Fighter fighter;
    public int damage;
    public boolean touchingStage;

    private PlayerAction currentAction;
    private Direction collisionDirection;
    private Vector2 knockback;
    private int lives;

    public Player(FighterType fighterType, Battle battle) {
        this.battle = battle;
        
        velocity = new Vector2();
        knockback = new Vector2();
        keys = new InputState(6);

        currentAction = null;

        fighter = new Fighter(fighterType);

        hitbox = new Rectangle(0, 0, fighter.hitboxWidth, fighter.hitboxHeight);
        drawRect = new Rectangle(0, 0, fighter.renderWidth, fighter.renderHeight);

        direction = 1;

        frameCount = fighter.frames;
        frame = 0;

        imageName = fighter.id + ".png";

        lives = 5;

        damage = 0;
    }

    public void startAttack(Attack attack, int delay, int endLag) {
        currentAction = new PlayerAction(delay, attack, true, endLag, 0);
    }

    public void startAttack(Attack attack, Animation animation, int delay, int endLag) {
        currentAction = new PlayerAction(delay, attack, animation, true, endLag, 0);
    }

    public int getLives() {
        return lives;
    }

    public void kill(int livesCost) {
        hitbox.setCenter(new Vector2());
        lives -= livesCost;
        damage = 0;
    }

    @Override
    public void preUpdate() {
        update();
        postUpdate();
    }

    @Override
    public void update() {
        if (knockback.len() > 8) {
            touchingStage = false;
            velocity.x = knockback.x;
            velocity.y = knockback.y;
            knockback.scl(0.9f);
            hitbox.x += velocity.x;
            hitbox.y += velocity.y;
            return;
        }

        hitbox.x += velocity.x;
        hitbox.y += velocity.y;
        
        if (touchingStage) {
            if (!keys.isPressed(Keys.LEFT) && !keys.isPressed(Keys.RIGHT)) velocity.x *= 0.3f;
            velocity.y = 0;
        }

        if (currentAction != null) {
            currentAction.update(this);
            if (currentAction.finished()) {
                currentAction = null;
            } else {
                gravityAndFriction();
                return;
            }
        }

        if (keys.isPressed(Keys.LEFT)) {
            velocity.x -= fighter.speed;
            direction = 1;
        }
        if (keys.isPressed(Keys.RIGHT)) {
            velocity.x += fighter.speed;
            direction = -1;
        }
        if (keys.isJustPressed(Keys.UP) && touchingStage) {
            velocity.y += getJumpVelocity();
        }

        if (currentAction == null) {
            if (keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) {
                frame = fighter.walkAnimation.step();
            } else {
                frame = 0;
                fighter.walkAnimation.reset();
            }

            // Attacks
            if (keys.isJustPressed(Keys.ATTACK)) {
                if (keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.RIGHT)) {
                    fighter.sideAttack(this);
                } else {
                    fighter.neutralAttack(this);
                }
            }
        }

        if (fighter.walkAnimation.isDone()) {
            fighter.walkAnimation.reset();
        }

        fighter.update(this);

        gravityAndFriction();
        hitbox.setPosition(hitbox.getPosition(new Vector2()).add(velocity));
        keys.update();

        touchingStage = false;
    }

    private float getJumpVelocity() {
        float h0 = fighter.jumpHeight;
        float g = 1;
        float m = fighter.mass;
        float k = fighter.friction;
        float v0 = (float) (-Math.sqrt(2 * g * h0) + (2f / 3f) * ((h0 * k) / m) - (h0 / (9 * Math.sqrt(2)) * Math.sqrt(h0 / g) * (k * k / m * m)));

        return -v0;
    }

    public void gravityAndFriction() {
        velocity.sub(velocity.cpy().scl(fighter.friction));
        velocity.y -= 1;

        if (touchingStage && !(keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT))) {
            velocity.sub(velocity.cpy().scl(fighter.friction * 3f));
        }
    }

    @Override
    public void postUpdate() {
        if (hitbox.y < -704 / 2 - fighter.hitboxHeight) {
            kill(1);
        }

        collisionDirection = CollisionResolver.movingResolve(this, battle.platform);

        drawRect.setCenter(hitbox.getCenter(new Vector2()).add(fighter.imageOffsetX, fighter.imageOffsetY));

        if (collisionDirection == Direction.UP) {
            touchingStage = true;
        }
    }

    @Override
    public boolean onHit(DamageSource source) {
        damage += source.damage;
        knockback.add(source.knockback.cpy().scl((damage) / 100f + 1f));
        return true;
    }
}