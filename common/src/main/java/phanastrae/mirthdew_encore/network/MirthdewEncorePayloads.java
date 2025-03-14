package phanastrae.mirthdew_encore.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.network.packet.*;
import phanastrae.mirthdew_encore.server.network.MirthdewEncoreServerPacketHandler;

import java.util.function.BiConsumer;

public class MirthdewEncorePayloads {

    public static void init(Helper helper) {
        helper.registerS2C(MirthUpdatePayload.PACKET_ID, MirthUpdatePayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleMirthUpdate);
        helper.registerS2C(FoodDebtUpdatePayload.PACKET_ID, FoodDebtUpdatePayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleFoodDebtUpdate);

        helper.registerS2C(EntityAcheruneWarpingPayload.PACKET_ID, EntityAcheruneWarpingPayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleEntityAcheruneWarping);

        helper.registerS2C(DreamtwirlDebugPayload.PACKET_ID, DreamtwirlDebugPayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleDreamtwirlDebug);


        helper.registerC2S(SetDoorMarkerBlockPayload.PACKET_ID, SetDoorMarkerBlockPayload.PACKET_CODEC, MirthdewEncoreServerPacketHandler::handleSetDoorMarkerBlock);
        helper.registerC2S(SetLychsealMarkerBlockPayload.PACKET_ID, SetLychsealMarkerBlockPayload.PACKET_CODEC, MirthdewEncoreServerPacketHandler::handleSetLychsealMarkerBlock);
    }

    public interface Helper {
        <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback);
        <T extends CustomPacketPayload> void registerC2S(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> serverCallback);
    }
}
