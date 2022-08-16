package sab.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class CollisionResolver {
    public static boolean resolve(Rectangle a, Vector2 velocity, Rectangle b) {
        if (!a.overlaps(b)) return false;

        a.x -= velocity.x;

        if (a.overlaps(b)) {
            if (velocity.y > 0) {
                a.y = b.y - a.height;
            } else {
                a.y = b.y + b.height;
            }
        }

        a.x += velocity.x;

        if (a.overlaps(b)) {
            if (velocity.x > 0) {
                a.x = b.x - a.width;
            } else {
                a.x = b.x + b.width;
            }
            
        }

        return true;
    }

    public static Direction movingResolve(GameObject a, GameObject b) {
        if (!a.hitbox.overlaps(b.hitbox)) return Direction.NONE;

        Direction direction = Direction.NONE;

        Vector2 collisionVelocity = new Vector2(a.velocity.x - b.velocity.x, a.velocity.y - b.velocity.y);

        a.hitbox.y += b.velocity.y;
        a.hitbox.x -= a.velocity.x;

        if (a.hitbox.overlaps(b.hitbox)) {
            if (collisionVelocity.y > 0) {
                direction = Direction.DOWN;
                a.hitbox.y = b.hitbox.y - a.hitbox.height;
            } else {
                direction = Direction.UP;
                a.hitbox.y = b.hitbox.y + b.hitbox.height;
            }
        }

        a.hitbox.x += a.velocity.x;
        a.hitbox.x += b.velocity.x;

        if (a.hitbox.overlaps(b.hitbox)) {
            if (collisionVelocity.x > 0) {
                direction = Direction.RIGHT;
                a.hitbox.x = b.hitbox.x - a.hitbox.width;
            } else {
                direction = Direction.LEFT;
                a.hitbox.x = b.hitbox.x + b.hitbox.width;
            }
            
        }


        resolve(a.hitbox, new Vector2(-b.velocity.x, -b.velocity.y), b.hitbox);

        return direction;
    }
}