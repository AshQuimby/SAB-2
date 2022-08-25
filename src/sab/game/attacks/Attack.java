package sab.game.attacks;

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
    public AttackType type;
    public boolean alive;
    public int life;
    public int hitCooldown;
    public boolean directional;
    public boolean collideWithStage;
    public Direction collisionDirection;
    private HashMap<GameObject, Integer> hitObjects;

    public Attack(AttackType type, Player player) {
        alive = true;
        life = 120;
        hitbox = new Rectangle();
        hitbox.setPosition(new Vector2());
        drawRect = new Rectangle();
        drawRect.setPosition(hitbox.getPosition(new Vector2()));
        hitObjects = new HashMap<GameObject, Integer>();
        velocity = new Vector2();
        owner = player;
        knockback = new Vector2();
        reflectable = true;
        this.type = type;
        type.onCreate(this);
    }

    public void onSpawn(int[] data) {
        type.onSpawn(this, data);
    }
    
    @Override
    public void preUpdate() {
        if (collideWithStage) {
            collisionDirection = CollisionResolver.moveWithCollisions(this, velocity, owner.battle.getPlatforms());
        } else {
            hitbox.x += velocity.x;
            hitbox.y += velocity.y;
        }

        drawRect.setCenter(hitbox.getCenter(new Vector2()));
        update();
        postUpdate();
    }

    @Override
    public void update() {
        super.update();
        type.update(this);

        for (GameObject key : hitObjects.keySet()) {
            hitObjects.replace(key, hitObjects.get(key) - 1);
        }

        for (GameObject target : owner.battle.getHittableGameObjects()) {
            if (hitbox.overlaps(target.hitbox) && target != owner && (hitObjects.get(target) == null || hitObjects.get(target) <= 0)) {
                if (((Hittable) target).onHit(this)) successfulHit(target);
                hit(target);
            }
        }

        if (--life == 0) {
            alive = false;
        }

        if (directional) {
            if (velocity.x > 0) {
                direction = 1;
            } else {
                direction = -1;
            }
        }

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

        type.successfulHit(this, hit);
    }

    public void kill() {
        type.kill(this);
        owner.battle.removeGameObject(this);
    }

    @Override
    public void render(Seagraphics g) {
        type.render(this, g);
    }
}