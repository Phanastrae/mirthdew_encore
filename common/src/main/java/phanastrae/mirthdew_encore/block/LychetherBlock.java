package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.TransparentBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class LychetherBlock extends TransparentBlock {
    public static final MapCodec<LychetherBlock> CODEC = simpleCodec(LychetherBlock::new);
    public static final BooleanProperty DISSOLVING = BooleanProperty.create("dissolving");

    @Override
    protected MapCodec<? extends TransparentBlock> codec() {
        return CODEC;
    }

    public LychetherBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(DISSOLVING, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DISSOLVING);
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        boolean dissolving = state.getValue(DISSOLVING);
        if(dissolving) {
            level.destroyBlock(pos, false);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.LAVA_POP, SoundSource.BLOCKS, 1.5F, 0.8F + random.nextFloat() * 0.5F);

            dissolveAdjacentLychether(level, pos, random);
        }
    }

    public static void dissolveAdjacentLychether(Level level, BlockPos pos, RandomSource random) {
        for(Direction direction : Direction.values()) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);

            if(adjState.is(MirthdewEncoreBlocks.LYCHETHER) && adjState.hasProperty(DISSOLVING)) {
                boolean adjDissolving = adjState.getValue(DISSOLVING);
                if(!adjDissolving) {
                    level.setBlockAndUpdate(adjPos, adjState.setValue(DISSOLVING, true));
                    level.scheduleTick(adjPos, adjState.getBlock(), 3 + random.nextInt(7));
                }
            }
        }
    }
}
