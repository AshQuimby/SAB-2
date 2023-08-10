package sab.net.packet;

public class SabPacketManager extends PacketManager {
    public SabPacketManager() {
        register(JoinedGamePacket.class);
        register(CharacterSelectPacket.class);
        register(KeyEventPacket.class);
        register(KickPacket.class);
        register(SpawnParticlePacket.class);
        register(ScreenTransitionPacket.class);
        register(StageSelectPacket.class);
        register(BattleConfigPacket.class);
        register(UpdatePacket.class);
        register(DebugCommandPacket.class);
        register(PausePacket.class);
        register(EndGamePacket.class);
        register(InputPacket.class);
        register(ReadyPacket.class);
    }
}
