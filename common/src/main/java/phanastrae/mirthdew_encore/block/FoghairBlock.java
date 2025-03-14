package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreBlockTags;

public class FoghairBlock extends BushBlock {
    public static final MapCodec<FoghairBlock> CODEC = simpleCodec(FoghairBlock::new);
    protected static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 13.0, 14.0);

    @Override
    public MapCodec<FoghairBlock> codec() {
        return CODEC;
    }

    protected FoghairBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(MirthdewEncoreBlockTags.NOVA_CLAG) || state.is(MirthdewEncoreBlocks.GACHERIMM);
    }
}
