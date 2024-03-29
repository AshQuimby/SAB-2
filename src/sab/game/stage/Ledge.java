package sab.game.stage;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Ledge {

    public Rectangle grabBox = new Rectangle();
    public int direction;

    private sab.game.stage.Platform platform;
    private Vector2 anchor;
    public boolean hasPlatform;

    public Ledge(Vector2 position, float width, float height, int direction) {
        grabBox.set(position.x, position.y, width, height);
        this.direction = direction;
        hasPlatform = false;
    }

    public Ledge(Platform attachment, Vector2 anchor, float width, float height, int direction) {
        grabBox.set(attachment.hitbox.getCenter(new Vector2()).x + anchor.x, attachment.hitbox.getCenter(new Vector2()).y + anchor.y, width, height);
        this.direction = direction;
        this.anchor = anchor;
        platform = attachment;
        hasPlatform = attachment != null;
    }

    public boolean ownerRemoved() {
        return hasPlatform && platform == null;
    }

    public void update() {
        if (platform != null) grabBox.setPosition(platform.hitbox.getCenter(new Vector2()).x + anchor.x, platform.hitbox.getCenter(new Vector2()).y + anchor.y);
    }

    public Vector2 getGrabPosition(Rectangle other) {
        Vector2 position = new Vector2();
        
        if (direction == -1) {
            position.x = grabBox.x;
            position.y = grabBox.y + grabBox.height - other.height;
        } else {
            position.x = grabBox.x + grabBox.width - other.width;
            position.y = grabBox.y + grabBox.height - other.height;
        }

        return position;
    }

    public boolean touching(Rectangle other) {
        return other.overlaps(other);
    }
}
