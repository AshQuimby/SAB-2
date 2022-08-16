package sab.net;

import com.badlogic.gdx.math.Vector2;

public class Packets {
    // Packet headers
    public static final byte KICK = 0x00;

    public static final byte KEY_PRESS = 0x01;
    public static final byte KEY_RELEASE = 0x02;

    public static final byte ENTITY_POSITION = 0x03;

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

    public static void sendEntityPosition(Connection connection, int id, Vector2 position) {
        connection.writeByte(ENTITY_POSITION);
        connection.writeInt(id);
        connection.writeFloat(position.x);
        connection.writeFloat(position.x);
    }

    public static Vector2 readVector(Connection connection) {
        return new Vector2(connection.readFloat(), connection.readFloat());
    }
}