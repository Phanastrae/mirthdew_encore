package phanastrae.mirthdew_encore.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.*;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class DreamseedBlock extends Block {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final BooleanProperty LIT = Properties.LIT;
    protected static final VoxelShape SHAPE = VoxelShapes.combine(
            Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            Block.createCuboidShape(7.0, 9.0, 7.0, 9.0, 12.0, 9.0),
            BooleanBiFunction.OR);

    public DreamseedBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState()
                .with(WATERLOGGED, false)
                .with(LIT, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LIT);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolidFullSquare(world, pos, Direction.UP);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        FluidState fluidState = world.getFluidState(pos);
        BlockState blockState = world.getBlockState(pos);
        return this.getDefaultState()
                .with(LIT, blockState.isOf(Blocks.SOUL_FIRE))
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos
    ) {
        if(state.get(WATERLOGGED)) {
            world.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        if(state.get(LIT) && !canLight(world, state, pos)) {
            return state.with(LIT, false);
        } else {
            return state;
        }
    }

    public boolean canLight(WorldAccess world, BlockState state, BlockPos pos) {
        return world.getBlockState(pos.down()).isIn(BlockTags.SOUL_FIRE_BASE_BLOCKS) && !state.get(WATERLOGGED);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if(state.get(LIT)) {
            if (!world.isClient()) {
                world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, pos, 0);
            }
        }

        return super.onBreak(world, pos, state, player);
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.afterBreak(world, player, pos, state, blockEntity, tool);
        if (!world.isClient) {
            if (!EnchantmentHelper.hasAnyEnchantmentsIn(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
                DreamspeckEntity dreamspeckEntity = MirthdewEncoreEntityTypes.DREAM_SPECK.create(world);
                if(dreamspeckEntity != null) {
                    dreamspeckEntity.setPosition(pos.toBottomCenterPos());
                    world.spawnEntity(dreamspeckEntity);
                }
            }
        }
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(state.get(LIT)) {
            if (!entity.isFireImmune()) {
                entity.setFireTicks(entity.getFireTicks() + 1);
                if (entity.getFireTicks() == 0) {
                    entity.setOnFireFor(8.0F);
                }
            }

            entity.damage(world.getDamageSources().inFire(), 2.0F);
        }
        super.onEntityCollision(state, world, pos, entity);
    }

    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (state.get(LIT) || (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE))) {
            return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
        } else {
            world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlockState(pos, state.with(LIT,  true), Block.NOTIFY_ALL_AND_REDRAW);
            world.emitGameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            Item item = stack.getItem();
            if (stack.isOf(Items.FLINT_AND_STEEL)) {
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.decrementUnlessCreative(1, player);
            }

            player.incrementStat(Stats.USED.getOrCreateStat(item));
            return ItemActionResult.success(world.isClient);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(state.get(LIT)) {
            if (random.nextInt(24) == 0) {
                world.playSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_FIRE_AMBIENT,
                        SoundCategory.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
            if (random.nextInt(5) == 0) {
                world.playSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
                        SoundCategory.BLOCKS,
                        0.5F + random.nextFloat() * 0.2F,
                        0.3F + random.nextFloat() * 1.5F,
                        false
                );
            }

            for (int i = 0; i < 3; i++) {
                double x = pos.getX() + random.nextDouble();
                double y = pos.getY() + random.nextDouble() * 0.5 + 0.5;
                double z = pos.getZ() + random.nextDouble();
                world.addParticle(ParticleTypes.LARGE_SMOKE, x, y, z, 0.0, 0.0, 0.0);
            }

            for(int i = 0; i < 12; i++) {
                double x = pos.getX() + 0.1 + 0.8 * random.nextDouble();
                double y = pos.getY() + 1.2 * random.nextDouble();
                double z = pos.getZ() + 0.1 + 0.8 * random.nextDouble();
                world.addParticle(i < 6 ? ParticleTypes.WITCH : ParticleTypes.ENCHANT, x, y, z, 0.0, 3, 0.0);
            }
        }
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
