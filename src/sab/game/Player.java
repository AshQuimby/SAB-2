package sab.game;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.animation.Animation;
import sab.game.attacks.Attack;
import sab.game.fighters.Fighter;
import sab.game.particles.Particle;
import sab.game.stages.Ledge;
import sab.net.Keys;

public class Player extends GameObject implements Hittable {
    public final Battle battle;

    public InputState keys;
    public Fighter fighter;
    public int damage;
    public boolean touchingStage;
    public boolean usedRecovery;
    public int costume;
    public Vector2 knockback;

    private PlayerAction currentAction;
    private Direction collisionDirection;
    private int knockbackDuration;
    private int lives;
    private int frozen;
    private int extraJumpsUsed;
    private int minCharge;
    private int maxCharge;
    private int stunned;
    private int charge;
    private int ledgeCooldown;
    private int ledgeGrabs;
    private int usedCharge;
    private int smokeGenerator;
    private int id;
    private int freezeFrame;
    private boolean repeatAttack;
    private boolean charging;

    public Player(Fighter fighter, int costume, int id, Battle battle) {
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
        freezeFrame = 0;
        frozen = 0;
        repeatAttack = false;
        charging = false;
        usedRecovery = false;
        maxCharge = 0;
        charge = 0;
        knockbackDuration = 0;
        smokeGenerator = 0;
        this.id = id;
    }

    public void move(Vector2 v) {
        Vector2 movement = v.cpy();

        while (movement.len() > 0) {
            Vector2 step = movement.cpy().limit(1);
            movement.sub(step);

            collisionDirection = CollisionResolver.moveWithCollisions(this, step, battle.getPlatforms());

            List<GameObject> passablePlatforms = battle.getPassablePlatforms();
            
            for (GameObject platform : passablePlatforms) {
                if (!keys.isPressed(Keys.DOWN) && velocity.y <= 0
                        && hitbox.y > platform.hitbox.y + platform.hitbox.height - 12) {
                    Direction tryDirection = CollisionResolver.resolveY(this, step, platform);
                    if (tryDirection != Direction.NONE)
                        collisionDirection = tryDirection;
                }
            }

            if (collisionDirection == Direction.DOWN) {
                touchingStage = true;
            }

            if (knockbackDuration > 0) {
                if (smokeGenerator-- <= 0) {
                    battle.addParticle(new Particle(hitbox.getCenter(new Vector2()), new Vector2(), 32, 32, 6, 4,
                            "p" + (id + 1) + "_smoke.png"));
                    smokeGenerator = 60;
                }
                if (collisionDirection == Direction.UP || collisionDirection == Direction.DOWN) {
                    knockback.y *= -1;
                } else if (collisionDirection == Direction.RIGHT || collisionDirection == Direction.LEFT) {
                    knockback.x *= -1;
                }
            }
        }
    }

    public void resetAction() {
        currentAction = null;
    }

