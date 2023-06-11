package sab.game.attack;

import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.*;
import sab.game.stage.Stage;
import sab.util.Utils;

public class Attack extends DamageSource {
    public sab.game.attack.AttackType type;
    public boolean alive;
    public boolean canHit;
    public int life;
    public int hitCooldown;
    public boolean directional;
    public boolean collideWithStage;
    public Direction collisionDirection;
    public final Player originalOwner;
    public boolean basedOffCostume;
    public int updatesPerTick;
    private final HashMap<GameObject, Integer> hitObjects;

    private Attack(Rectangle hitbox, Rectangle drawRect, String imageName, Vector2 velocity, AttackType type, boolean canHit, int life, int hitCooldown, boolean collideWithStage, int updatesPerTick, Player owner) {
        hitObjects = new HashMap<>();
        this.hitbox = new Rectangle(hitbox);
        this.drawRect = new Rectangle(drawRect);
        this.imageName = imageName;
        this.velocity = velocity.cpy();
        this.type = type;
        this.canHit = canHit;
        this.life = life;
        this.hitCooldown = hitCooldown;
        this.collideWithStage = collideWithStage;
        this.updatesPerTick = updatesPerTick;
        this.owner = owner;
        this.originalOwner = owner;
    }

    public Attack(AttackType type, Player player) {
        alive = false;
        life = 120;
        hitbox = new Rectangle();
        hitbox.setPosition(new Vector2());
        previousPosition = new Vector2();
        drawRect = new Rectangle();
        drawRect.setPosition(hitbox.getPosition(new Vector2()));
        direction = 1;
        hitObjects = new HashMap<>();
        velocity = new Vector2();
        owner = player;
        this.originalOwner = player;
        knockback = new Vector2();
        reflectable = true;
        parryable = true;
        updatesPerTick = 1;
        canHit = true;
        this.type = type;
        type.setDefaults(this);
        if (basedOffCostume) {
            if (owner.costume > 0) {
                imageName = Utils.applyCostumeToFilename(imageName, owner.costume, "png");
            }
        }
    }

    public void move(Vector2 v) {
        Vector2 movement = v.cpy();

        while (movement.len() > 0) {
            Vector2 step = movement.cpy().limit(1);
            movement.sub(step);
            collisionDirection = CollisionResolver.moveWithCollisions(this, step, owner.battle.getSolidStageObjects());

            if (velocity.y <= 0) {
                Direction tryDirection;
                for (GameObject gameObject : owner.battle.getPassablePlatforms()) {
                    if (previousPosition.y > gameObject.hitbox.y + gameObject.hitbox.height) {
                        tryDirection = CollisionResolver.resolveY(this, step.y, gameObject.hitbox);
                        if (tryDirection != Direction.NONE) collisionDirection = tryDirection;
                    }
                }
            }
        }
    }

    public void onSpawn(int[] data) {
        alive = true;
        if (directional && owner != null) {
            direction = owner.direction;
        }
        type.onSpawn(this, data);
    }

    @Override
    public void preUpdate() {
        previousPosition = new Vector2(hitbox.x, hitbox.y);
        for (int i = 0; i < updatesPerTick; i++) {
            if (collideWithStage) {
                move(velocity);
            } else {
                hitbox.x += velocity.x;
                hitbox.y += velocity.y;
            }

            drawRect.setCenter(hitbox.getCenter(new Vector2()));
            if (!alive) break;
            update();
        }
        if (--life == 0) {
            alive = false;
        }
        postUpdate();
    }

    @Override
    public void update() {
        super.update();
        type.update(this);

        for (GameObject key : hitObjects.keySet()) {
            if (hitObjects.get(key) > 0) hitObjects.replace(key, hitObjects.get(key) - 1);
        }

        if (canHit) {
            for (GameObject target : owner.battle.getHittableGameObjects()) {
                attemptHit(target, (Hittable) target);
            }
        }

        if (directional) {
            if (velocity.x > 0) {
                direction = 1;
            } else {
                direction = -1;
            }
        }
    }

    public void attemptHit(GameObject target, Hittable hit) {
        if (canHit) {
            if (target != owner && type.canHit(this, target)) {
                if ((hitObjects.get(target) == null || hitObjects.get(target) == 0)) {
                    boolean canBeHit = hit.canBeHit(this);
                    if (canBeHit) successfulHit(target);
                    type.hit(this, target);
                    if (canBeHit) hit.onHit(this);
                }
            }
        }
    }

    // Similar to attemptHit() but will ignore if hitboxes overlap, if the target is the owner of this attack, if the attack is on hitcooldown for the target, or if the attack's canHit field is true
    public void forceAttemptHit(GameObject target, Hittable hit) {
        boolean canBeHit = hit.canBeHit(this);
        if (canBeHit) successfulHit(target);
        type.hit(this, target);
        if (canBeHit) hit.onHit(this);
    }

    public void clearHitObject(GameObject gameObject) {
        hitObjects.remove(gameObject);
    }

    public void clearHitObjects() {
        hitObjects.clear();
    }

    public void successfulHit(GameObject hit) {
        if (hitObjects.containsKey(hit))
            hitObjects.replace(hit, hitCooldown);
        else 
           hitObjects.put(hit, hitCooldown);

        owner.hitObject(this, hit);
        owner.gameStats.dealtDamage(damage);

        type.successfulHit(this, hit);
    }

    public void kill() {
        type.onKill(this);
    }

    @Override
    public void render(Seagraphics g) {
        type.render(this, g);
    }

    public void lateRender(Seagraphics g) {
        type.lateRender(this, g);
    }

    public Battle getBattle() {
        return owner.battle;
    }

    public Stage getStage() {
        return owner.battle.getStage();
    }

    // Finds the nearest opponent player in range of maxDistance, set maxDistance to less than 0 for infinite tracking. Returns null when there is not an eligible target
    public Player getNearestOpponent(float maxDistance) {
        float bestDistance = maxDistance;
        Player bestTarget = null;
        for (Player player : owner.battle.getPlayers()) {
            if (player != owner) {
                float distance = player.hitbox.getCenter(new Vector2()).dst(hitbox.getCenter(new Vector2()));
                if (distance <= bestDistance || bestDistance < 0) {
                    bestTarget = player;
                    bestDistance = distance;
                }
            }
        }
        return bestTarget;
    }

    public Attack copy() {
        return new Attack(hitbox, drawRect, imageName, velocity, type, canHit, life, hitCooldown, collideWithStage, updatesPerTick, owner);
    }

    public void onParry() {
        type.onParry(this);
    }
}