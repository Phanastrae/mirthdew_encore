package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ParticleUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class DecidrheumLeavesBlock extends LeavesBlock {
    public static final MapCodec<DecidrheumLeavesBlock> CODEC = simpleCodec(DecidrheumLeavesBlock::new);

    @Override
    public MapCodec<DecidrheumLeavesBlock> codec() {
        return CODEC;
    }

    public DecidrheumLeavesBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(3) == 0) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = level.getBlockState(blockpos);
            if (!isFaceFull(blockstate.getCollisionShape(level, blockpos), Direction.UP)) {
                ParticleUtils.spawnParticleBelow(level, pos, random, MirthdewEncoreParticleTypes.DECIDRHEUM_LEAVES);
            }
        }
    }
}
