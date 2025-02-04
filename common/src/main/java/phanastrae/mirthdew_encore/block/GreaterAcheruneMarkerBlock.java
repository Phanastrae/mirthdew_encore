package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.block.entity.GreaterAcheruneMarkerBlockEntity;

public class GreaterAcheruneMarkerBlock extends Block implements EntityBlock, GameMasterBlock {
    public static final MapCodec<GreaterAcheruneMarkerBlock> CODEC = simpleCodec(GreaterAcheruneMarkerBlock::new);

    @Override
    public MapCodec<GreaterAcheruneMarkerBlock> codec() {
        return CODEC;
    }

    protected GreaterAcheruneMarkerBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreaterAcheruneMarkerBlockEntity(pos, state);
    }
}
