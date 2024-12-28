package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SoulspotMushrheumBlock extends BushBlock implements BonemealableBlock {
    public static final MapCodec<SoulspotMushrheumBlock> CODEC = simpleCodec(SoulspotMushrheumBlock::new);
    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);

    @Override
    public MapCodec<SoulspotMushrheumBlock> codec() {
        return CODEC;
    }

    public SoulspotMushrheumBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public void spread(BlockState state, ServerLevel level, BlockPos pos, RandomSource random, int allowedNearby) {
        for (BlockPos blockpos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
            if (level.getBlockState(blockpos).is(this)) {
                if (--allowedNearby <= 0) {
                    return;
                }
            }
        }

        BlockPos blockpos1 = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);

        for (int k = 0; k < 4; k++) {
            if (level.isEmptyBlock(blockpos1) && canPlant(level, blockpos1) && state.canSurvive(level, blockpos1)) {
                pos = blockpos1;
            }

            blockpos1 = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
        }

        if (level.isEmptyBlock(blockpos1) && canPlant(level, blockpos1) && state.canSurvive(level, blockpos1)) {
            level.setBlock(blockpos1, state, 2);
        }
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isSolidRender(level, pos);
    }

    public boolean validGround(BlockState blockstate) {
        return (blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK) || blockstate.is(BlockTags.LOGS) || blockstate.is(MirthdewEncoreBlocks.RHEUMDAUBED_ONYXSCALE));
    }

    protected boolean canPlant(LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return validGround(blockstate);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        return validGround(blockstate) || this.mayPlaceOn(blockstate, level, blockpos);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        for(int i = 0; i < 9; i++) {
            spread(state, level, pos, random, 8);
        }
    }
}
