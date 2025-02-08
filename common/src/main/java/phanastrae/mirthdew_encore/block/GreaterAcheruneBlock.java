package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import phanastrae.mirthdew_encore.block.entity.GreaterAcheruneBlockEntity;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.util.BlockPosDimensional;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Set;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE;

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

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
            if(stage != null) {
                StageAcherunes stageAcherunes = stage.getStageAcherunes();
                Acherune acherune = stageAcherunes.getAcherune(pos);
                MinecraftServer server = level.getServer();
                if(acherune != null && server != null) {
                    boolean valid = acherune.validateLinkedPos(server, stageAcherunes);
                    if(valid) {
                        BlockPosDimensional linkedPos = acherune.getLinkedPos();
                        if (linkedPos != null) {
                            if (linkedPos.getLevel(server) instanceof ServerLevel linkedLevel) {
                                player.teleportTo(linkedLevel, linkedPos.x() + 0.5, linkedPos.y() + 1.0, linkedPos.z() + 0.5, Set.of(), player.getXRot(), player.getYRot());
                                return InteractionResult.CONSUME;
                            }
                        }
                    }
                }
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(stack.is(Items.ENDER_EYE)) {
            if(level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            } else {
                DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, RegionPos.fromBlockPos(pos));
                if(stage != null) {
                    Acherune acherune = stage.getStageAcherunes().getAcherune(pos);
                    if(acherune != null) {
                        ItemStack newStack = MirthdewEncoreItems.SLEEPY_EYE.getDefaultInstance();
                        newStack.applyComponentsAndValidate(stack.getComponentsPatch());
                        newStack.set(LINKED_ACHERUNE, LinkedAcheruneComponent.fromAcheruneAndStage(stage, acherune));

                        stack.consume(1, player);

                        if (!player.getInventory().add(newStack.copy())) {
                            player.drop(newStack, false);
                        }

                        return ItemInteractionResult.CONSUME;
                    }
                }
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }
}
