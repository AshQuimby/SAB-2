package sab.game.stage;

import com.badlogic.gdx.math.Vector2;

import sab.game.Battle;
import sab.game.CollisionResolver;
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

    @Override
    public void preUpdate() {
        update();
        postUpdate();
    }

    @Override
    public void updateStageObject(Battle battle) {

        hitbox.x += velocity.x;
        for (Player player : battle.getPlayers()) {
            CollisionResolver.resolveX(player, player.velocity.x, this.hitbox);
        }
        for (Attack attack : battle.getAttacks()) {
            CollisionResolver.resolveX(attack, attack.velocity.x, this.hitbox);
        }

        hitbox.y += velocity.y;
        for (Player player : battle.getPlayers()) {
            CollisionResolver.resolveY(player, player.velocity.y, this.hitbox);
        }
        for (Attack attack : battle.getAttacks()) {
            CollisionResolver.resolveY(attack, attack.velocity.y, this.hitbox);
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
