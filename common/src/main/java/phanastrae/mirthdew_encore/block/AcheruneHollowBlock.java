package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.util.RegionPos;

public class AcheruneHollowBlock extends Block {
    public static final MapCodec<AcheruneHollowBlock> CODEC = simpleCodec(AcheruneHollowBlock::new);

    @Override
    public MapCodec<AcheruneHollowBlock> codec() {
        return CODEC;
    }

    public AcheruneHollowBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
            if(stage != null) {
                StageAcherunes stageAcherunes = stage.getStageAcherunes();
                Acherune nearestAcherune = stageAcherunes.getNearestAcheruneToPos(pos, level.getRandom());
                if(nearestAcherune != null) {
                    BlockPos oldPos = nearestAcherune.getPos();

                    if(pos.distSqr(oldPos) < 16.0 * 16.0) {
                        boolean moved = stageAcherunes.moveAcherune(oldPos, pos);
                        if (moved) {
                            level.destroyBlock(oldPos, false);

                            BlockState newState = MirthdewEncoreBlocks.GREATER_ACHERUNE.defaultBlockState();
                            level.setBlock(pos, newState, 3);

                            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, newState));
                            level.playSound(null, oldPos.getX() + 0.5, oldPos.getY() + 0.5, oldPos.getZ() + 0.5, SoundEvents.GENERIC_EAT, SoundSource.BLOCKS, 1.0F, 1.0F);
                            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.AMETHYST_BLOCK_RESONATE, SoundSource.BLOCKS, 1.0F, 1.0F);

                            return InteractionResult.CONSUME;
                        }
                    }
                }
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}