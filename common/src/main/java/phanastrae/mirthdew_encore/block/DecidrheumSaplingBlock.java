package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.grower.TreeGrower;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.data.worldgen.features.MirthdewEncoreTreeFeatures;

import java.util.Optional;

public class DecidrheumSaplingBlock extends SaplingBlock {

    protected DecidrheumSaplingBlock(TreeGrower treeGrower, Properties properties) {
        super(treeGrower, properties);
    }

    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(MirthdewEncoreBlocks.RHEUMDAUBED_ONYXSCALE) || state.is(MirthdewEncoreBlocks.ONYXSCALE);
    }

    public static final TreeGrower DECIDRHEUM = new TreeGrower(
            "mirthdew_encore:decidrheum",
            0.15F,
            Optional.empty(),
            Optional.empty(),
            Optional.of(MirthdewEncoreTreeFeatures.FANCY_DECIDRHEUM),
            Optional.of(MirthdewEncoreTreeFeatures.DECIDRHEUM),
            Optional.empty(),
            Optional.empty()
    );
}
