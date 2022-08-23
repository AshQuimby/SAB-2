package sab.game.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

public class Particle extends GameObject {
    public boolean alive;
    private float rotationSpeed;
    private Color tint;

    public Particle(Vector2 position, Vector2 velocity, float width, float height, String image) {
        alive = true;
        imageName = image;
        hitbox = new Rectangle();
        hitbox.height = height;
        hitbox.width = width;
        hitbox.setCenter(position);
        drawRect = new Rectangle();
        drawRect.set(hitbox);
        frameCount = 1;
        rotationSpeed = 0;
        tint = Color.WHITE;
        frame = 0;
        this.velocity = velocity;
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, float maxRotationSpeed, String image) {
        alive = true;
        imageName = image;
        hitbox = new Rectangle();
        hitbox.height = height;
        hitbox.width = width;
        hitbox.setCenter(position);
        drawRect = new Rectangle();
        drawRect.set(hitbox);
        frameCount = 1;
        rotationSpeed = (MathUtils.random() * maxRotationSpeed) * 2 - maxRotationSpeed;
        tint = Color.WHITE;
        frame = 0;
        this.velocity = velocity;
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, float maxRotationSpeed, Color tint, String image) {
        alive = true;
        imageName = image;
        hitbox = new Rectangle();
        hitbox.height = height;
        hitbox.width = width;
        hitbox.setCenter(position);
        drawRect = new Rectangle();
        drawRect.set(hitbox);
        frameCount = 1;
        rotationSpeed = (MathUtils.random() * maxRotationSpeed) * 2 - maxRotationSpeed;
        this.tint = tint;
        frame = 0;
        this.velocity = velocity;
    }

    @Override
    public void preUpdate() {
        Rectangle old = new Rectangle(hitbox);
        rotation += rotationSpeed;
        resize(hitbox.width *= 0.95f, hitbox.height *= 0.95f);
        hitbox.setCenter(old.getCenter(new Vector2()));
        hitbox.x += velocity.x;
        hitbox.y += velocity.y;
        drawRect.set(hitbox);
        if (drawRect.width < 2f || drawRect.height < 2f) alive = false;
    }

    @Override
    public void render(Seagraphics g) {
        preRender(g);
        g.usefulTintDraw(g.imageProvider.getImage(imageName), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, direction == 1, false, tint);
        postRender(g);
    }

}
