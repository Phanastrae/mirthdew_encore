package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.item.SlumberingEyeItem;

public class SlumbersocketBlock extends BaseEntityBlock {
    public static final MapCodec<SlumbersocketBlock> CODEC = simpleCodec(SlumbersocketBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty DREAMING = BooleanProperty.create("dreaming");

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    public SlumbersocketBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DREAMING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DREAMING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        boolean dreaming = false;

        Level world = ctx.getLevel();
        if(!world.isClientSide()) {
            ItemStack itemStack = ctx.getItemInHand();
            CustomData nbtComponent = itemStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY);

            NonNullList<ItemStack> heldItem = NonNullList.withSize(1, ItemStack.EMPTY);
            ContainerHelper.loadAllItems(nbtComponent.copyTag(), heldItem, world.registryAccess());
            ItemStack heldStack = heldItem.getFirst();
            if(!heldStack.isEmpty() && heldStack.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
                dreaming = true;
            }
        }
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(DREAMING, dreaming);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        world.scheduleTick(pos, this, 4);

        super.setPlacedBy(world, pos, state, placer, itemStack);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        world.scheduleTick(pos, this, 4);

        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SlumbersocketBlockEntity slumberSocketBlockEntity) {
                Containers.dropContents(world, pos, slumberSocketBlockEntity.getItems());
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleTick(pos, this, 4);
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void tick(BlockState state, ServerLevel world, BlockPos pos, RandomSource random) {
        if(state.getValue(DREAMING)) {
            BlockPos downPos = pos.below();
            BlockState downState = world.getBlockState(downPos);
            if (!downState.is(this) && downState.isAir()) {
                world.setBlockAndUpdate(downPos,
                        MirthdewEncoreBlocks.SLUMBERVEIL.defaultBlockState()
                                .setValue(SlumberveilBlock.DISTANCE, 0)
                                .setValue(SlumberveilBlock.SUPPORTING, true)
                                .setValue(SlumberveilBlock.AXIS, state.getValue(FACING).getClockWise().getAxis()));
            }
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SlumbersocketBlockEntity slumberSocketBlockEntity) {
            if(!slumberSocketBlockEntity.isHoldingItem()) {
                ItemStack itemStack = player.getItemInHand(hand);
                if(itemStack.is(Items.ENDER_EYE) || (itemStack.is(MirthdewEncoreItems.SLUMBERING_EYE) && (SlumberingEyeItem.eyeHasDestination(itemStack)))) {
                    if (!world.isClientSide) {
                        player.level().playSound(null, player, SoundEvents.END_PORTAL_FRAME_FILL, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.BLOCK_PLACE, player);

                        ItemStack newStack = itemStack.copyWithCount(1);
                        itemStack.consume(1, player);
                        slumberSocketBlockEntity.setHeldItem(newStack);
                        if(newStack.is(MirthdewEncoreItems.SLUMBERING_EYE)) {
                            world.setBlockAndUpdate(pos, state.setValue(SlumbersocketBlock.DREAMING, true));
                        }
                        if(world instanceof ServerLevel serverWorld) {
                            slumberSocketBlockEntity.markForUpdate(serverWorld);
                        }

                        return ItemInteractionResult.SUCCESS;
                    } else {
                        slumberSocketBlockEntity.setDefaultLookTarget(state);
                    }

                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SlumbersocketBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, SlumbersocketBlockEntity::tick);
    }
}
