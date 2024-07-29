package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.VericDreamsnareBlockEntity;

import static net.minecraft.world.entity.LivingEntity.getSlotForHand;

public class VericDreamsnareBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<VericDreamsnareBlock> CODEC = simpleCodec(VericDreamsnareBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected static final VoxelShape[] SHAPES = getShapes();
    protected static final VoxelShape[] SHAPES_WITH_ITEM = getShapesWithItem();

    private static VoxelShape[] getShapes() {
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[0] = Block.box(1.0, 13.0, 1.0, 15.0, 16.0, 15.0);
        shapes[1] = Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0);
        shapes[2] = Block.box(1.0, 1.0, 13.0, 15.0, 15.0, 16.0);
        shapes[3] = Block.box(1.0, 1.0, 0.0, 15.0, 15.0, 3.0);
        shapes[4] = Block.box(13.0, 1.0, 1.0, 16.0, 15.0, 15.0);
        shapes[5] = Block.box(0.0, 1.0, 1.0, 3.0, 15.0, 15.0);
        return shapes;
    }

    private static VoxelShape[] getShapesWithItem() {
        VoxelShape ITEM_SHAPE = Block.box(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);
        VoxelShape[] shapes = new VoxelShape[6];
        for(int i = 0; i < 6; i++) {
            shapes[i] = Shapes.joinUnoptimized(SHAPES[i], ITEM_SHAPE, BooleanOp.OR);
        }
        return shapes;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    protected VericDreamsnareBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(
                this.defaultBlockState()
                        .setValue(BlockStateProperties.FACING, Direction.UP)
                        .setValue(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction direction = state.getValue(FACING);
        BlockPos blockPos = pos.relative(direction.getOpposite());
        return world.getBlockState(blockPos).isFaceSturdy(world, blockPos, direction);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        FluidState fluidState = ctx.getLevel().getFluidState(blockPos);
        return this.defaultBlockState().setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER).setValue(FACING, ctx.getClickedFace());
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        Direction stateDirection = state.getValue(FACING);
        return stateDirection.getOpposite() == direction && !state.canSurvive(world, pos)
                ? Blocks.AIR.defaultBlockState()
                : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if(world.getBlockEntity(pos) instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
            if(vericDreamsnareBlockEntity.isHoldingItem()) {
                Direction direction = state.getValue(FACING);
                return SHAPES_WITH_ITEM[direction.get3DDataValue()];
            }
        }
        return getCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);
        return SHAPES[direction.get3DDataValue()];
    }

    @Override
    protected void spawnAfterBreak(BlockState state, ServerLevel world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.spawnAfterBreak(state, world, pos, tool, dropExperience);
        if (dropExperience) {
            this.tryDropExperience(world, pos, tool, ConstantInt.of(5));
        }
    }

    @Override
    protected void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
                Containers.dropContents(world, pos, vericDreamsnareBlockEntity.getItems());
            }

            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
            if(vericDreamsnareBlockEntity.isHoldingItem()) {
                ItemStack itemStack = player.getItemInHand(hand);
                if(itemStack.is(Items.SHEARS)) {
                    if (!world.isClientSide) {
                        player.level().playSound(null, player, SoundEvents.BOGGED_SHEAR, SoundSource.PLAYERS, 1.0F, 1.0F);
                        player.gameEvent(GameEvent.SHEAR, player);
                        itemStack.hurtAndBreak(1, player, getSlotForHand(hand));
                        Containers.dropContents(world, pos, vericDreamsnareBlockEntity.getItems());

                        return ItemInteractionResult.SUCCESS;
                    }

                    return ItemInteractionResult.CONSUME;
                }
            }
        }

        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity && vericDreamsnareBlockEntity.isHoldingItem()) {

            Direction direction = state.getValue(FACING);
            Vec3 snareBaseOffset = vericDreamsnareBlockEntity.getBaseOffset();

            for(int i = 0; i < 12; i++) {
                world.addParticle(
                        ParticleTypes.ENCHANT,
                        pos.getX() + snareBaseOffset.x + (random.nextFloat() - 0.5) * 0.6,
                        pos.getY() + snareBaseOffset.y + (random.nextFloat() - 0.5) * 0.6,
                        pos.getZ() + snareBaseOffset.z + (random.nextFloat() - 0.5) * 0.6,
                        direction.getStepX() * 0.8 + 0.2 * (random.nextFloat() - 0.5),
                        direction.getStepY() * 0.8 + 0.2 * (random.nextFloat() - 0.5),
                        direction.getStepZ() * 0.8 + 0.2 * (random.nextFloat() - 0.5)
                );
            }
        }
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new VericDreamsnareBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return BaseEntityBlock.createTickerHelper(
                type,
                MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE,
                VericDreamsnareBlockEntity::tick
        );
    }
}
