package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class RheumdaubedOnyxscaleBlock extends Block {
    public static final MapCodec<RheumdaubedOnyxscaleBlock> CODEC = simpleCodec(RheumdaubedOnyxscaleBlock::new);

    @Override
    public MapCodec<RheumdaubedOnyxscaleBlock> codec() {
        return CODEC;
    }

    protected RheumdaubedOnyxscaleBlock(Properties properties) {
        super(properties);
    }

    private static boolean canBeRheumdaubed(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeRheumdaubed(state, level, pos)) {
            level.setBlockAndUpdate(pos, MirthdewEncoreBlocks.ONYXSCALE.defaultBlockState());
        }
    }
}
