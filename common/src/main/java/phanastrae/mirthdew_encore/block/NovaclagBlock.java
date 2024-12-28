package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;

public class NovaclagBlock extends Block {
    public static final MapCodec<NovaclagBlock> CODEC = simpleCodec(NovaclagBlock::new);

    @Override
    public MapCodec<NovaclagBlock> codec() {
        return CODEC;
    }

    protected NovaclagBlock(Properties properties) {
        super(properties);
    }

    private static boolean canExist(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canExist(state, level, pos)) {
            level.setBlockAndUpdate(pos, MirthdewEncoreBlocks.GACHERIMM.defaultBlockState());
        }
    }
}
