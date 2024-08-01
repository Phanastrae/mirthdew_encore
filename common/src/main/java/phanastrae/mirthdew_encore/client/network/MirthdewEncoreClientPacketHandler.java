package phanastrae.mirthdew_encore.client.network;

import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.FoodDebtUpdatePayload;
import phanastrae.mirthdew_encore.network.packet.MirthUpdatePayload;

public class MirthdewEncoreClientPacketHandler {

    public static void handleFoodDebtUpdate(FoodDebtUpdatePayload payload, Player player) {
        PlayerEntityHungerData hungerData = PlayerEntityHungerData.fromPlayer(player);
        hungerData.setFoodLevelDebt(payload.foodLevelDebt());
    }

    public static void handleMirthUpdate(MirthUpdatePayload payload, Player player) {
        PlayerEntityMirthData mirthData = PlayerEntityMirthData.fromPlayer(player);
        mirthData.setMirth(payload.mirth());
    }
}
