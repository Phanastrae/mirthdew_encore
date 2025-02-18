package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LychsealBlockEntity extends BlockEntity {

    public LychsealBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.LYCHSEAL, pos, blockState);
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, LychsealBlockEntity blockEntity) {

    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, LychsealBlockEntity blockEntity) {

    }

    public void open(Level level, BlockPos pos, BlockState state) {
        level.destroyBlock(pos, false);
    }
}
