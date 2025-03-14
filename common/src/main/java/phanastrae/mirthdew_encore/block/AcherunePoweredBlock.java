package phanastrae.mirthdew_encore.block;

import com.google.common.collect.Sets;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;

import java.util.Set;

public class AcherunePoweredBlock extends Block {
    public static final IntegerProperty ACHERUNE_POWER = IntegerProperty.create("acherune_power", 0, 31);
    public static final MapCodec<AcherunePoweredBlock> CODEC = simpleCodec(AcherunePoweredBlock::new);

    @Override
    public MapCodec<AcherunePoweredBlock> codec() {
        return CODEC;
    }

    public AcherunePoweredBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(ACHERUNE_POWER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACHERUNE_POWER);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (!oldState.is(state.getBlock()) && !level.isClientSide) {
            this.updatePowerStrength(level, pos, state);

            level.updateNeighborsAt(pos, this);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);
        if (!isMoving && !state.is(newState.getBlock()) && !level.isClientSide) {
            level.updateNeighborsAt(pos, this);

            this.updatePowerStrength(level, pos, state);
        }
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            this.updatePowerStrength(level, pos, state);
        }
    }

    private void updatePowerStrength(Level level, BlockPos pos, BlockState state) {
        int targetPower = this.calculateTargetPower(level, pos);
        if (state.getValue(ACHERUNE_POWER) != targetPower) {
            if (level.getBlockState(pos) == state) {
                level.setBlock(pos, state.setValue(ACHERUNE_POWER, targetPower), 2);
            }

            Set<BlockPos> updatePosSet = Sets.newHashSet();
            updatePosSet.add(pos);
            for (Direction direction : Direction.values()) {
                updatePosSet.add(pos.relative(direction));
            }

            for (BlockPos blockpos : updatePosSet) {
                level.updateNeighborsAt(blockpos, this);
            }
        }
    }

    private int calculateTargetPower(Level level, BlockPos pos) {
        if(level.getBlockState(pos.above()).isFaceSturdy(level, pos, Direction.DOWN)) {
            return 0;
        } else {
            int power = 0;
            for(Direction direction : Direction.Plane.HORIZONTAL) {
                BlockPos adjPos = pos.relative(direction);
                BlockState adjState = level.getBlockState(adjPos);
                power = Math.max(power, getPower(adjState));
            }

            return Math.max(0, power - 1);
        }
    }

    public static int getPower(BlockState state) {
        if(state.hasProperty(ACHERUNE_POWER)) {
            return state.getValue(ACHERUNE_POWER);
        } else if(state.is(MirthdewEncoreBlocks.GREATER_ACHERUNE)) {
            return state.getValue(GreaterAcheruneBlock.RUNE_STATE) == GreaterAcheruneBlock.RuneState.BOUND ? 32 : 0;
        } else {
            return 0;
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if(state.getValue(ACHERUNE_POWER) > 0) {
            if(random.nextBoolean()) {
                level.addParticle(ParticleTypes.ENCHANT,
                        pos.getX() + random.nextFloat(),
                        pos.getY() + 1F + random.nextFloat(),
                        pos.getZ() + random.nextFloat(),
                        0,
                        0.5,
                        0
                );
            }

            for(int i = 0; i < 3; i++) {
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
}
