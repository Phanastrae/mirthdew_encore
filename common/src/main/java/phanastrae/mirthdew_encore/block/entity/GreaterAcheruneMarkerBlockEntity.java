package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class GreaterAcheruneMarkerBlockEntity extends BlockEntity {

    public GreaterAcheruneMarkerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.GREATER_ACHERUNE_MARKER, pos, blockState);
    }
}
