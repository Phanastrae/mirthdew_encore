package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LychsealMarkerBlockEntity extends BlockEntity {

    public LychsealMarkerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.LYCHSEAL_MARKER, pos, blockState);
    }
}
