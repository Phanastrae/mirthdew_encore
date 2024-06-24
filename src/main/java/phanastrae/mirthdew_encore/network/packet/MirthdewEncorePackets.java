package phanastrae.mirthdew_encore.network.packet;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class MirthdewEncorePackets {

    public static void init() {
        PayloadTypeRegistry.playS2C().register(FoodDebtUpdatePayload.PACKET_ID, FoodDebtUpdatePayload.PACKET_CODEC);
        PayloadTypeRegistry.playS2C().register(MirthUpdatePayload.PACKET_ID, MirthUpdatePayload.PACKET_CODEC);
    }
}
