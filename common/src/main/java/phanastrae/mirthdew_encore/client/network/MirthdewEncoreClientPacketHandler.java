package phanastrae.mirthdew_encore.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;
import phanastrae.mirthdew_encore.network.packet.MirthUpdatePayload;

public class MirthdewEncoreClientPacketHandler {

    public static void init() {
        registerGlobalReceiver(FoodDebtUpdatePayload.PACKET_ID, MirthdewEncoreClientPacketHandler::handleFoodDebtUpdate);
        registerGlobalReceiver(MirthUpdatePayload.PACKET_ID, MirthdewEncoreClientPacketHandler::handleMirthUpdate);
    }

    @FunctionalInterface
    public interface PayloadHandler<T extends CustomPacketPayload> {
        void receive(T payload, Minecraft client, LocalPlayer player);
    }

    private static <T extends CustomPacketPayload> void registerGlobalReceiver(CustomPacketPayload.Type<T> type, PayloadHandler<T> handler) {
        XPlatClientInterface.INSTANCE.registerGlobalReceiver(type, handler);
    }

    public static void handleFoodDebtUpdate(FoodDebtUpdatePayload payload, Minecraft client, LocalPlayer player) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(player);
        hungerData.setFoodLevelDebt(payload.foodLevelDebt());
    }

    public static void handleMirthUpdate(MirthUpdatePayload payload, Minecraft client, LocalPlayer player) {
        PlayerEntityMirthData mirthData = PlayerEntityMirthData.fromPlayer(player);
        mirthData.setMirth(payload.mirth());
    }
}
