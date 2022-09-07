package sab.game;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class CollisionResolver {
    public static Direction moveWithCollisions(GameObject a, Vector2 velocity, GameObject b) {
        return moveWithCollisions(a, velocity, List.of(b));
    }

    public static Direction moveWithCollisions(GameObject gameObject, Vector2 velocity, List<GameObject> colliders) {
        Rectangle a = gameObject.hitbox;

        Direction collisionDirection = Direction.NONE;

        List<GameObject> movingColliders = new ArrayList<>();

        a.x += velocity.x;
        for (GameObject collider : colliders) {
            Direction tryDirection = resolveX(gameObject, velocity, collider.hitbox);
            if (tryDirection != Direction.NONE) collisionDirection = tryDirection;
            if (collider.velocity.x != 0 && collider.velocity.y != 0) movingColliders.add(collider);
        }

        a.y += velocity.y;
        for (GameObject collider : colliders) {
            Direction tryDirection = resolveY(gameObject, velocity, collider.hitbox);
            if (tryDirection != Direction.NONE) collisionDirection = tryDirection;
        }

        for (GameObject collider : movingColliders) {
            if (resolveX(gameObject, collider.velocity.cpy().scl(-1), new Rectangle(collider.hitbox.x + collider.velocity.x, collider.hitbox.y, collider.hitbox.width, collider.hitbox.height)) != Direction.NONE) gameObject.hitbox.x += collider.velocity.x;
        }

        for (GameObject collider : movingColliders) {
            Direction tryDirection = resolveY(gameObject, collider.velocity.cpy().scl(-1), new Rectangle(collider.hitbox.x, collider.hitbox.y + collider.velocity.y, collider.hitbox.width, collider.hitbox.height));
            if (tryDirection != Direction.NONE) {
                gameObject.hitbox.y += collider.velocity.y;
                collisionDirection = tryDirection;
            }
        }

        return collisionDirection;
    }

    public static Direction resolveX(GameObject gameObject, Vector2 velocity, Rectangle b) {
        Direction collisionDirection = Direction.NONE;
        Rectangle a = gameObject.hitbox;

        if (a.overlaps(b)) {
            if (velocity.x > 0) {
                a.x = b.x - a.width;
                collisionDirection = Direction.RIGHT;
            } else if (velocity.x < 0) {
                a.x = b.x + b.width;
                collisionDirection = Direction.LEFT;
            }
        }

        return collisionDirection;
    }

    public static Direction resolveY(GameObject gameObject, Vector2 velocity, Rectangle b) {
        Direction collisionDirection = Direction.NONE;
        Rectangle a = gameObject.hitbox;

        if (a.overlaps(b)) {
            if (velocity.y > 0) {
                a.y = b.y - a.height;
                collisionDirection = Direction.UP;
            } else if (velocity.y <= 0) {
                a.y = b.y + b.height;
                collisionDirection = Direction.DOWN;
            }
        }

        return collisionDirection;
    }
}