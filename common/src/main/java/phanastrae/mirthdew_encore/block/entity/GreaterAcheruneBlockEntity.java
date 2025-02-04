package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.util.RegionPos;

public class GreaterAcheruneBlockEntity extends BlockEntity {

    public GreaterAcheruneBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.GREATER_ACHERUNE, pos, blockState);
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
        if(stage != null) {
            StageAcherunes stageAcherunes = stage.getStageAcherunes();

            boolean success = stageAcherunes.create(pos, level);
            if(!success) {
                // if (somehow) this fails, just break the block
                level.destroyBlock(pos, false);
            }
        }
    }

    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState) {
        DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
        if(stage != null) {
            StageAcherunes stageAcherunes = stage.getStageAcherunes();

            stageAcherunes.remove(pos);
        }
    }
}