    public void startAnimation(int delay, Animation animation, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, animation, important, endLag);
    }

    public void startAttack(Attack attack, int delay, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, attack, important, endLag, null);
    }

    public void startAttack(Attack attack, Animation animation, int delay, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, null);
    }

    public void startAttack(Attack attack, Animation animation, int delay, int endLag, boolean important, int[] data) {
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, data);
    }

    public void startRepeatingAttack(Attack attack, Animation animation, int delay, int endLag, boolean important, int[] data) {
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, data);
        repeatAttack = true;
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
        knockback.add(force.cpy().scl(1f / fighter.mass));
    }

    @Override
    public void update() {
        usedCharge = 0;
        if (stunned > 0) {
            stunned--;
            return;
        }

        if (frozen > 0) {
            frozen--;
        }

        Ledge ledge = null;
        if (ledgeCooldown <= 0) ledge = battle.getStage().grabLedge(this);

        if (knockbackDuration > 0) {
            if (currentAction != null && !currentAction.isImportant()) currentAction = null;

            if (ledge != null) { 
                ledgeCooldown = 8;
                ledge = null;
            }

            velocity = knockback.cpy().scl(1f / fighter.mass);
            gravityAndFriction();
            repeatAttack = false;
            usedCharge = 0;
            charge = 0;
            charging = false;
            if (knockbackDuration-- <= 0) {
                knockback = new Vector2(0, 0);
            }
            return;
        } else {
            knockback = new Vector2(0, 0);
        }

        if (frozen > 0) {
            return;
        }
        
        if (ledgeCooldown > 0) {
            ledgeCooldown--;
            ledge = null;
        }

        if (ledge != null) {
            direction = ledge.direction;
            hitbox.setPosition(ledge.getGrabPosition(hitbox));
            velocity.scl(0);
            frame = fighter.ledgeAnimation.stepLooping();
            usedRecovery = false;
            if (keys.isJustPressed(Keys.DOWN)) {
                ledgeCooldown = 8;
                velocity.y = -getJumpVelocity() / 2;
            } else if (keys.isJustPressed(Keys.UP)) {
                ledgeCooldown = 8;
                velocity.y = getJumpVelocity();
            }
            return;
        }

        if (touchingStage) {
            extraJumpsUsed = 0;
            usedRecovery = false;
            if (velocity.y < 0) velocity.y = 0;
        }
        if (collisionDirection == Direction.RIGHT || collisionDirection == Direction.LEFT) {
            velocity.x = 0;
        }
        
        if (charging) {
            if (++charge >= maxCharge) {
                charge = maxCharge;
            }
            fighter.charging(this, charge);
            if (keys.isPressed(Keys.ATTACK)) {
                if (currentAction != null) {
                    currentAction.changeDelay(1);
                }
            }
        }

        if (currentAction != null) {
            currentAction.update(this);
            if (currentAction.finished()) {
                currentAction = null;
                if (charging) fighter.chargeAttack(this, charge);
                usedCharge = charge;
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
        if (keys.isJustPressed(Keys.ATTACK) || (repeatAttack && keys.isPressed(Keys.ATTACK))) {
            if (keys.isPressed(Keys.DOWN)) {
                fighter.downAttack(this);
            } else if (keys.isPressed(Keys.UP)) {
                fighter.upAttack(this);
            } else if (keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.RIGHT)) {
                fighter.sideAttack(this);
            } else {
                fighter.neutralAttack(this);
            }
        } else {
            repeatAttack = false;
        }

        if (usedRecovery) frame = fighter.freefallAnimation.stepLooping();

        fighter.update(this);
        gravityAndFriction();
    }

    public int getCharge() {
        return charge;
    }

    public int getUsedCharge() {
        return usedCharge;
    }

    public void stun(int ticks) {
        if (ticks > stunned) stunned = ticks;
    }

    public void freeze(int ticks) {
        if (ticks > frozen) frozen = ticks;
        freezeFrame = frame;
    }

    private float getJumpVelocity() {
        // Don't ask. -a_viper
        // I highly recommend DMing a_viper and asking -AshQuimby
        float h0 = fighter.jumpHeight;
        float g = 1.6f;
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
        if (touchingStage && (!(keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) || charging)) {
            velocity.x *= 0.3f;
        }
    }

    @Override
    public void postUpdate() {

        touchingStage = false;

        if (stunned <= 0) move(velocity);
        
        if (knockbackDuration > 0) frame = fighter.knockbackAnimation.stepLooping();

        keys.update();
        
        if (hitbox.y < -704 / 2 - fighter.hitboxHeight) {
            kill(1);
        }
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return true;
    }

    @Override
    public boolean onHit(DamageSource source) {
        damage += source.damage;
        Vector2 newKnockback = source.knockback.cpy().scl(4f).scl(damage / 100f + 1f);
        if (newKnockback.len() > knockback.len()) {
            smokeGenerator = 0;
            knockback.set(newKnockback);
        } else {
            knockback.setAngleRad(newKnockback.angleRad());
        }
        knockbackDuration = (int) (knockback.len() * 0.25f);
        return true;
    }

    @Override
    public void render(Seagraphics g) {
        drawRect.setCenter(hitbox.getCenter(new Vector2()).add(fighter.imageOffsetX * direction, fighter.imageOffsetY));
        String costumeString = fighter.id + (costume == 0 ? "" : "_alt_" + costume) + ".png";
        if (frozen > 0) {
            frame = freezeFrame;
        }
        preRender(g);
        g.usefulDraw(g.imageProvider.getImage(costumeString), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, direction == 1, false);
        postRender(g);

        if (frozen > 0) {
            g.usefulDraw(g.imageProvider.getImage("ice.png"), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, false, false);
        }
    }
}