package sab.game.stages;

public class PassablePlatform extends Platform {
    
    public PassablePlatform(float x, float y, float width, float height, String imageName) {
        super(x, y, width, height, imageName);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        frameCount = 1;
    }

    public PassablePlatform(float x, float y, float width, float height, String imageName, PlatformBehavior behavior) {
        super(x, y, width, height, imageName);
        this.behavior = behavior;
        updates = true;
        hitbox = drawRect;
        frame = 0;
        frameCount = 1;
    }

    public PassablePlatform(float x, float y, float width, float height, int frameCount, String imageName) {
        super(x, y, width, height, imageName);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        this.frameCount = frameCount;
    }

    public PassablePlatform(float x, float y, float width, float height, int frameCount, String imageName, PlatformBehavior behavior) {
        super(x, y, width, height, imageName);
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
