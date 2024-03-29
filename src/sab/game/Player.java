package sab.game;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.action.IndefinitePlayerAction;
import sab.game.action.PlayerAction;
import sab.game.ai.AI;
import sab.game.animation.Animation;
import sab.game.attack.Attack;
import sab.game.attack.AttackType;
import sab.game.fighter.Fighter;
import sab.game.input.InputState;
import sab.game.item.Item;
import sab.game.particle.Particle;
import sab.game.settings.Settings;
import sab.game.stage.Ledge;
import sab.game.stage.PassablePlatform;
import sab.game.stage.Slope;
import sab.game.stage.StageObject;
import sab.net.Keys;
import sab.replay.ReplayAI;
import sab.util.SabRandom;
import sab.util.Utils;

public class Player extends GameObject implements Hittable {
    public final Battle battle;

    public boolean usedMacro;
    private boolean assCharged;

    public boolean ignoreSlowdowns;

    public InputState keys;
    public Fighter fighter;
    public int damage;
    private boolean useHealth;
    private int health;
    private int startingHealth;
    public boolean touchingStage;
    public boolean usedRecovery;
    public int costume;
    public Vector2 knockback;
    public Vector2 drawRectOffset;
    public boolean invulnerable;

    public GameStats gameStats;

    private AI ai;

    private PlayerAction currentAction;
    private Direction collisionDirection;
    private Item heldItem;
    private int iFrames;
    private int parryTime;
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
    private int occupied;
    private int freezeFrame;
    private boolean repeatAttack;
    private boolean charging;
    private boolean hide;
    private boolean ledgeGrabbing;
    private boolean usedAirDodge;
    private int airDodging;
    private Vector2 airDodge;
    private float surfaceFriction;

    public Player(Fighter fighter) {
        this.fighter = fighter;
        this.drawRect = new Rectangle(0, 0, fighter.renderWidth, fighter.renderHeight);
        this.battle = null;
    }

    public Player(Fighter fighter, int costume, int id, int lives, Battle battle) {
        this.battle = battle;

        velocity = new Vector2();
        knockback = new Vector2();
        keys = new InputState(6);

        respawnTime = 0;

        currentAction = null;

        this.fighter = fighter;

        hitbox = new Rectangle(0, 0, fighter.hitboxWidth, fighter.hitboxHeight);
        hitbox.setCenter(new Vector2(id == 0 ? battle.getStage().player1SpawnX : battle.getStage().player2SpawnX,
                id == 0 ? battle.getStage().player1SpawnY : battle.getStage().player2SpawnY));
        if (battle.getStage().projectPlayerSpawns) move(new Vector2(0, -4000));
        drawRect = new Rectangle(0, 0, fighter.renderWidth, fighter.renderHeight);

        direction = 1;

        frameCount = fighter.frames;
        frame = 0;

        invulnerable = false;

        imageName = fighter.id + ".png";

        this.lives = lives;

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
        iFrames = 0;
        occupied = 0;
        parryTime = 0;

        heldItem = null;

        usedMacro = false;

        drawRectOffset = new Vector2();
        gameStats = new GameStats("Human " + fighter.name, id);
        fighter.start(this);
    }

    public Player(Fighter fighter, int costume, int id, int lives, Battle battle, int startHealth) {
        this(fighter, costume, id, lives, battle);
        useHealth = true;
        health = startHealth;
        startingHealth = startHealth;
    }

    public void setAI(AI ai) {
        if (ai != null && !(ai instanceof ReplayAI)) gameStats.setType("AI " + fighter.name, id);
        this.ai = ai;
    }

    public boolean isHuman() {
        return ai == null;
    }

    public void move(Vector2 v) {
        move(v, 1f);
    }

