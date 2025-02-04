package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.block.entity.GreaterAcheruneBlockEntity;

public class GreaterAcheruneBlock extends Block implements EntityBlock {
    public static final MapCodec<GreaterAcheruneBlock> CODEC = simpleCodec(GreaterAcheruneBlock::new);

    @Override
    public MapCodec<GreaterAcheruneBlock> codec() {
        return CODEC;
    }

    protected GreaterAcheruneBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreaterAcheruneBlockEntity(pos, state);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        super.onPlace(state, level, pos, oldState, movedByPiston);

        if(level.getBlockEntity(pos) instanceof GreaterAcheruneBlockEntity gabe) {
            gabe.onPlace(state, level, pos, oldState);
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if(level.getBlockEntity(pos) instanceof GreaterAcheruneBlockEntity gabe) {
            gabe.onRemove(state, level, pos, newState);
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
