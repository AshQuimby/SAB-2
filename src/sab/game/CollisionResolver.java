package sab.game;

import java.util.List;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class CollisionResolver {
    public static Direction moveWithCollisions(GameObject a, Vector2 velocity, GameObject b) {
        return moveWithCollisions(a, velocity, List.of(b));
    }

    public static Direction moveWithCollisions(GameObject gameObject, Vector2 velocity, List<? extends GameObject> colliders) {
        Rectangle a = gameObject.hitbox;

        Direction collisionDirection = Direction.NONE;

        a.x += velocity.x;
        for (GameObject collider : colliders) {
            Direction tryDirection = resolveX(gameObject, velocity.x, collider.hitbox);
            if (tryDirection != Direction.NONE) {
                collisionDirection = tryDirection;
            }
        }

        a.y += velocity.y;
        for (GameObject collider : colliders) {
            Direction tryDirection = resolveY(gameObject, velocity.y, collider.hitbox);
            if (tryDirection != Direction.NONE) {
                collisionDirection = tryDirection;
            }
        }

        return collisionDirection;
    }

    public static Direction resolveX(GameObject gameObject, float dX, Rectangle b) {
        Direction collisionDirection = Direction.NONE;
        Rectangle a = gameObject.hitbox;

        if (a.overlaps(b)) {
            if (dX > 0) {
                a.x = b.x - a.width;
                collisionDirection = Direction.RIGHT;
            } else if (dX < 0) {
                a.x = b.x + b.width;
                collisionDirection = Direction.LEFT;
            }
        }

        return collisionDirection;
    }

    public static Direction resolveY(GameObject gameObject, float dY, Rectangle b) {
        Direction collisionDirection = Direction.NONE;
        Rectangle a = gameObject.hitbox;

        if (a.overlaps(b)) {
            if (dY > 0) {
                a.y = b.y - a.height;
                collisionDirection = Direction.UP;
            } else if (dY <= 0) {
                a.y = b.y + b.height;
                collisionDirection = Direction.DOWN;
            }
        }

        return collisionDirection;
    }
}