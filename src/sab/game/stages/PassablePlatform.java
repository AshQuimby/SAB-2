package sab.game.stages;

public class PassablePlatform extends Platform {
    
    public PassablePlatform(float x, float y, float width, float height, String imageName, Stage stage) {
        super(x, y, width, height, imageName, stage);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        frameCount = 1;
    }

    public PassablePlatform(float x, float y, float width, float height, String imageName, StageObjectBehaviour behavior, Stage stage) {
        super(x, y, width, height, imageName, stage);
        this.behavior = behavior;
        updates = true;
        hitbox = drawRect;
        frame = 0;
        frameCount = 1;
    }

    public PassablePlatform(float x, float y, float width, float height, int frameCount, String imageName, Stage stage) {
        super(x, y, width, height, imageName, stage);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        this.frameCount = frameCount;
    }

    public PassablePlatform(float x, float y, float width, float height, int frameCount, String imageName, StageObjectBehaviour behavior, Stage stage) {
        super(x, y, width, height, imageName, stage);
        this.behavior = behavior;
        updates = true;
        hitbox = drawRect;
        frame = 0;
        this.frameCount = frameCount;
    }

    public boolean isSolid() {
        return false;
    }
}
