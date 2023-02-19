package sab.net.packet;

import sab.net.Connection;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class PacketManager {
    private final Map<Byte, Class<? extends Packet>> registeredPacketTypes;
    private final Map<Class<? extends Packet>, Byte> typeIds;
    private byte nexId;

    public PacketManager() {
        registeredPacketTypes = new HashMap<>();
        typeIds = new HashMap<>();
        nexId = Byte.MIN_VALUE;
    }

    public boolean register(Class<? extends Packet> type) {
        if (registeredPacketTypes.size() == 256) {
            return false;
        }

        registeredPacketTypes.put(nexId, type);
        typeIds.put(type, nexId);

        nexId++;
        return true;
    }

    public Packet getPacket(byte id) {
        if (id > nexId - 1) {
            return null;
        }

        Class<? extends Packet> type = registeredPacketTypes.get(id);
        try {
            return type.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte getId(Class<? extends Packet> type) {
        return typeIds.get(type);
    }

    public void sendPacket(Connection connection, Packet packet) throws IOException {
        connection.writeByte(getId(packet.getClass()));
        packet.send(connection);
    }
}