    public void move(Vector2 v, float physicsScalar) {
        Vector2 movement = v.cpy().scl(physicsScalar);

        while (movement.len() > 0) {
            Vector2 step = movement.cpy().limit(1);
            movement.sub(step);

            List<StageObject> solidStageObjects = battle.getSolidStageObjects();

            boolean collided = false;

            hitbox.x += step.x;
            for (StageObject collider : solidStageObjects) {
                Direction tryDirection = CollisionResolver.resolveX(this, step.x, collider.hitbox);
                if (tryDirection != Direction.NONE) {
                    collisionDirection = tryDirection;
                    collided = true;
                }
            }

            hitbox.y += step.y;
            for (StageObject collider : solidStageObjects) {
                Direction tryDirection = CollisionResolver.resolveY(this, step.y, collider.hitbox);
                if (tryDirection != Direction.NONE) {
                    surfaceFriction = collider.friction;
                    collisionDirection = tryDirection;
                    collided = true;
                }
            }

            for (Slope slope : battle.getStage().getSlopes()) {
                float m = slope.getSlope();
                if (isStuck() || -slope.outerDirection * m > 1.2f) {
                    if (hitbox.overlaps(slope.bounds)) {
                        float x = slope.getXIntersection(hitbox.y);
                        if (x >= hitbox.x && x <= hitbox.x + hitbox.width) {
                            if (slope.outerDirection == -1) {
                                hitbox.x = x - hitbox.width;
                            } else {
                                hitbox.x = x;
                            }
                        }

                        x = slope.getXIntersection(hitbox.y + hitbox.height);
                        if (x >= hitbox.x && x <= hitbox.x + hitbox.width) {
                            if (slope.outerDirection == -1) {
                                hitbox.x = x - hitbox.width;
                            } else {
                                hitbox.x = x;
                            }
                        }
                    }
                } else {
                    if (hitbox.overlaps(slope.bounds)) {
                        float y = slope.getYIntersection(hitbox.x);
                        if (y >= hitbox.y && y <= hitbox.y + hitbox.height) {
                            if (slope.outerDirection == -1) {
                                if (m >= 0) touchingStage = true;
                                hitbox.y = m > 0 ? y : y - hitbox.height;
                            } else {
                                if (m <= 0) touchingStage = true;
                                hitbox.y = m > 0 ? y - hitbox.height : y;
                            }
                        }

                        y = slope.getYIntersection(hitbox.x + hitbox.width);
                        if (y >= hitbox.y && y <= hitbox.y + hitbox.height) {
                            if (slope.outerDirection == -1) {
                                if (m >= 0) touchingStage = true;
                                hitbox.y = m > 0 ? y : y - hitbox.height;
                            } else {
                                if (m <= 0) touchingStage = true;
                                hitbox.y = m > 0 ? y - hitbox.height : y;
                            }
                        }
                    }
                }
            }

            if (!collided) collisionDirection = Direction.NONE;

            if (!keys.isPressed(Keys.DOWN)) {
                List<PassablePlatform> passablePlatforms = battle.getPassablePlatforms();
                for (PassablePlatform platform : passablePlatforms) {
                    if (velocity.y <= 0 && hitbox.y > platform.hitbox.y + platform.hitbox.height - 12) {
                        Direction tryDirection = CollisionResolver.resolveY(this, step.y, platform.hitbox);
                        if (tryDirection.isNotNone()) {
                            collisionDirection = tryDirection;
                            surfaceFriction = platform.friction;
                        }
                    }
                }
            }

            if (collisionDirection == Direction.DOWN) {
                touchingStage = true;
                usedAirDodge = false;
            }

            if (knockbackDuration > 0) {
                if (movement.len() > 1) {
                    if (keys.isPressed(Keys.DOWN)) {
                        movement.y -= 0.1f * physicsScalar;
                    } else if (keys.isPressed(Keys.UP)) {
                        movement.y += 0.1f * physicsScalar;
                    } else if (keys.isPressed(Keys.LEFT)) {
                        movement.x -= 0.1f * physicsScalar;
                    } else if (keys.isPressed(Keys.RIGHT)) {
                        movement.x += 0.1f * physicsScalar;
                    }
                }
                if (smokeGenerator-- <= 0) {
                    battle.addParticle(new Particle(hitbox.getCenter(new Vector2()), new Vector2(), 32, 32, 6, 4, "p" + (id + 1) + "_smoke.png"));
                    smokeGenerator = 60;
                }
                if (collisionDirection == Direction.UP || collisionDirection == Direction.DOWN) {
                    knockback.y *= -1;
                    movement.y *= -1;
                    v.y *= -1;
                } else if (collisionDirection == Direction.RIGHT || collisionDirection == Direction.LEFT) {
                    knockback.x *= -1;
                    movement.x *= -1;
                    v.x *= -1;
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
        extraJumpsUsed = fighter.airJumps;
    }

    public void removeLedgeGrabs() {
        ledgeGrabs = 0;
    }

    public void resetLedgeGrabs() {
        ledgeGrabs = 5;
    }

    public void startAnimation(int delay, Animation animation, int endLag, boolean important) {
        currentAction = new PlayerAction(delay, animation, important, endLag);
    }

    public Attack startIndefiniteAttack(AttackType type, int delay, boolean important) {
        Attack attack = new Attack(type, this);
        currentAction = new IndefinitePlayerAction(delay, attack, important, 0, null);
        repeatAttack = false;
        return attack;
    }

    public Attack startIndefiniteAttack(AttackType type, Animation animation, int delay, boolean important) {
        Attack attack = new Attack(type, this);
        currentAction = new IndefinitePlayerAction(delay, attack, animation, important, 0, null);
        repeatAttack = false;
        return attack;
    }

    public Attack startIndefiniteAttack(AttackType type, Animation animation, int delay, boolean important, int[] data) {
        Attack attack = new Attack(type, this);
        currentAction = new IndefinitePlayerAction(delay, attack, animation, important, 0, data);
        repeatAttack = false;
        return attack;
    }

    public Attack startAttack(AttackType type, int delay, int endLag, boolean important) {
        Attack attack = new Attack(type, this);
        currentAction = new PlayerAction(delay, attack, important, endLag, null);
        repeatAttack = false;
        return attack;
    }

    public Attack startAttack(AttackType type, Animation animation, int delay, int endLag, boolean important) {
        Attack attack = new Attack(type, this);
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, null);
        repeatAttack = false;
        return attack;
    }

    public Attack startAttack(AttackType type, Animation animation, int delay, int endLag, boolean important, int[] data) {
        Attack attack = new Attack(type, this);
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, data);
        repeatAttack = false;
        return attack;
    }

    public Attack startRepeatingAttack(AttackType type, Animation animation, int delay, int endLag, boolean important, int[] data) {
        Attack attack = new Attack(type, this);
        currentAction = new PlayerAction(delay, attack, animation, important, endLag, data);
        repeatAttack = true;
        return attack;
    }

    public int getLives() {
        return lives;
    }

    public void kill(int livesCost) {
        fighter.onKill(this);
        if (battle.getStage().getSafeBlastZone().contains(getCenter())) {
            for (int i = 0; i < 16; i++) {
                float particleSize = 64 * SabRandom.random(0.5f, 2f);
                battle.addParticle(new Particle(hitbox.getCenter(new Vector2()), Utils.randomParticleVelocity(8), particleSize, particleSize, "twinkle.png"));
            }
        } else {
            for (int i = 0; i < 16; i++) {
                battle.addParticle(new Particle(hitbox.getCenter(new Vector2()), hitbox.getCenter(new Vector2()).scl(-0.025f * SabRandom.random(0.125f, 1f)).rotateDeg(SabRandom.random(-2.5f, 2.5f)), 128, 128, "twinkle.png"));
            }
        }
        battle.shakeCamera(6);
        battle.onPlayerKilled(this);
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
        iFrames = 60;
        if (assCharged) battle.spawnAssBall();
        assCharged = false;
        battle.getPlayer(1 - id).gameStats.gotKill();
        SabSounds.playSound("death.mp3");
        lives -= livesCost;
        if (useHealth) {
            health = startingHealth;
        } else {
            damage = 0;
        }
        if (heldItem != null) heldItem.toss(this);

        for (Attack attack : battle.getAttacks()) {
            if (attack.originalOwner == this) {
                attack.alive = false;
            }
        }

        if (lives <= 0) {
            stunned = 100000;
            respawnTime = 0;
            battle.shakeCamera(16);
            hide();
        }
    }

    public void onEndBattle() {
        fighter.onEndBattle(this);
    }

    public boolean usingAnimation(Animation animation) {
        if (currentAction == null) return false;
        return currentAction.usingAnimation(animation);
    }

    public void checkController() {
//        if (Game.playerController.controllerMap.containsKey(id)) {
//            Controller controller = Game.playerController.controllerMap.get(id);
//            if (controller.getAxis(0) > 0.5f) {
//                keys.press(Keys.RIGHT);
//            } else {
//                keys.release(Keys.RIGHT);
//            }
//
//            if (controller.getAxis(2) > 0.5f) {
//                keys.press(Keys.LEFT);
//            } else {
//                keys.release(Keys.LEFT);
//            }
//        }
    }

    public boolean ownsAttackType(Class c) {
        for (Attack attack : battle.getAttacks()) {
            if (attack.type.getClass().isAssignableFrom(c)) return true;
        }
        return false;
    }

    @Override
    public void preUpdate() {
        checkController();

        if (useHealth && health <= 0) {
            kill(1);
        }

        update();
        postUpdate();
    }

    public void physicsUpdate(float physicsScalar) {
        if (stunned <= 0) move(velocity, physicsScalar);
    }

    public void applyForce(Vector2 force) {
        // F = m * a, so a = F / m
        velocity.add(force.cpy().scl(1f / fighter.mass));
        knockback.add(force.cpy().scl(1f / fighter.mass));
    }

    public void syncHitbox() {
        Vector2 center = getCenter();
        hitbox = new Rectangle(hitbox.x, hitbox.y, fighter.hitboxWidth, fighter.hitboxHeight);
        hitbox.setCenter(center);
    }

    public void syncDrawRect() {
        drawRect.setSize(fighter.renderWidth, fighter.renderHeight);
    }

    @Override
    public void update() {
        syncHitbox();

        usedCharge = 0;

        if (lives == 0) return;

        if (iFrames > 0) {
            iFrames--;
            invulnerable = true;
            if (iFrames == 0) {
                invulnerable = false;
            }
        }

        if (respawnTime > 0) {
            respawnTime--;
            invulnerable = true;
            float respawnX = id == 0 ? battle.getStage().player1SpawnX : battle.getStage().player2SpawnX;
            float respawnY = id == 0 ? battle.getStage().player1SpawnY : battle.getStage().player2SpawnY;
            hitbox.setCenter(new Vector2(respawnX,
                    respawnY + (battle.getStage().descendingRespawnPlatforms ? Math.max(respawnTime * 2, 120) - 280 + hitbox.height / 2 : 0)));
            frame = 0;
            if ((keys.isPressed(Keys.RIGHT) || keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.DOWN) || keys.isPressed(Keys.UP)) && respawnTime < 100 || respawnTime == 0) {
                respawnTime = 0;
                iFrames = 120;
            } else {
                return;
            }
        }

        if (ai != null && !(ai instanceof ReplayAI)) {
            ai.update();
            ai.updateKeys();
        }

        if (stunned > 0) {
            if (hasAction()) resetAction();
            stunned--;
            return;
        }

        if (frozen > 0) {
            frozen--;
        }

        Ledge ledge = null;
        if (ledgeCooldown <= 0 && ledgeGrabs > 0) ledge = battle.getStage().grabLedge(this);
        if (ledge != null) {
            if (currentAction != null) {
                currentAction = null;
            }
        } else {
            ledge = null;
        }

        ledgeGrabbing = ledge != null;

        if (knockbackDuration > 0) {
                if (currentAction != null && currentAction.isImportant() && currentAction.hasUsedAttack()) {
                knockbackDuration = 0;
                knockback = new Vector2();
            } else {
                if (currentAction != null) currentAction = null;

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
            }
        } else {
            knockback = new Vector2(0, 0);
        }

        if (frozen > 0) {
            return;
        }
        
        if (ledgeCooldown > 0) {
            ledgeCooldown--;
            ledge = null;
            ledgeGrabbing = false;
        }

        if (ledgeGrabbing) {
            resetAction();
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
                PlayerAction previousAction = currentAction;
                currentAction = null;
                if (charging) fighter.chargeAttack(this, charge);
                usedCharge = charge;
                charge = 0;
                minCharge = 0;
                charging = false;
                if (repeatAttack) {
                    tryAttacks();
                }
                fighter.onEndAction(previousAction, this);
            }
            gravityAndFriction();
            return;
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
                SabSounds.playSound("jump.mp3");
                fighter.onJump(this, false);
            } else if (extraJumpsUsed < fighter.airJumps && velocity.y < getJumpVelocity() * fighter.doubleJumpMultiplier) {
                velocity.y = getJumpVelocity() * fighter.doubleJumpMultiplier;
                SabSounds.playSound("double_jump.mp3");
                extraJumpsUsed++;
                battle.addParticle(new Particle(getCenter().sub(0, hitbox.height / 2), new Vector2(), 56, 16, 3, 3, direction, "double_jump.png"));
                fighter.onJump(this, true);
            }
        }

        if (!fighter.useWalkAnimationInAir && touchingStage || fighter.useWalkAnimationInAir) {
            if (keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) {
                frame = fighter.walkAnimation.stepLooping();
            } else {
                frame = fighter.idleAnimation.stepLooping();
                fighter.walkAnimation.reset();
            }
        }
        // Player actions
        if (occupied <= 0) {
            // Attacks
            tryAttacks();

            // Parrying
            if (keys.isJustPressed(Keys.PARRY)) {
                if (touchingStage) {
                    if (parryTime <= -20) {
                        fighter.onParry(this);
                        parryTime = 20;
                        fighter.parryAnimation.reset();
                        startAnimation(1, fighter.parryAnimation, 30, false);
                    }
                } else if (!usedAirDodge) {
                    if (iFrames < 15) iFrames = 15;
                    if (keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) {
                        airDodging = 10;
                        airDodge = new Vector2(fighter.airDodgeSpeed * direction * 0.7f, keys.isPressed(Keys.DOWN) ? -3 : keys.isPressed(Keys.UP) ? 4 : 1);
                    } else {
                        velocity.scl(0);
                    }
                    usedAirDodge = true;
                }
            }
        }

        if (occupied > 0) {
            occupied--;
        }

        if (usedRecovery) frame = fighter.freefallAnimation.stepLooping();

        gravityAndFriction();
    }

    public void tryAttacks() {
        if (keys.isJustPressed(Keys.ATTACK) || (repeatAttack && keys.isPressed(Keys.ATTACK))) {
            if (keys.isPressed(Keys.DOWN)) {
                fighter.downAttack(this);
            } else if (keys.isPressed(Keys.UP)) {
                fighter.upAttack(this);
            } else if (keys.isPressed(Keys.LEFT) || keys.isPressed(Keys.RIGHT)) {
                if (hasItem()) fighter.useItem(this);
                else fighter.sideAttack(this);
            } else {
                if (hasItem()) fighter.useItem(this);
                else if (assCharged && !repeatAttack) {
                    if (fighter.finalAss(this)) assCharged = false;
                }
                else fighter.neutralAttack(this);
            }
        } else {
            repeatAttack = false;
        }
    }

    public int getRemainingJumps() {
        return fighter.airJumps - extraJumpsUsed;
    }

    // Returns true if the player has a condition that would lock them out of normal control, like knockback, ledge grabing, being frozen, etc
    public boolean isStuck() {
        return frozen() || takingKnockback() || grabbingLedge() || stunned() || respawning();
    }
    public boolean inFreeFall() {
        return usedRecovery;
    }

    // Returns true if the player is not stuck and is not performing an action
    public boolean isReady() {
        return !isStuck() && !hasAction();
    }

    public boolean isStationary() {
        return !(keys.isPressed(Keys.RIGHT) ^ keys.isPressed(Keys.LEFT)) && !keys.isPressed(Keys.UP) && touchingStage && isReady();
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

    public boolean isHidden() {
        return hide;
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

    public void resetStun() {
    }
    public void freeze(int ticks) {
        if (ticks > frozen) frozen = ticks;
        freezeFrame = frame;
    }

    public void pickupItem(Item item) {
        heldItem = item;
        heldItem.onPickup(this);
    }

    public void tossItem() {
        Item item = heldItem;
        heldItem = null;
        item.onToss(this);
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
        if (airDodging > 0) {
            frame = fighter.airDodgeAnimation.stepLooping();
            velocity = (airDodge);
            airDodging--;
        } else {
            velocity.y -= 0.96f;
            applyForce(velocity.cpy().scl(-fighter.friction));
            if (touchingStage && (!(keys.isPressed(Keys.LEFT) ^ keys.isPressed(Keys.RIGHT)) || charging)) {
                velocity.x *= surfaceFriction;
            }
        }
    }

    @Override
    public void postUpdate() {
        if (knockbackDuration > 0) frame = fighter.knockbackAnimation.stepLooping();
        else fighter.knockbackAnimation.reset();

        if (--parryTime > -20) {
            parryTime--;
        }

        if (iFrames <= 0 && lives > 0 && ((knockbackDuration > 0 && !hitbox.overlaps(battle.getStage().getSafeBlastZone())) || (hitbox.x + hitbox.width < battle.getStage().getUnsafeBlastZone().x || hitbox.x > battle.getStage().getUnsafeBlastZone().x + battle.getStage().getUnsafeBlastZone().width || hitbox.y + hitbox.height < battle.getStage().getUnsafeBlastZone().y))) {
            kill(1);
        }

        if (hasItem()) heldItem.updateHeld(this);
        fighter.update(this);
    }

    @Override
    public void lateUpdate() {
        touchingStage = false;
    }

    public void useItem() {
        heldItem.use(this);
    }

    public boolean hasItem() {
        return heldItem != null;
    }

    public Item getItem() {
        return heldItem;
    }

    public boolean takingKnockback() {
        return knockbackDuration > 0;
    }

    @Override
    public boolean canBeHit(DamageSource source) {
        if (source.parryable && parryTime > 0) {
            source.onParry();
            battle.onSuccessfulParry(source, this);
            gameStats.landedParry();
            if (source.owner != null) {
                source.owner.stun(20);
            }
            resetAction();
            fighter.onSuccessfulParry(this, source);
            parryTime = -20;
            setIFrames(10);
            battle.addParticle(new Particle(getCenter().add(4 * direction, hitbox.height / 3), new Vector2(1 * SabRandom.random(), 0).rotateDeg(SabRandom.random() * 360), 64, 64, 0, "twinkle.png"));
            return false;
        }
        return fighter.onHit(this, source) && !invulnerable;
    }

    public void setIFrames(int duration) {
        iFrames = duration;
    }

    public void grantFinalAss() {
        assCharged = true;
    }

    public void revokeFinalAss() {
        assCharged = false;
    }

    public boolean isAssCharged() {
        return assCharged;
    }

    @Override
    public boolean onHit(DamageSource source) {
        if (!battle.preOnPlayerHit(source, this)) {
            return false;
        }
        int statusBefore;
        if (useHealth) {
            statusBefore = health;
            health -= source.damage;
        } else {
            statusBefore = damage;
            damage += source.damage;
        }

        battle.shakeCamera(3);
        battle.freezeFrame((int) Math.round((source.damage - 10) / 30f), 2, 1, false);

        SabSounds.playSound("hit.mp3");
        Vector2 newKnockback = source.knockback.cpy();
        if (!source.staticKnockback) {
            newKnockback.scl(3.25f).scl(damage / 100f + 1f);
        }
        if (newKnockback.len() > knockback.len()) {
            smokeGenerator = 0;
            knockback.set(newKnockback);
        } else {
            knockback.setAngleRad(newKnockback.angleRad());
        }

        if (source.getCustomHitLag() < 0) knockbackDuration = (int) (knockback.len() * 0.225f);
        else knockbackDuration = source.getCustomHitLag();

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
            }
        }

        if (knockback.len() > 4) {
            velocity = new Vector2();
        }

        battle.onPlayerHit(source, this);
        battle.getStage().onPlayerHit(this, source, shouldDie);

        if (useHealth) {
            gameStats.tookDamage(statusBefore - health);
        } else {
            gameStats.tookDamage(damage - statusBefore);
            if (damage > 1000) damage = 1000;
        }

        return true;
    }

    // Called when one of this player's attacks hits a gameObject
    public void hitObject(Attack attack, GameObject hit) {
        fighter.hitObject(this, attack, hit);
    }

    public PlayerAction getCurrentAction() {
        return currentAction;
    }

    @Override
    public void render(Seagraphics g) {
        syncDrawRect();
        if (!hide) {
            if (fighter.preRender(this, g)) {
                drawRect.setCenter(getCenter().add(fighter.imageOffsetX * direction, fighter.imageOffsetY).add(drawRectOffset));
                if (frozen > 0) {
                    frame = freezeFrame;
                }
                preRender(g);
                if (assCharged) {
                    g.usefulDraw(g.imageProvider.getImage("glow.png"), drawRect.x - 16, drawRect.y - 16, (int) drawRect.width + 32, (int) drawRect.height + 32, 0, 1, 0, false, false);
                    drawRect.x += SabRandom.random(-4f, 4f);
                    drawRect.y += SabRandom.random(-4f, 4f);
                }
                fighter.render(this, g);
                drawRect.setCenter(getCenter().add(fighter.imageOffsetX * direction, fighter.imageOffsetY).add(drawRectOffset));
                fighter.render(this, g);
                if (hasItem()) {
                    heldItem.renderHeld(this, g);
                }
                postRender(g);

                if (frozen > 0) {
                    g.usefulDraw(g.imageProvider.getImage("ice.png"), drawRect.x - 4, drawRect.y - 4, (int) drawRect.width + 8, (int) drawRect.height + 8, 0, 1, rotation, false, false);
                }
                if (respawnTime > 0) {
                    g.usefulDraw(g.imageProvider.getImage("p" + (id + 1) + "_spawn_platform.png"), drawRect.getCenter(new Vector2()).x - 40, hitbox.y - 32, 80, 32, 0, 1, rotation, false, false);
                }
            }
            if (Settings.localSettings.drawPlayerArrows.value) {
                Vector2 arrowPosition = getCenter().add(-8, drawRect.height / 2 + 4);
                g.usefulDraw(g.imageProvider.getImage("player_arrows.png"), arrowPosition.x, arrowPosition.y, 16, 8, id == -1 ? 2 : id,3, 0, false, false);
            }
        }
    }

    public Animation getAnimation() {
        return currentAction == null ? null : currentAction .getAnimation();
    }

    public int getOccupiedTicks() {
        return occupied;
    }

    public int getIFrames() {
        return iFrames;
    }

    // Sets the number of ticks the player will be "occupied," meaning they are able to move but unable to attack.
    public void occupy(int ticks) {
        this.occupied = ticks;
    }

    public int getId() {
        return id;
    }

    public String getImageName() {
        return fighter.id + (costume == 0 ? "" : ("_alt_" + costume)) + ".png";
    }

    public String getRenderName() {
        return fighter.id + "_render" + (costume == 0 ? "" : ("_alt_" + costume)) + ".png";
    }

    public void renderUI(Seagraphics g) {
        if (id == 0) {
            g.scalableDraw(g.imageProvider.getImage("in_battle_hud_p1.png"), -256, -256 - 64, 128, 128);

            for (int i = 0; i < lives; i++) {
                g.scalableDraw(g.imageProvider.getImage("life_p1.png"), -256 + 48 + 24 * i, -256 - 12, 20, 20);
            }

            if (useHealth) {
                g.drawText(health + "hp", Game.getDefaultFont(), -256 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);
            } else {
                g.drawText(damage + "%", Game.getDefaultFont(), -256 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);
            }
        } else if (id == 1) {
            g.scalableDraw(g.imageProvider.getImage("in_battle_hud_p2.png"), 256 - 128, -256 - 64, 128, 128);

            for (int i = 0; i < lives; i++) {
                g.scalableDraw(g.imageProvider.getImage("life_p2.png"), 256 - 128 + 48 + 24 * i, -256 - 12, 20, 20);
            }

            if (useHealth) {
                g.drawText(health + "hp", Game.getDefaultFont(), 256 - 128 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);
            } else {
                g.drawText(damage + "%", Game.getDefaultFont(), 256 - 128 + 116, -256 + 48, Game.getDefaultFontScale(), Color.WHITE, 1);
            }
        }

        fighter.renderUI(this, g);
    }
}