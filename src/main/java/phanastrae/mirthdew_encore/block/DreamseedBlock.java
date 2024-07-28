package phanastrae.mirthdew_encore.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.entity.DreamspeckEntity;
import phanastrae.mirthdew_encore.entity.MirthdewEncoreEntityTypes;

public class DreamseedBlock extends Block {
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    protected static final VoxelShape SHAPE = Shapes.joinUnoptimized(
            Block.box(4.0, 0.0, 4.0, 12.0, 9.0, 12.0),
            Block.box(7.0, 9.0, 7.0, 9.0, 12.0, 9.0),
            BooleanOp.OR);

    public DreamseedBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.defaultBlockState()
                .setValue(WATERLOGGED, false)
                .setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED, LIT);
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return world.getBlockState(pos.below()).isFaceSturdy(world, pos, Direction.UP);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        FluidState fluidState = world.getFluidState(pos);
        BlockState blockState = world.getBlockState(pos);
        return this.defaultBlockState()
                .setValue(LIT, blockState.is(Blocks.SOUL_FIRE))
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    @Override
    protected BlockState updateShape(
            BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos
    ) {
        if(state.getValue(WATERLOGGED)) {
            world.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
        }

        if(state.getValue(LIT) && !canLight(world, state, pos)) {
            return state.setValue(LIT, false);
        } else {
            return state;
        }
    }

    public boolean canLight(LevelAccessor world, BlockState state, BlockPos pos) {
        return world.getBlockState(pos.below()).is(BlockTags.SOUL_FIRE_BASE_BLOCKS) && !state.getValue(WATERLOGGED);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if(state.getValue(LIT)) {
            if (!world.isClientSide()) {
                world.levelEvent(null, LevelEvent.SOUND_EXTINGUISH_FIRE, pos, 0);
            }
        }

        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void playerDestroy(Level world, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        super.playerDestroy(world, player, pos, state, blockEntity, tool);
        if (!world.isClientSide) {
            if (!EnchantmentHelper.hasTag(tool, EnchantmentTags.PREVENTS_DECORATED_POT_SHATTERING)) {
                DreamspeckEntity dreamspeckEntity = MirthdewEncoreEntityTypes.DREAM_SPECK.create(world);
                if(dreamspeckEntity != null) {
                    dreamspeckEntity.setPos(pos.getBottomCenter());
                    world.addFreshEntity(dreamspeckEntity);
                }
            }
        }
    }

    @Override
    protected void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if(state.getValue(LIT)) {
            if (!entity.fireImmune()) {
                entity.setRemainingFireTicks(entity.getRemainingFireTicks() + 1);
                if (entity.getRemainingFireTicks() == 0) {
                    entity.igniteForSeconds(8.0F);
                }
            }

            entity.hurt(world.damageSources().inFire(), 2.0F);
        }
        super.entityInside(state, world, pos, entity);
    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (state.getValue(LIT) || (!stack.is(Items.FLINT_AND_STEEL) && !stack.is(Items.FIRE_CHARGE))) {
            return super.useItemOn(stack, state, world, pos, player, hand, hit);
        } else {
            world.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
            world.setBlock(pos, state.setValue(LIT,  true), Block.UPDATE_ALL_IMMEDIATE);
            world.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);

            Item item = stack.getItem();
            if (stack.is(Items.FLINT_AND_STEEL)) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(hand));
            } else {
                stack.consume(1, player);
            }

            player.awardStat(Stats.ITEM_USED.get(item));
            return ItemInteractionResult.sidedSuccess(world.isClientSide);
        }
    }

    @Override
    public void animateTick(BlockState state, Level world, BlockPos pos, RandomSource random) {
        if(state.getValue(LIT)) {
            if (random.nextInt(24) == 0) {
                world.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.FIRE_AMBIENT,
                        SoundSource.BLOCKS,
                        1.0F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.3F,
                        false
                );
            }
            if (random.nextInt(5) == 0) {
                world.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.AMETHYST_BLOCK_RESONATE,
                        SoundSource.BLOCKS,
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
    protected VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }
}
