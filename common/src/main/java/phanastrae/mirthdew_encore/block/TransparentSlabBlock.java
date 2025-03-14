package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TransparentSlabBlock extends SlabBlock {
    public static final MapCodec<TransparentSlabBlock> CODEC = simpleCodec(TransparentSlabBlock::new);
    public static final EnumProperty<SlabType> TYPE = BlockStateProperties.SLAB_TYPE;

    @Override
    public MapCodec<? extends TransparentSlabBlock> codec() {
        return CODEC;
    }

    public TransparentSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected boolean skipRendering(BlockState state, BlockState adjacentBlockState, Direction side) {
        if(adjacentBlockState.is(this)) {
            if(state.hasProperty(TYPE) && adjacentBlockState.hasProperty(TYPE)) {
                SlabType type = state.getValue(TYPE);
                SlabType adjacentType = adjacentBlockState.getValue(TYPE);

                if (side.getAxis().isHorizontal()) {
                    if(type.equals(adjacentType) || adjacentType.equals(SlabType.DOUBLE)) {
                        return true;
                    }
                } else {
                    // axis is vertical
                    if(side.equals(Direction.UP)) {
                        if(!type.equals(SlabType.BOTTOM) && !adjacentType.equals(SlabType.TOP)) {
                            return true;
                        }
                    } else {
                        // side is down
                        if(!type.equals(SlabType.TOP) && !adjacentType.equals(SlabType.BOTTOM)) {
                            return true;
                        }
                    }
                }
            }
        }

        return super.skipRendering(state, adjacentBlockState, side);
    }

    @Override
    protected VoxelShape getVisualShape(BlockState p_309057_, BlockGetter p_308936_, BlockPos p_308956_, CollisionContext p_309006_) {
        return Shapes.empty();
    }

    @Override
    protected float getShadeBrightness(BlockState p_308911_, BlockGetter p_308952_, BlockPos p_308918_) {
        return 1.0F;
    }

    @Override
    protected boolean propagatesSkylightDown(BlockState p_309084_, BlockGetter p_309133_, BlockPos p_309097_) {
        return true;
    }
}
