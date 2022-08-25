package sab.game;

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

        a.x += velocity.x;
        for (GameObject collider : colliders) {
            Rectangle b = collider.hitbox;

            if (a.overlaps(b)) {
                if (velocity.x > 0) {
                    a.x = b.x - a.width;
                    collisionDirection = Direction.RIGHT;
                } else if (velocity.x < 0) {
                    a.x = b.x + b.width;
                    collisionDirection = Direction.LEFT;
                }
            }
        }

        a.y += velocity.y;
        for (GameObject collider : colliders) {
            Rectangle b = collider.hitbox;

            if (a.overlaps(b)) {
                if (velocity.y > 0) {
                    a.y = b.y - a.height;
                    collisionDirection = Direction.UP;
                } else if (velocity.y < 0) {
                    a.y = b.y + b.height;
                    collisionDirection = Direction.DOWN;
                }
            }
        }

        return collisionDirection;
    }
}