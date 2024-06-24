package phanastrae.mirthdew_encore.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;
import phanastrae.mirthdew_encore.network.packet.MirthUpdatePayload;

public class MirthdewEncoreClientPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FoodDebtUpdatePayload.PACKET_ID, MirthdewEncoreClientPacketHandler::handleFoodDebtUpdate);
        ClientPlayNetworking.registerGlobalReceiver(MirthUpdatePayload.PACKET_ID, MirthdewEncoreClientPacketHandler::handleMirthUpdate);
    }

    public static void handleFoodDebtUpdate(FoodDebtUpdatePayload payload, ClientPlayNetworking.Context context) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(context.player());
        hungerData.setFoodLevelDebt(payload.foodLevelDebt());
    }

    public static void handleMirthUpdate(MirthUpdatePayload payload, ClientPlayNetworking.Context context) {
        PlayerEntityMirthData mirthData = PlayerEntityMirthData.fromPlayer(context.player());
        mirthData.setMirth(payload.mirth());
    }
}
