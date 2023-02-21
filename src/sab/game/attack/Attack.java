package sab.game.attack;

import java.awt.*;
import java.util.HashMap;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

import sab.game.DamageSource;
import sab.game.Direction;
import sab.game.Hittable;
import sab.game.Player;
import sab.game.CollisionResolver;

public class Attack extends DamageSource {
    public sab.game.attack.AttackType type;
    public boolean alive;
    public boolean canHit;
    public int life;
    public int hitCooldown;
    public boolean directional;
    public boolean collideWithStage;
    public Direction collisionDirection;
    public int updatesPerTick;
    private final HashMap<GameObject, Integer> hitObjects;

    public Attack(AttackType type, Player player) {
        alive = true;
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
        knockback = new Vector2();
        reflectable = true;
        updatesPerTick = 1;
        canHit = true;
        this.type = type;
        type.setDefaults(this);
    }

    private void move(Vector2 v) {
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
        if (directional) {
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
            hitObjects.replace(key, hitObjects.get(key) - 1);
        }

        if (canHit) {
            for (GameObject target : owner.battle.getHittableGameObjects()) {
                if (hitbox.overlaps(target.hitbox) && target != owner && (hitObjects.get(target) == null || hitObjects.get(target) <= 0)) {
                    if (((Hittable) target).canBeHit(this)) successfulHit(target);
                    hit(target);
                    if (((Hittable) target).canBeHit(this)) ((Hittable) target).onHit(this);
                }
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

    public void postUpdate() {
        if (!alive) {
            kill();
        }
    }

    public void clearHitObject(GameObject gameObject) {
        hitObjects.remove(gameObject);
    }

    public void clearHitObjects() {
        hitObjects.clear();
    }

    public void hit(GameObject hit) {
        type.hit(this, hit);
    }

    public void successfulHit(GameObject hit) {
        if (hitObjects.containsKey(hit))
            hitObjects.replace(hit, hitCooldown);
        else 
           hitObjects.put(hit, hitCooldown);
        
        owner.gameStats.dealtDamage(damage);

        type.successfulHit(this, hit);
    }

    public void kill() {
        type.onKill(this);
        if (!alive) owner.battle.removeGameObject(this);
    }

    @Override
    public void render(Seagraphics g) {
        type.render(this, g);
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
}