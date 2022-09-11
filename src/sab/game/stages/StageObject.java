package sab.game.stages;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;

import sab.game.Battle;

public class StageObject extends GameObject {
    protected StageObjectBehaviour behavior;
    protected boolean updates;
    public Stage stage;

    public StageObject(float x, float y, float width, float height, String imageName, Stage stage) {
        velocity = new Vector2();
        behavior = null;
        updates = false;
        this.imageName = imageName;
        drawRect = new Rectangle(x, y, width, height);
        hitbox = new Rectangle(drawRect);
        this.stage = stage;
    }

    public StageObject(float x, float y, float width, float height, String imageName, Stage stage, StageObjectBehaviour behavior) {
        this.behavior = behavior;
        updates = behavior != null;
        velocity = new Vector2();
        this.imageName = imageName;
        drawRect = new Rectangle(x, y, width, height);
        hitbox = new Rectangle(drawRect);
        this.stage = stage;
    }

    public void addBehavior(StageObjectBehaviour behavior) {
        this.behavior = behavior;
        updates = behavior != null;        
    }

    public void updateStageObject(Battle battle) {
        if (!updates) return;
        behavior.update(this, battle);
    }

    public boolean inBackground() {
        return true;
    }

    public boolean isSolid() {
        return false;
    }
}
