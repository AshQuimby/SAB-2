package sab.game.stage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Battle;

public class StageObject extends GameObject {
    protected sab.game.stage.StageObjectBehaviour behavior;
    protected boolean updates;
    public sab.game.stage.Stage stage;
    public boolean alive;
    public float friction;

    public StageObject(float x, float y, float width, float height, String imageName, sab.game.stage.Stage stage) {
        velocity = new Vector2();
        behavior = null;
        updates = false;
        this.imageName = imageName;
        drawRect = new Rectangle(x, y, width, height);
        hitbox = new Rectangle(drawRect);
        this.stage = stage;
        friction = 0.3f;
        alive = true;
    }

    public StageObject(float x, float y, float width, float height, String imageName, Stage stage, sab.game.stage.StageObjectBehaviour behavior) {
        this.behavior = behavior;
        updates = behavior != null;
        velocity = new Vector2();
        this.imageName = imageName;
        drawRect = new Rectangle(x, y, width, height);
        hitbox = new Rectangle(drawRect);
        this.stage = stage;
        alive = true;
    }

    public void addBehavior(StageObjectBehaviour behavior) {
        this.behavior = behavior;
        updates = behavior != null;        
    }

    public void updateStageObject(Battle battle) {
        if (!updates) return;
        behavior.update(this, battle);
    }
    
    public void kill() {
        alive = false;
    }

    public boolean inBackground() {
        return true;
    }

    public boolean isSolid() {
        return false;
    }
}
