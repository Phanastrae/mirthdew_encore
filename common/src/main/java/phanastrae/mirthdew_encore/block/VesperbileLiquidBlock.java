package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

public class VesperbileLiquidBlock extends CustomLiquidBlock {
    public static final BooleanProperty EMITS_LIGHT = BooleanProperty.create("emits_light");

    protected VesperbileLiquidBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LEVEL, 0)
                .setValue(EMITS_LIGHT, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(EMITS_LIGHT);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if(state != null) {
            state = updateEmitsLight(state, context.getClickedPos(), context.getLevel());
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        return updateEmitsLight(super.updateShape(state, facing, facingState, level, currentPos, facingPos), currentPos, level);
    }

    public static BlockState updateEmitsLight(BlockState state, BlockPos pos, LevelAccessor level) {
        return state.setValue(EMITS_LIGHT, shouldEmitLight(state, pos, level));
    }

    public static boolean shouldEmitLight(BlockState state, BlockPos pos, LevelAccessor level) {
        for(Direction direction : Direction.values()) {
            BlockPos adjPos = pos.relative(direction);
            BlockState adjState = level.getBlockState(adjPos);
            if(emitLightForAdjState(adjState, level, adjPos, direction)) {
                return true;
            }
        }
        return false;
    }

    public static boolean emitLightForAdjState(BlockState adjState, LevelAccessor level, BlockPos adjPos, Direction direction) {
        return adjState.getFluidState().isEmpty() && !adjState.isFaceSturdy(level, adjPos, direction.getOpposite());
    }
}
