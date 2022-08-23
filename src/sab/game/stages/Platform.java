package sab.game.stages;

public class Platform extends StageObject {
    private PlatformBehavior behavior;
    private boolean updates;

    public Platform(float x, float y, float width, float height, String imageName) {
        super(x, y, width, height, imageName);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        frameCount = 1;
    }

    public Platform(float x, float y, float width, float height, String imageName, PlatformBehavior behavior) {
        super(x, y, width, height, imageName);
        this.behavior = behavior;
        updates = true;
        hitbox = drawRect;
        frame = 0;
        frameCount = 1;
    }

    public void addBehavior(PlatformBehavior behavior) {
        this.behavior = behavior;
        if (behavior != null) updates = true; else updates = false;        
    }

    @Override
    public void update() {
        if (updates) behavior.update();
        postUpdate();
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
