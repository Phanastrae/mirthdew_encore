package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

public class WakesideRuneBlock extends Block {
    public static final MapCodec<WakesideRuneBlock> CODEC = simpleCodec(WakesideRuneBlock::new);

    @Override
    public MapCodec<WakesideRuneBlock> codec() {
        return CODEC;
    }

    public WakesideRuneBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for(int i = 0; i < 2; i++) {
            level.addParticle(MirthdewEncoreParticleTypes.BACCHENITE_GLIMMER,
                    pos.getX() + random.nextFloat(),
                    pos.getY() + 1F + random.nextFloat(),
                    pos.getZ() + random.nextFloat(),
                    (random.nextFloat() - 0.5) * 0.01,
                    0.1,
                    (random.nextFloat() - 0.5) * 0.01
            );
        }
    }
}
