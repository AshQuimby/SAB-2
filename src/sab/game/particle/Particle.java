package sab.game.particle;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.seagull_engine.GameObject;
import com.seagull_engine.Seagraphics;

public class Particle extends GameObject {
    public byte type;
    public boolean alive;
    public Color tint;

    public int age;
    public float rotationSpeed;
    public int frameLength;
    public float gravity;

    public Particle(Vector2 position, Vector2 velocity, float width, float height, String image) {
        imageName = image;
        hitbox = new Rectangle(position.x - width / 2, position.y - width / 2, width, height);
        drawRect = new Rectangle(hitbox);
        this.velocity = velocity;

        alive = true;
        tint = Color.WHITE;

        frameCount = 1;
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, float maxRotationSpeed, String image) {
        this(position, velocity, width, height, image);
        rotationSpeed = MathUtils.random(-maxRotationSpeed, maxRotationSpeed);
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, float maxRotationSpeed, Color tint, String image) {
        this(position, velocity, width, height, image);
        rotationSpeed = MathUtils.random(-maxRotationSpeed, maxRotationSpeed);
        this.tint = tint;
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, int frameCount, int frameLength, String image) {
        this(position, velocity, width, height, image);
        this.frameCount = frameCount;
        this.frameLength = frameLength;
        type = 1;
    }

    public Particle(Vector2 position, Vector2 velocity, float width, float height, int frameCount, int frameLength, int direction, String image) {
        this(position, velocity, width, height, image);
        this.frameCount = frameCount;
        this.frameLength = frameLength;
        this.direction = direction;
        type = 1;
    }

    public Particle(float gravity, Vector2 position, Vector2 velocity, float width, float height, String image) {
        this(position, velocity, width, height, image);
        this.gravity = gravity;
        type = 2;
    }

    public Particle(float gravity, Vector2 position, Vector2 velocity, float width, float height, int maxRotationSpeed, String image) {
        this(position, velocity, width, height, image);
        rotationSpeed = MathUtils.random(-maxRotationSpeed, maxRotationSpeed);
        this.gravity = gravity;
        type = 2;
    }

    @Override
    public void preUpdate() {
        hitbox.x += velocity.x;
        hitbox.y += velocity.y;
        rotation += rotationSpeed;
        velocity.y -= gravity;
        age++;

        switch (type) {
            case 0 -> {
                resize(hitbox.width * .95f, hitbox.height * .95f);
                if (hitbox.width < 2f || hitbox.height < 2f) alive = false;
            }

            case 1 -> {
                if (age % frameLength == 0) frame++;
                if (frame == frameCount) alive = false;
            }

            case 2 -> {
                if (hitbox.y < -400) alive = false;
            }
        }

        drawRect.set(hitbox);
    }

    @Override
    public void render(Seagraphics g) {
        preRender(g);
        g.usefulTintDraw(g.imageProvider.getImage(imageName), drawRect.x, drawRect.y, (int) drawRect.width, (int) drawRect.height, frame, frameCount, rotation, direction == 1, false, tint);
        postRender(g);
    }
}
