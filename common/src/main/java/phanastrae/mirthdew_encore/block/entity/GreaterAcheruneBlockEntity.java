package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.GreaterAcheruneBlock;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;
import phanastrae.mirthdew_encore.util.RegionPos;

public class GreaterAcheruneBlockEntity extends BlockEntity {

    private @Nullable Acherune acherune = null;
    private boolean hasLink;

    public GreaterAcheruneBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.GREATER_ACHERUNE, pos, blockState);
        this.hasLink = blockState.getValue(GreaterAcheruneBlock.RUNE_STATE) == GreaterAcheruneBlock.RuneState.BOUND;
    }

    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState) {
        this.createIfNeeded(state, level, pos);
    }

    public void createIfNeeded(BlockState state, Level level, BlockPos pos) {
        if(this.acherune == null) {
            DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
            if (stage != null) {
                StageAcherunes stageAcherunes = stage.getStageAcherunes();

                Acherune ac = stageAcherunes.getAcherune(pos);
                if (ac == null) {
                    ac = stageAcherunes.create(pos, level);
                }

                if (ac != null) {
                    this.acherune = ac;
                    level.setBlock(pos, state.setValue(GreaterAcheruneBlock.RUNE_STATE, ac.getLinkedPos() == null ? GreaterAcheruneBlock.RuneState.UNBOUND : GreaterAcheruneBlock.RuneState.BOUND), 3);
                }
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

    public static void tickClient(Level level, BlockPos pos, BlockState state, GreaterAcheruneBlockEntity blockEntity) {
        if(state.getValue(GreaterAcheruneBlock.RUNE_STATE) == GreaterAcheruneBlock.RuneState.BOUND) {
            RandomSource random = level.getRandom();
            level.addParticle(ParticleTypes.ENCHANT,
                    pos.getX() + random.nextFloat(),
                    pos.getY() + 1F + random.nextFloat(),
                    pos.getZ() + random.nextFloat(),
                    0,
                    0.5,
                    0
            );
        }
    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, GreaterAcheruneBlockEntity blockEntity) {
        blockEntity.createIfNeeded(state, level, pos);

        if(blockEntity.acherune != null) {
            BlockPosDimensional bpd = blockEntity.acherune.getLinkedPos();
            boolean nowHasLink = bpd != null;
            boolean hadLink = blockEntity.hasLink;

            if(nowHasLink && !hadLink) {
                level.setBlock(pos, state.setValue(GreaterAcheruneBlock.RUNE_STATE, GreaterAcheruneBlock.RuneState.BOUND), 3);
                blockEntity.hasLink = false;
            } else if(!nowHasLink && hadLink) {
                level.setBlock(pos, state.setValue(GreaterAcheruneBlock.RUNE_STATE, GreaterAcheruneBlock.RuneState.UNBOUND), 3);
                blockEntity.hasLink = true;
            }
        }
    }
}
