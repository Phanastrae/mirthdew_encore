package phanastrae.mirthdew_encore.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;
import phanastrae.mirthdew_encore.network.packet.MirthUpdatePayload;

import java.util.function.BiConsumer;

public class MirthdewEncorePayloads {

    public static void init(Helper helper) {
        helper.registerS2C(MirthUpdatePayload.PACKET_ID, MirthUpdatePayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleMirthUpdate);
        helper.registerS2C(FoodDebtUpdatePayload.PACKET_ID, FoodDebtUpdatePayload.PACKET_CODEC, MirthdewEncoreClientPacketHandler::handleFoodDebtUpdate);
    }

    @FunctionalInterface
    public interface Helper {
        <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback);
    }
}
