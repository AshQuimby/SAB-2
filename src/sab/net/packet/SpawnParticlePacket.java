package sab.net.packet;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import sab.game.particle.Particle;
import sab.net.Connection;

import java.io.IOException;

public class SpawnParticlePacket implements Packet {
    public Particle particle;

    public SpawnParticlePacket(Particle particle) {
        this.particle = particle;
    }

    public SpawnParticlePacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        Vector2 center = particle.hitbox.getPosition(new Vector2());
        Vector2 dimensions = particle.hitbox.getSize(new Vector2());

        connection.writeFloat(center.x);
        connection.writeFloat(center.y);

        connection.writeFloat(particle.velocity.x);
        connection.writeFloat(particle.velocity.y);

        connection.writeFloat(dimensions.x);
        connection.writeFloat(dimensions.y);

        connection.writeFloat(particle.rotationSpeed);
        connection.writeInt(particle.tint.toIntBits());
        connection.writeUTF(particle.imageName);
    }

    @Override
    public void receive(Connection connection) throws IOException {
        Vector2 center = new Vector2(connection.readFloat(), connection.readFloat());
        Vector2 velocity = new Vector2(connection.readFloat(), connection.readFloat());
        float width = connection.readFloat();
        float height = connection.readFloat();
        float rotationSpeed = connection.readFloat();

        Color tint = new Color();
        Color.rgba8888ToColor(tint, connection.readInt());

        String imageName = connection.readUTF();

        particle = new Particle(center, velocity, width, height, rotationSpeed, tint, imageName);
    }
}

