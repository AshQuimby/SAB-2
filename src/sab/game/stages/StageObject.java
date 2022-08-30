package sab.game.stages;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

public class StageObject extends GameObject {
    public StageObject(float x, float y, float width, float height, String imageName) {
        velocity = new Vector2();
        this.imageName = imageName;
        drawRect = new Rectangle(x, y, width, height);
        hitbox = new Rectangle(drawRect);
    }

    public boolean inBackground() {
        return true;
    }

    public boolean isSolid() {
        return false;
    }
}
