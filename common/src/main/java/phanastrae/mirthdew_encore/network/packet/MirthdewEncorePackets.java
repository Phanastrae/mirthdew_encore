package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.services.Services;

public class MirthdewEncorePackets {

    public static void init() {
        registerPlayS2CPayload(FoodDebtUpdatePayload.PACKET_ID, FoodDebtUpdatePayload.PACKET_CODEC);
        registerPlayS2CPayload(MirthUpdatePayload.PACKET_ID, MirthUpdatePayload.PACKET_CODEC);
    }

    public static <T extends CustomPacketPayload> void registerPlayS2CPayload(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec) {
        Services.XPLAT.registerPlayS2CPayload(id, codec);
    }
}
