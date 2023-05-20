package sab.net.packet;

public class SabPacketManager extends PacketManager {
    public SabPacketManager() {
        register(JoinedGamePacket.class);
        register(CharacterSelectPacket.class);
        register(KeyEventPacket.class);
        register(KickPacket.class);
        register(PlayerStatePacket.class);
        register(SpawnParticlePacket.class);
        register(ScreenTransitionPacket.class);
        register(StageSelectPacket.class);
    }
}
