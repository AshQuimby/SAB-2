package sab.net;

import com.badlogic.gdx.math.Vector2;

public class Packets {
    // Packet headers
    public static final byte KICK = 0x00; // reason: UTF

    public static final byte KEY_PRESS = 0x01; // id: byte
    public static final byte KEY_RELEASE = 0x02; // id: byte

    public static final byte PLAYER_STATE = 0x03; // player_id: byte, position: vec2, velocity: vec2, frame: byte

    public static final void sendKick(Connection connection, String reason) {
        connection.writeByte(KICK);
        connection.writeUTF(reason);
    }

    public static void sendKeyPress(Connection connection, byte key) {
        connection.writeByte(KEY_PRESS);
        connection.writeByte(key);
    }

    public static void sendKeyRelease(Connection connection, byte key) {
        connection.writeByte(KEY_RELEASE);
        connection.writeByte(key);
    }

    public static void sendVector(Connection connection , Vector2 vector) {
        connection.writeFloat(vector.x);
        connection.writeFloat(vector.y);
    }

    public static void sendPlayerState(Connection connection, byte playerId, Vector2 position, Vector2 velocity, byte frame) {
        connection.writeByte(PLAYER_STATE);
        connection.writeByte(playerId);
        sendVector(connection, position);
        sendVector(connection, velocity);
        connection.writeByte(frame);
    }

    public static Vector2 readVector(Connection connection) {
        return new Vector2(connection.readFloat(), connection.readFloat());
    }
}