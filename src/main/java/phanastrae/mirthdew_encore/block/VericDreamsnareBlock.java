package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.block.entity.VericDreamsnareBlockEntity;

import static net.minecraft.entity.LivingEntity.getSlotForHand;

public class VericDreamsnareBlock extends BlockWithEntity implements Waterloggable {
    public static final MapCodec<VericDreamsnareBlock> CODEC = createCodec(VericDreamsnareBlock::new);
    public static final DirectionProperty FACING = Properties.FACING;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    protected static final VoxelShape[] SHAPES = getShapes();
    protected static final VoxelShape[] SHAPES_WITH_ITEM = getShapesWithItem();

    private static VoxelShape[] getShapes() {
        VoxelShape[] shapes = new VoxelShape[6];
        shapes[0] = Block.createCuboidShape(1.0, 13.0, 1.0, 15.0, 16.0, 15.0);
        shapes[1] = Block.createCuboidShape(1.0, 0.0, 1.0, 15.0, 3.0, 15.0);
        shapes[2] = Block.createCuboidShape(1.0, 1.0, 13.0, 15.0, 15.0, 16.0);
        shapes[3] = Block.createCuboidShape(1.0, 1.0, 0.0, 15.0, 15.0, 3.0);
        shapes[4] = Block.createCuboidShape(13.0, 1.0, 1.0, 16.0, 15.0, 15.0);
        shapes[5] = Block.createCuboidShape(0.0, 1.0, 1.0, 3.0, 15.0, 15.0);
        return shapes;
    }

    private static VoxelShape[] getShapesWithItem() {
        VoxelShape ITEM_SHAPE = Block.createCuboidShape(2.0, 2.0, 2.0, 14.0, 14.0, 14.0);
        VoxelShape[] shapes = new VoxelShape[6];
        for(int i = 0; i < 6; i++) {
            shapes[i] = VoxelShapes.combine(SHAPES[i], ITEM_SHAPE, BooleanBiFunction.OR);
        }
        return shapes;
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    protected VericDreamsnareBlock(Settings settings) {
        super(settings);
        this.setDefaultState(
                this.getDefaultState()
                        .with(Properties.FACING, Direction.UP)
                        .with(WATERLOGGED, Boolean.FALSE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
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
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockPos blockPos = pos.offset(direction.getOpposite());
        return world.getBlockState(blockPos).isSideSolidFullSquare(world, blockPos, direction);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos blockPos = ctx.getBlockPos();
        FluidState fluidState = ctx.getWorld().getFluidState(blockPos);
        return this.getDefaultState().with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER).with(FACING, ctx.getSide());
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        Direction stateDirection = state.get(FACING);
        return stateDirection.getOpposite() == direction && !state.canPlaceAt(world, pos)
                ? Blocks.AIR.getDefaultState()
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if(world.getBlockEntity(pos) instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
            if(vericDreamsnareBlockEntity.isHoldingItem()) {
                Direction direction = state.get(FACING);
                return SHAPES_WITH_ITEM[direction.getId()];
            }
        }
        return getCollisionShape(state, world, pos, context);
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction direction = state.get(FACING);
        return SHAPES[direction.getId()];
    }

    @Override
    protected void onStacksDropped(BlockState state, ServerWorld world, BlockPos pos, ItemStack tool, boolean dropExperience) {
        super.onStacksDropped(state, world, pos, tool, dropExperience);
        if (dropExperience) {
            this.dropExperienceWhenMined(world, pos, tool, ConstantIntProvider.create(5));
        }
    }

    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
                ItemScatterer.spawn(world, pos, vericDreamsnareBlockEntity.getItems());
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity) {
            if(vericDreamsnareBlockEntity.isHoldingItem()) {
                ItemStack itemStack = player.getStackInHand(hand);
                if(itemStack.isOf(Items.SHEARS)) {
                    if (!world.isClient) {
                        player.getWorld().playSoundFromEntity(null, player, SoundEvents.ENTITY_BOGGED_SHEAR, SoundCategory.PLAYERS, 1.0F, 1.0F);
                        player.emitGameEvent(GameEvent.SHEAR, player);
                        itemStack.damage(1, player, getSlotForHand(hand));
                        ItemScatterer.spawn(world, pos, vericDreamsnareBlockEntity.getItems());

                        return ItemActionResult.SUCCESS;
                    }

                    return ItemActionResult.CONSUME;
                }
            }
        }

        return ItemActionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if(blockEntity instanceof VericDreamsnareBlockEntity vericDreamsnareBlockEntity && vericDreamsnareBlockEntity.isHoldingItem()) {

            Direction direction = state.get(FACING);
            Vec3d snareBaseOffset = vericDreamsnareBlockEntity.getBaseOffset();

            for(int i = 0; i < 12; i++) {
                world.addParticle(
                        ParticleTypes.ENCHANT,
                        pos.getX() + snareBaseOffset.x + (random.nextFloat() - 0.5) * 0.6,
                        pos.getY() + snareBaseOffset.y + (random.nextFloat() - 0.5) * 0.6,
                        pos.getZ() + snareBaseOffset.z + (random.nextFloat() - 0.5) * 0.6,
                        direction.getOffsetX() * 0.8 + 0.2 * (random.nextFloat() - 0.5),
                        direction.getOffsetY() * 0.8 + 0.2 * (random.nextFloat() - 0.5),
                        direction.getOffsetZ() * 0.8 + 0.2 * (random.nextFloat() - 0.5)
                );
            }
        }
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new VericDreamsnareBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return BlockWithEntity.validateTicker(
                type,
                MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE,
                VericDreamsnareBlockEntity::tick
        );
    }
}
