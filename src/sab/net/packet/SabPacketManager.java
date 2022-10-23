package sab.net.packet;

public class SabPacketManager extends PacketManager {
    public SabPacketManager() {
        register(JoinedGamePacket.class);
        register(KeyEventPacket.class);
        register(KickPacket.class);
        register(PlayerStatePacket.class);
        register(SpawnParticlePacket.class);
    }
}
