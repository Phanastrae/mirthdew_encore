package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.GreaterAcheruneBlockEntity;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.component.type.LinkedAcheruneComponent;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

import static phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes.LINKED_ACHERUNE;

public class GreaterAcheruneBlock extends BaseEntityBlock {
    public static final EnumProperty<RuneState> RUNE_STATE = EnumProperty.create("rune_state", RuneState.class);
    public static final MapCodec<GreaterAcheruneBlock> CODEC = simpleCodec(GreaterAcheruneBlock::new);

    @Override
    public MapCodec<GreaterAcheruneBlock> codec() {
        return CODEC;
    }

    protected GreaterAcheruneBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(RUNE_STATE, RuneState.INERT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RUNE_STATE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new GreaterAcheruneBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MirthdewEncoreBlockEntityTypes.GREATER_ACHERUNE,
                level.isClientSide() ? GreaterAcheruneBlockEntity::tickClient : GreaterAcheruneBlockEntity::tickServer);
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
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof GreaterAcheruneBlockEntity gabe) {
                gabe.onRemove(state, level, pos, newState);
            }
        }

        super.onRemove(state, level, pos, newState, movedByPiston);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if(state.getValue(RUNE_STATE) != RuneState.INERT && stack.is(Items.ENDER_EYE)) {
            if (level.isClientSide) {
                return ItemInteractionResult.SUCCESS;
            } else {
                DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, pos);
                if (stage != null) {
                    Acherune acherune = stage.getStageAcherunes().getAcherune(pos);
                    if (acherune != null) {
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

    public enum RuneState implements StringRepresentable {
        INERT("inert"),
        UNBOUND("unbound"),
        BOUND("bound");

        String name;

        RuneState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
