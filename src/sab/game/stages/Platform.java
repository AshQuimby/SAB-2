package sab.game.stages;

import com.badlogic.gdx.math.Vector2;

import sab.game.Battle;
import sab.game.CollisionResolver;
import sab.game.Player;

public class Platform extends StageObject {
    protected boolean updates;

    public Platform(float x, float y, float width, float height, String imageName, Stage stage) {
        super(x, y, width, height, imageName, stage, null);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        frameCount = 1;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, String imageName, StageObjectBehaviour behavior, Stage stage) {
        super(x, y, width, height, imageName, stage, behavior);
        updates = true;
        hitbox = drawRect;
        frame = 0;
        frameCount = 1;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, int frameCount, String imageName, Stage stage) {
        super(x, y, width, height, imageName, stage, null);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        this.frameCount = frameCount;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, int frameCount, String imageName, StageObjectBehaviour behavior, Stage stage) {
        super(x, y, width, height, imageName, stage, behavior);
        updates = true;
        hitbox = drawRect;
        frame = 0;
        this.frameCount = frameCount;
        this.stage = stage;
    }

    @Override
    public void preUpdate() {
        update();
        postUpdate();
    }

    @Override
    public void updateStageObject(Battle battle) {

        hitbox.x += velocity.x;
        if (isSolid())for (Player player : battle.getPlayers()) {
            if (velocity.x > 0) {
                CollisionResolver.resolveX(player, 1, hitbox);
            } else if (velocity.x < 0) {
                CollisionResolver.resolveX(player, 1, hitbox);
            }
        }

        hitbox.y += velocity.y;
        if (isSolid())for (Player player : battle.getPlayers()) {
            if (velocity.y > 0) {
                CollisionResolver.resolveY(player, -1, hitbox);
            } else if (velocity.y < 0) {
                CollisionResolver.resolveY(player, 1, hitbox);
            }
        }
        drawRect.setCenter(hitbox.getCenter(new Vector2()));

        if (!updates) return;
        behavior.update(this, battle);
    }

    @Override
    public boolean inBackground() {
        return false;
    }

    @Override
    public boolean isSolid() {
        return true;
    }
}
