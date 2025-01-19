package phanastrae.mirthdew_encore.server.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.block.entity.DoorMarkerBlockEntity;
import phanastrae.mirthdew_encore.network.packet.SetDoorMarkerBlockPayload;

public class MirthdewEncoreServerPacketHandler {

    public static void handleSetDoorMarkerBlock(SetDoorMarkerBlockPayload payload, Player player) {
        // TODO is the thread thing needed for custom payloads??
        //PacketUtils.ensureRunningOnSameThread(packet, this, this.player.serverLevel());
        if (player.canUseGameMasterBlocks()) {
            BlockPos blockpos = payload.blockPos();
            Level level = player.level();
            BlockState blockstate = level.getBlockState(blockpos);
            if (level.getBlockEntity(blockpos) instanceof DoorMarkerBlockEntity doorMarkerBlockEntity) {
                // TODO add rest of functionality
                //doorMarkerBlockEntity.setName(packet.getName());
                //doorMarkerBlockEntity.setTarget(packet.getTarget());
                //doorMarkerBlockEntity.setPool(ResourceKey.create(Registries.TEMPLATE_POOL, packet.getPool()));
                //doorMarkerBlockEntity.setFinalState(packet.getFinalState());
                //doorMarkerBlockEntity.setJoint(packet.getJoint());
                //doorMarkerBlockEntity.setPlacementPriority(packet.getPlacementPriority());
                //doorMarkerBlockEntity.setSelectionPriority(packet.getSelectionPriority());
                doorMarkerBlockEntity.setDoorType(payload.doorType());
                doorMarkerBlockEntity.setChanged();
                level.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
            }
        }
    }
}
