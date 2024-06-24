package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.SlumbersocketBlockEntity;
import phanastrae.mirthdew_encore.component.MirthdewEncoreDataComponentTypes;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;

public class SlumbersocketBlock extends BlockWithEntity {
    public static final MapCodec<SlumbersocketBlock> CODEC = createCodec(SlumbersocketBlock::new);
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;
    public static final BooleanProperty DREAMING = BooleanProperty.of("dreaming");

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public SlumbersocketBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(DREAMING, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, DREAMING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        world.scheduleBlockTick(pos, this, 4);

        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SlumbersocketBlockEntity slumberSocketBlockEntity) {
                ItemScatterer.spawn(world, pos, slumberSocketBlockEntity.getItems());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        world.scheduleBlockTick(pos, this, 4);
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(state.get(DREAMING)) {
            BlockPos downPos = pos.down();
            BlockState downState = world.getBlockState(downPos);
            if (!downState.isOf(this) && downState.isAir()) {
                world.setBlockState(downPos,
                        MirthdewEncoreBlocks.SLUMBERVEIL.getDefaultState()
                                .with(SlumberveilBlock.DISTANCE, 0)
                                .with(SlumberveilBlock.SUPPORTING, true)
                                .with(SlumberveilBlock.AXIS, state.get(FACING).rotateYClockwise().getAxis()));
            }
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SlumbersocketBlockEntity slumberSocketBlockEntity) {
            if(!slumberSocketBlockEntity.isHoldingItem()) {
                ItemStack itemStack = player.getStackInHand(hand);
                if(itemStack.isOf(Items.ENDER_EYE) || (itemStack.isOf(MirthdewEncoreItems.SLUMBERING_EYE) && itemStack.contains(MirthdewEncoreDataComponentTypes.LOCATION_COMPONENT))) {
                    if (!world.isClient) {
                        player.getWorld().playSoundFromEntity(null, player, SoundEvents.BLOCK_END_PORTAL_FRAME_FILL, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.emitGameEvent(GameEvent.BLOCK_PLACE, player);

                        ItemStack newStack = itemStack.copyWithCount(1);
                        itemStack.decrementUnlessCreative(1, player);
                        slumberSocketBlockEntity.setHeldItem(newStack);
                        if(newStack.isOf(MirthdewEncoreItems.SLUMBERING_EYE)) {
                            world.setBlockState(pos, state.with(SlumbersocketBlock.DREAMING, true));
                        }
                        if(world instanceof ServerWorld serverWorld) {
                            slumberSocketBlockEntity.markForUpdate(serverWorld);
                        }

                        return ItemActionResult.SUCCESS;
                    } else {
                        slumberSocketBlockEntity.setDefaultLookTarget(state);
                    }

                    return ItemActionResult.CONSUME;
                }
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SlumbersocketBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, SlumbersocketBlockEntity::tick);
    }
}
