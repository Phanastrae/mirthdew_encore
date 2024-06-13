package phanastrae.mirthdew_encore.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;

public class MirthdewEncoreClientPacketHandler {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(FoodDebtUpdatePayload.PACKET_ID, MirthdewEncoreClientPacketHandler::handleFoodDebtUpdate);
    }

    public static void handleFoodDebtUpdate(FoodDebtUpdatePayload payload, ClientPlayNetworking.Context context) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(context.player());
        hungerData.setFoodLevelDebt(payload.foodLevelDebt());
    }
}
