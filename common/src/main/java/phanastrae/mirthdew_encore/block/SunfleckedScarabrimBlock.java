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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LightEngine;
import net.minecraft.world.phys.Vec3;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class SunfleckedScarabrimBlock extends Block {
    public static final MapCodec<SunfleckedScarabrimBlock> CODEC = simpleCodec(SunfleckedScarabrimBlock::new);

    @Override
    public MapCodec<SunfleckedScarabrimBlock> codec() {
        return CODEC;
    }

    protected SunfleckedScarabrimBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    private static boolean canBeSunflecked(BlockState state, LevelReader reader, BlockPos pos) {
        BlockPos blockpos = pos.above();
        BlockState blockstate = reader.getBlockState(blockpos);
        int i = LightEngine.getLightBlockInto(reader, state, pos, blockstate, blockpos, Direction.UP, blockstate.getLightBlock(reader, blockpos));
        return i < reader.getMaxLightLevel();
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!canBeSunflecked(state, level, pos)) {
            level.setBlockAndUpdate(pos, MirthdewEncoreBlocks.SCARABRIM.defaultBlockState());
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(7) == 0) {
            BlockPos blockpos = pos.above();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.DOWN)) {
                ParticleUtils.spawnParticleOnFace(level, pos, Direction.UP, MirthdewEncoreParticleTypes.SUNFLECK, new Vec3(0, 0.2, 0), 0.5);
            }
        }
    }
}
