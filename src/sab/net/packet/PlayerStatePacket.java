package sab.net.packet;

import com.badlogic.gdx.math.Vector2;
import sab.game.Player;
import sab.net.Connection;

import java.io.IOException;

public class PlayerStatePacket implements Packet {
    public byte playerId;
    public Vector2 position;
    public Vector2 velocity;
    public byte inputs;
    public boolean facingRight;
    public byte[] data;

    public PlayerStatePacket(Player player) {
        playerId = (byte) player.getId();
        position = player.hitbox.getPosition(new Vector2());
        velocity = player.velocity;

        inputs = (byte) 0;

        for (int i = 0; i < 6; i++) {
            if (player.keys.isPressed(i)) {
                inputs |= 1 << i;
            }
        }

        facingRight = player.direction == 1;

        data = player.fighter.type.getData();
    }

    public PlayerStatePacket() {

    }

    @Override
    public void send(Connection connection) throws IOException {
        connection.writeByte(playerId);
        connection.writeFloat(position.x);
        connection.writeFloat(position.y);
        connection.writeFloat(velocity.x);
        connection.writeFloat(velocity.y);
        connection.writeByte(inputs);
        connection.writeBoolean(facingRight);
        connection.writeByte(data == null ? 0 : (byte) data.length);
        if (data != null) {
            for (byte datum : data) {
                connection.writeByte(datum);
            }
        }
    }

    @Override
    public void receive(Connection connection) throws IOException {
        playerId = connection.readByte();
        position = new Vector2(connection.readFloat(), connection.readFloat());
        velocity = new Vector2(connection.readFloat(), connection.readFloat());
        inputs = connection.readByte();
        facingRight = connection.readBoolean();

        int dataLength = connection.readByte();
        if (dataLength > 0) {
            data = new byte[dataLength];
            for (byte i = 0; i < dataLength; i++) {
                data[i] = connection.readByte();
            }
        }
    }

    public void syncPlayer(Player player) {
        player.hitbox.setPosition(position);
        player.velocity.set(velocity);

        for (int i = 0; i < 6; i++) {
            if (((inputs >> i) & 1) == 1) {
                if (!player.keys.isPressed(i)) {
                    player.keys.press(i);
                }
            } else {
                if (player.keys.isPressed(i)) {
                    player.keys.release(i);
                }
            }
        }

        player.direction = facingRight ? 1 : -1;

        player.fighter.type.setData(data);
    }
}


