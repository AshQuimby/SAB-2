package sab.game.stage;

import com.badlogic.gdx.math.Vector2;

import sab.game.Battle;
import sab.game.CollisionResolver;
import sab.game.Direction;
import sab.game.Player;
import sab.game.attack.Attack;

public class Platform extends StageObject {
    protected boolean updates;

    public Platform(float x, float y, float width, float height, String imageName, sab.game.stage.Stage stage) {
        super(x, y, width, height, imageName, stage, null);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        frameCount = 1;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, String imageName, sab.game.stage.Stage stage, sab.game.stage.StageObjectBehaviour behavior) {
        super(x, y, width, height, imageName, stage, behavior);
        updates = true;
        hitbox = drawRect;
        frame = 0;
        frameCount = 1;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, int frameCount, String imageName, sab.game.stage.Stage stage) {
        super(x, y, width, height, imageName, stage, null);
        hitbox = drawRect;
        updates = false;
        frame = 0;
        this.frameCount = frameCount;
        this.stage = stage;
    }

    public Platform(float x, float y, float width, float height, int frameCount, String imageName, Stage stage, StageObjectBehaviour behavior) {
        super(x, y, width, height, imageName, stage, behavior);
        updates = true;
        hitbox = drawRect;
        frame = 0;
        this.frameCount = frameCount;
        this.stage = stage;
    }

    public void createLedges(float yOffset, float width, float height, Stage stage) {
        stage.addLedge(new Ledge(this, new Vector2(-hitbox.width / 2 - width, -yOffset), width, height, 1));
        stage.addLedge(new Ledge(this, new Vector2(hitbox.width / 2, -yOffset), width, height, -1));
    }

    public void createLedges(Stage stage) {
        createLedges(hitbox.height / 2 + 12, 16, hitbox.height, stage);
    }

    @Override
    public void preUpdate() {
        update();
        postUpdate();
    }

    @Override
    public void updateStageObject(Battle battle) {

        if (isSolid()) {
            hitbox.x += velocity.x;
            for (Player player : battle.getPlayers()) {
                if (CollisionResolver.resolveX(player, -velocity.x, this.hitbox) != Direction.NONE) {
                    player.move(new Vector2(velocity.x, 0));
                }
            }
            for (Attack attack : battle.getAttacks()) {
                if (attack.collideWithStage && CollisionResolver.resolveX(attack, -velocity.x, this.hitbox) != Direction.NONE) {
                }
            }

            hitbox.y += velocity.y;
            for (Player player : battle.getPlayers()) {
                if (CollisionResolver.resolveY(player, -velocity.y, this.hitbox) != Direction.NONE) {
                    player.move(new Vector2(0, velocity.y));
                }
            }
            for (Attack attack : battle.getAttacks()) {
                if (attack.collideWithStage && CollisionResolver.resolveY(attack, -velocity.y, this.hitbox) != Direction.NONE) {
                }
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
