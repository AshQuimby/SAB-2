package sab.game.stage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Slope {
    public final Rectangle bounds;
    public final Vector2 start;
    public final Vector2 end;
    public final int outerDirection;

    public Slope(float x1, float y1, float x2, float y2, int outerDirection) {
        start = new Vector2(x1, y1);
        end = new Vector2(x2, y2);

        // Ensure start -> end always moves in the +x direction
        if (end.x < start.x) {
            Vector2 temp = start.cpy();
            start.set(end);
            end.set(temp);
        }

        bounds = new Rectangle(start.x, Math.min(start.y, end.y), end.x - start.x, Math.max(start.y - end.y, end.y - start.y));
        this.outerDirection = outerDirection;
    }

    public Slope(Vector2 start, Vector2 end, int outerDirection) {
        this(start.x, start.y, end.x, end.y, outerDirection);
    }

    public float getSlope() {
        return (end.y - start.y) / (end.x - start.x);
    }

    public float getXIntersection(float y) {
        float m = getSlope();
        float b = start.y - start.x * m;
        return (y - b) / m;
    }

    public float getYIntersection(float x) {
        float m = getSlope();
        float b = start.y - start.x * m;
        return m * x + b;
    }
}
