package sab.game;

import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.ai.AI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.fighter.Fighter;
import sab.game.particle.Particle;
import sab.game.stage.Ledge;
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
    public boolean invulnerable;

    public GameStats gameStats;

    private AI ai;

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
    private int respawnTime;
    private int usedCharge;
    private int smokeGenerator;
    private int id;
    private int freezeFrame;
    private boolean repeatAttack;
    private boolean charging;
    private boolean hide;
    private boolean ledgeGrabbing;

    public Player(Fighter fighter, int costume, int id, Battle battle) {
        this.battle = battle;

        velocity = new Vector2();
        knockback = new Vector2();
        keys = new InputState(6);

        respawnTime = 0;

        currentAction = null;

        this.fighter = fighter;

        hitbox = new Rectangle(0, 0, fighter.hitboxWidth, fighter.hitboxHeight);
        hitbox.setCenter(new Vector2(128 * (id == 0 ? -1 : 1), battle.getStage().getSafeBlastZone().height / 2 + Math.max(respawnTime, 120) - 512 - hitbox.height));
        drawRect = new Rectangle(0, 0, fighter.renderWidth, fighter.renderHeight);

        direction = 1;

        frameCount = fighter.frames;
        frame = 0;

        invulnerable = false;

        imageName = fighter.id + ".png";

        lives = 5;

        damage = 0;

        ledgeCooldown = 5;
        ledgeGrabs = 5;

        this.costume = costume;

        hide = false;
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

        gameStats = new GameStats("Human " + fighter.name, id);
    }

    public void setAI(AI ai) {
        if (ai != null) gameStats.setType("AI " + fighter.name, id);
        this.ai = ai;
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
                    Direction tryDirection = CollisionResolver.resolveY(this, step.y, platform.hitbox);
                    if (tryDirection != Direction.NONE)
                        collisionDirection = tryDirection;
                }
            }

            if (collisionDirection == Direction.DOWN) {
                touchingStage = true;
            }

            if (knockbackDuration > 0) {
                if (smokeGenerator-- <= 0) {
                    battle.addParticle(new Particle(
                            hitbox.getCenter(new Vector2()),
                            new Vector2(),
                            32, 32,
                            6,
                            4,
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
        charging = false;
        charge = 0;
        currentAction = null;
    }

    public void removeJumps() {
        extraJumpsUsed = fighter.jumps;
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

    public int getId() {
        return id;
    }

    public int getLives() {
        return lives;
    }

    public void kill(int livesCost) {
        for (int i = 0; i < 16; i++) {
            battle.addParticle(new Particle(hitbox.getCenter(new Vector2()), hitbox.getCenter(new Vector2()).scl(-0.025f * MathUtils.random(0.125f, 1f)).rotateDeg(MathUtils.random(-2.5f, 2.5f)), 128, 128, "twinkle.png"));
        }
        battle.shakeCamera(5);
        rotation = 0;
        velocity.scl(0);
        knockback.scl(0);
        usedRecovery = false;
        extraJumpsUsed = 0;
        stunned = 0;
        resetAction();
        frozen = 0;
        knockbackDuration = 0;
        respawnTime = 180;
        gameStats.died();
        battle.getPlayer(1 - id).gameStats.gotKill();
        SABSounds.playSound("death.mp3");
        lives -= livesCost;
        damage = 0;

        for (GameObject gameObject : battle.getGameObjects()) {
            if (gameObject instanceof Attack) {
                if (((Attack) gameObject).owner == this) {
                    battle.removeGameObject(gameObject);
                    ((Attack) gameObject).alive = false;
                }
            }
        }

        if (lives <= 0) {
            stunned = 100000;
            respawnTime = 0;
            battle.shakeCamera(10);
        }
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

        if (lives == 0) return;

        if (respawnTime > 0) {
            respawnTime--;
            invulnerable = true;
            hitbox.setCenter(new Vector2(128 * (id == 0 ? -1 : 1), battle.getStage().getSafeBlastZone().height / 2 + Math.max(respawnTime * 2, 120) - 280 + hitbox.height / 2));
            frame = 0;
            if ((keys.isPressed(Keys.RIGHT) || keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.DOWN) || keys.isPressed(Keys.UP)) && respawnTime < 100 || respawnTime == 0) {
                invulnerable = false;
                respawnTime = 0;
            } else {
                return;
            }
        }

        if (stunned > 0) {
            stunned--;
            return;
        }
        
        if (ai != null) ai.update();

        if (frozen > 0) {
            frozen--;
        }

        Ledge ledge = null;
        if (ledgeCooldown <= 0 && ledgeGrabs > 0) ledge = battle.getStage().grabLedge(this);
        ledgeGrabbing = ledge != null;

        if (knockbackDuration > 0) {
            if (currentAction != null && !currentAction.isImportant()) currentAction = null;

            if (ledge != null) { 
                ledgeCooldown = 8;
                ledge = null;
            }

            usedRecovery = false;
            velocity = knockback.cpy().scl(1.5f / (fighter.mass / 2f + 2));
            gravityAndFriction();
            repeatAttack = false;
            usedCharge = 0;
            charge = 0;
            charging = false;
            if (knockbackDuration-- <= 0) {
                knockback = new Vector2(0, 0);
                velocity.scl(0.2f);
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
            rotation = 0;
            usedRecovery = false;
            extraJumpsUsed = 0;
            if (keys.isJustPressed(Keys.DOWN)) {
                ledgeCooldown = 8;
                ledgeGrabs--;
                velocity.y = -getJumpVelocity() / 2;
            } else if (keys.isJustPressed(Keys.UP)) {
                ledgeCooldown = 8;
                ledgeGrabs--;
                velocity.y = getJumpVelocity();
            }
            return;
        }

        if (touchingStage) {
            extraJumpsUsed = 0;
            usedRecovery = false;
            ledgeGrabs = 5;
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
            if (keys.isPressed(Keys.ATTACK) || charge < minCharge) {
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
                minCharge = 0;
                charging = false;
            } else {
                gravityAndFriction();
                return;
            }
        }

        if (keys.isPressed(Keys.LEFT)) {
            velocity.x += Math.max(-fighter.acceleration, -fighter.speed - velocity.x);
            if (!keys.isPressed(Keys.RIGHT)) direction = -1;
        }
        if (keys.isPressed(Keys.RIGHT)) {
            velocity.x += Math.min(fighter.acceleration, fighter.speed - velocity.x);
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

        gravityAndFriction();
    }

    public int getRemainingJumps() {
        return fighter.jumps - extraJumpsUsed;
    }

    // Returns true if the player has a condition that would lock them out of normal control, like knockback, ledge grabing, being frozen, etc
    public boolean stuckCondition() {
        return frozen() || takingKnockback() || grabbingLedge() || stunned() || charging();
    }

    public boolean charging() {
        return charging;
    }

    public boolean stunned() {
        return stunned > 0;
    }

    public boolean grabbingLedge() {
        return ledgeGrabbing;
    }

    public int getCharge() {
        return charge;
    }

    public boolean frozen() {
        return frozen > 0;
    }

    public boolean respawning() {
        return respawnTime > 0;
    }

    public boolean hasAction() {
        return currentAction != null;
    }

    public int getUsedCharge() {
        return usedCharge;
    }

    public void hide() {
        hide = true;
    }

    public void reveal() {
        hide = false;
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
        velocity.y -= 0.96f;

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
        
        if (lives > 0 && ((knockbackDuration > 0 && !hitbox.overlaps(battle.getStage().getSafeBlastZone())) || (hitbox.x + hitbox.width < battle.getStage().getUnsafeBlastZone().x || hitbox.x > battle.getStage().getUnsafeBlastZone().x + battle.getStage().getUnsafeBlastZone().width || hitbox.y + hitbox.height < battle.getStage().getUnsafeBlastZone().y))) {
            kill(1);
        }
        
        fighter.update(this);
    }

    public boolean takingKnockback() {
        return knockbackDuration > 0;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        return !invulnerable;
    }

    @Override
    public boolean onHit(DamageSource source) {
        int damageBefore = damage;
        damage += source.damage;

        battle.shakeCamera(2);
        battle.freezeFrame(2 + (source.damage / 25), 0, 0, false);

        SABSounds.playSound("hit.mp3");
        Vector2 newKnockback = source.knockback.cpy().scl(4f).scl(damage / 100f + 1f);
        if (newKnockback.len() > knockback.len()) {
            smokeGenerator = 0;
            knockback.set(newKnockback);
        } else {
            knockback.setAngleRad(newKnockback.angleRad());
        }
        knockbackDuration = (int) (knockback.len() * 0.225f);

        boolean shouldDie = false;

        if (knockback.len() > 30) {

            Rectangle death = new Rectangle(hitbox);
            Vector2 tempKnockback = knockback.cpy();

            for (int i = 0; i < knockbackDuration + 12; i++) {
                Vector2 moveBy = tempKnockback.cpy().scl(1.5f / (fighter.mass / 2f + 2));
                death.x += moveBy.x;
                death.y += moveBy.y;
                velocity.y -= 0.96f;
                tempKnockback.add(tempKnockback.cpy().scl(-fighter.friction).scl(1f / fighter.mass));
                if (!death.overlaps(battle.getStage().getSafeBlastZone())) {
                    shouldDie = true;
                }
            }

            if (shouldDie) {
                battle.smashScreen();
                battle.shakeCamera(10);
            }
        }

        battle.getStage().onPlayerHit(this, source, shouldDie);

        gameStats.tookDamage(damage - damageBefore);
        return true;
    }

    @Override
    public void render(Seagraphics g) {
        if (!hide) {
            drawRect.setCenter(hitbox.getCenter(new Vector2()).add(fighter.imageOffsetX * direction, fighter.imageOffsetY));
            String costumeString = fighter.id + (costume == 0 ? "" : "_alt_" + costume) + ".png";
            if (frozen > 0) {
                frame = freezeFrame;
            }
            preRender(g);
            g.usefulDraw(g.imageProvider.getImage(costumeString), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, direction == 1, false);
            postRender(g);

            if (frozen > 0) {
                g.usefulDraw(g.imageProvider.getImage("ice.png"), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, 0, 1, rotation, false, false);
            }
            if (respawnTime > 0) {
                g.usefulDraw(g.imageProvider.getImage("p" + (id + 1) + "_spawn_platform.png"), drawRect.getCenter(new Vector2()).x - 40, drawRect.y - 32, 80, 32, 0, 1, rotation, false, false);
            }
        }
    }
}