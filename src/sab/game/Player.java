package sab.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.fighters.Fighter;
import sab.game.stages.Ledge;
import sab.net.Keys;

public class Player extends GameObject implements Hittable {
    public final Battle battle;

    public InputState keys;
    public Fighter fighter;
    public int damage;
    public boolean touchingStage;
    public int costume;

    private PlayerAction currentAction;
    private Direction collisionDirection;
    private Vector2 knockback;
    private int lives;
    private int extraJumpsUsed;
    private int minCharge;
    private int maxCharge;
    private int stunned;
    private int charge;
    private int ledgeCooldown;
    private int ledgeGrabs;
    private boolean charging;

    public Player(Fighter fighter, int costume, Battle battle) {
        this.battle = battle;
        
        velocity = new Vector2();
        knockback = new Vector2();
        keys = new InputState(6);

        currentAction = null;

        this.fighter = fighter;

        hitbox = new Rectangle(0, 0, fighter.hitboxWidth, fighter.hitboxHeight);
        drawRect = new Rectangle(0, 0, fighter.renderWidth, fighter.renderHeight);

        direction = 1;

        frameCount = fighter.frames;
        frame = 0;

        imageName = fighter.id + ".png";

        lives = 5;

        damage = 0;

        ledgeCooldown = 5;
        ledgeGrabs = 5;

        this.costume = costume;

        stunned = 0;
        charging = false;
        maxCharge = 0;
        charge = 0;
    }

    public void startAttack(Attack attack, int delay, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, attack, false, endLag, null);
    }

    public void startAttack(Attack attack, Animation animation, int delay, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, attack, animation, false, endLag, null);
    }

    public void startAttack(Attack attack, Animation animation, int delay, int endLag, boolean important, int[] data) {
        currentAction = new PlayerAction(delay, attack, animation, false, endLag, data);
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

    public void applyForce(Vector2 force) {
        // F = m * a, so a = F / m
        velocity.add(force.cpy().scl(1f / fighter.mass));
    }

    @Override
    public void update() {

        if (stunned > 0) {
            stunned--;
            return;
        }
        Ledge ledge = null;
        if (ledgeCooldown <= 0) ledge = battle.getStage().grabLedge(this);

        if (knockback.len() > 0) {
            if (currentAction != null && !currentAction.isImportant()) currentAction = null;

            if (ledge != null) { 
                ledgeCooldown = 8;
                ledge = null;
            }

            applyForce(knockback);
            charge = 0;
            charging = false;
            knockback.set(0, 0);
            return;
        }

        hitbox.x += velocity.x;
        hitbox.y += velocity.y;
        
        if (ledgeCooldown > 0) {
            ledgeCooldown--;
            ledge = null;
        }

        if (ledge != null) {
            direction = ledge.direction;
            hitbox.setPosition(ledge.getGrabPosition(hitbox));
            velocity.scl(0);
            frame = fighter.ledgeAnimation.stepLooping();
            if (keys.isJustPressed(Keys.DOWN)) {
                ledgeCooldown = 8;
                velocity.y = -getJumpVelocity() / 2;
            } else if (keys.isJustPressed(Keys.UP)) {
                ledgeCooldown = 8;
                velocity.y = getJumpVelocity() * 0.75f;
            } else return;
        }

        if (touchingStage) {
            extraJumpsUsed = 0;
            velocity.y = 0;
        }

        if (charging) {
            if (++charge >= maxCharge) {
                charge = maxCharge;
            }
            fighter.charging(this, charge);
            if (keys.isPressed(Keys.ATTACK)) {
                currentAction.changeDelay(1);
            }
        }

        if (currentAction != null) {
            currentAction.update(this);
            if (currentAction.finished()) {
                currentAction = null;
                if (charging) fighter.chargeAttack(this, charge);
                charge = 0;
                charging = false;
            } else {
                gravityAndFriction();
                return;
            }
        }

        if (keys.isPressed(Keys.LEFT)) {
            velocity.x -= fighter.speed;
            if (!keys.isPressed(Keys.RIGHT)) direction = -1;
        }
        if (keys.isPressed(Keys.RIGHT)) {
            velocity.x += fighter.speed;
            if (!keys.isPressed(Keys.LEFT)) direction = 1;
        }
        if (keys.isJustPressed(Keys.UP)) {
            if (touchingStage) {
                velocity.y = getJumpVelocity();
            } else if (extraJumpsUsed < fighter.jumps) {
                velocity.y = getJumpVelocity() * fighter.doubleJumpMultiplier;
                extraJumpsUsed++;
            }
        }

        if (keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) {
            frame = fighter.walkAnimation.stepLooping();
        } else {
            frame = 0;
            fighter.walkAnimation.reset();
        }

        // Attacks
        if (keys.isJustPressed(Keys.ATTACK)) {
            if (keys.isPressed(Keys.DOWN)) {
                fighter.downAttack(this);
            } else if (keys.isPressed(Keys.UP)) {
                fighter.upAttack(this);
            } else if (keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.RIGHT)) {
                fighter.sideAttack(this);
            } else {
                fighter.neutralAttack(this);
            }
        }

        fighter.update(this);

        gravityAndFriction();
        hitbox.setPosition(hitbox.getPosition(new Vector2()).add(velocity));
    }

    public void stun(int time) {
        if (time > stunned) stunned = time;
    }

    private float getJumpVelocity() {
        // Don't ask. -a_viper
        // I highly recommend DMing a_viper and asking -AshQuimby
        float h0 = fighter.jumpHeight;
        float g = 1.2f;
        float m = fighter.mass;
        float k = fighter.friction;
        float v0 = (float) (-Math.sqrt(2 * g * h0) + (2f / 3f) * ((h0 * k) / m) - (h0 / (9 * Math.sqrt(2)) * Math.sqrt(h0 / g) * (k * k / m * m)));

        return -v0;
    }

    public void startChargeAttack(PlayerAction action, int minCharge, int maxCharge) {
        currentAction = action;
        this.minCharge = minCharge;
        this.maxCharge = maxCharge;
        charging = true;
        charge = 0;
    }

    public void gravityAndFriction() {
        velocity.y -= 1.2f;

        applyForce(velocity.cpy().scl(-fighter.friction));
        if (touchingStage && !(keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT))) {
            velocity.x *= 0.3f;
        }
    }

    @Override
    public void postUpdate() {
        touchingStage = false;
        
        keys.update();
        
        if (hitbox.y < -704 / 2 - fighter.hitboxHeight) {
            kill(1);
        }

        Direction collisionDirection = CollisionResolver.movingResolve(this, battle.getPlatforms());
        if (collisionDirection == Direction.UP) touchingStage = true;

        

        drawRect.setCenter(hitbox.getCenter(new Vector2()).add(fighter.imageOffsetX, fighter.imageOffsetY));
    }

    @Override
    public boolean onHit(DamageSource source) {
        damage += source.damage;
        knockback.add(source.knockback.cpy().scl((damage) / 100f + 1f));
        return true;
    }

    @Override
    public void render(Seagraphics g) {
        String costumeString = fighter.id + (costume == 0 ? "" : "_alt_" + costume) + ".png";
        preRender(g);
        g.usefulDraw(g.imageProvider.getImage(costumeString), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, direction == 1, false);
        postRender(g);
    }
}