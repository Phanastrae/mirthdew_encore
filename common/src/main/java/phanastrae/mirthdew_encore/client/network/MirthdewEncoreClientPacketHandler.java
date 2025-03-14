package phanastrae.mirthdew_encore.client.network;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlDebug;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityAttachment;
import phanastrae.mirthdew_encore.entity.PlayerEntityHungerData;
import phanastrae.mirthdew_encore.network.packet.DreamtwirlDebugPayload;
import phanastrae.mirthdew_encore.network.packet.EntityAcheruneWarpingPayload;
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

    public static void handleEntityAcheruneWarping(EntityAcheruneWarpingPayload payload, Player player) {
        Level level = player.level();
        Entity entity = level.getEntity(payload.id());
        if (entity != null) {
            MirthdewEncoreEntityAttachment meea = MirthdewEncoreEntityAttachment.fromEntity(entity);
            meea.setWarping(payload.warping());
            meea.setWarpTicks(0);
        }
    }

    public static void handleDreamtwirlDebug(DreamtwirlDebugPayload payload, Player player) {
        DreamtwirlDebug.getInstance().addDebugInfo(payload.getDebugInfo());
    }
}
