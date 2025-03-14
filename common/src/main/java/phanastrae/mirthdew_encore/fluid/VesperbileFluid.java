package phanastrae.mirthdew_encore.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.item.MirthdewEncoreItems;
import phanastrae.mirthdew_encore.particle.MirthdewEncoreParticleTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

import java.util.Optional;

public abstract class VesperbileFluid extends FlowingFluid {

    @Override
    public Fluid getFlowing() {
        return MirthdewEncoreFluids.FLOWING_VESPERBILE;
    }

    @Override
    public Fluid getSource() {
        return MirthdewEncoreFluids.VESPERBILE;
    }

    @Override
    public Item getBucket() {
        return MirthdewEncoreItems.VESPERBILE_BUCKET;
    }

    @Override
    protected void animateTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        // TODO improve sounds / particles
        if (!state.isSource() && !state.getValue(FALLING)) {
            if (random.nextInt(180) == 0) {
                level.playLocalSound(
                        (double)pos.getX() + 0.5,
                        (double)pos.getY() + 0.5,
                        (double)pos.getZ() + 0.5,
                        SoundEvents.WATER_AMBIENT,
                        SoundSource.BLOCKS,
                        random.nextFloat() * 0.25F + 0.75F,
                        random.nextBoolean() ? random.nextFloat() * 0.2F : random.nextFloat() * 0.4F + 1.5F,
                        false
                );
            }
        } else if (random.nextInt(50) == 0) {
            level.addParticle(
                    MirthdewEncoreParticleTypes.SUNFLECK,
                    (double)pos.getX() + random.nextDouble(),
                    (double)pos.getY() + random.nextDouble(),
                    (double)pos.getZ() + random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    @Override
    protected void randomTick(Level level, BlockPos pos, FluidState state, RandomSource random) {
        // TODO some sort of block digestion mechanic maybe?
    }

    @Override
    protected boolean isRandomlyTicking() {
        // TODO enable, or remove randomTick()
        return false;
    }

    @Nullable
    @Override
    protected ParticleOptions getDripParticle() {
        return MirthdewEncoreParticleTypes.DRIPPING_VESPERBILE;
    }

    @Override
    protected void beforeDestroyingBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        BlockEntity blockentity = state.hasBlockEntity() ? level.getBlockEntity(pos) : null;
        Block.dropResources(state, level, pos, blockentity);
    }

    @Override
    public int getSlopeFindDistance(LevelReader level) {
        return 2;
    }

    @Override
    public BlockState createLegacyBlock(FluidState state) {
        return MirthdewEncoreBlocks.VESPERBILE.defaultBlockState().setValue(LiquidBlock.LEVEL, getLegacyLevel(state));
    }

    @Override
    public boolean isSame(Fluid fluid) {
        return fluid == MirthdewEncoreFluids.VESPERBILE || fluid == MirthdewEncoreFluids.FLOWING_VESPERBILE;
    }

    @Override
    public int getDropOff(LevelReader level) {
        return 1;
    }

    @Override
    public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockReader, BlockPos pos, Fluid fluid, Direction direction) {
        return direction == Direction.DOWN && !fluid.is(MirthdewEncoreFluidTags.VESPERBILE);
    }

    @Override
    public int getTickDelay(LevelReader level) {
        return 4;
    }

    @Override
    protected boolean canConvertToSource(Level level) {
        // TODO add gamerule?
        return true;
    }

    @Override
    protected void spreadTo(LevelAccessor level, BlockPos pos, BlockState blockState, Direction direction, FluidState fluidState) {
        if (direction == Direction.DOWN) {
            FluidState fluidstate = level.getFluidState(pos);
            if (this.is(MirthdewEncoreFluidTags.VESPERBILE) && fluidstate.is(FluidTags.WATER)) {
                if (blockState.getBlock() instanceof LiquidBlock) {
                    level.setBlock(pos, MirthdewEncoreBlocks.ONYXSCALE.defaultBlockState(), 3);
                }

                this.fizz(level, pos);
                return;
            }
        }

        super.spreadTo(level, pos, blockState, direction, fluidState);
    }

    private void fizz(LevelAccessor level, BlockPos pos) {
        level.levelEvent(1501, pos, 0);
    }

    @Override
    protected float getExplosionResistance() {
        return 100.0F;
    }

    @Override
    public Optional<SoundEvent> getPickupSound() {
        return Optional.of(SoundEvents.BUCKET_FILL);
    }

    public static class Flowing extends VesperbileFluid {
        @Override
        protected void createFluidStateDefinition(StateDefinition.Builder<Fluid, FluidState> builder) {
            super.createFluidStateDefinition(builder);
            builder.add(LEVEL);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) {
            return false;
        }
    }

    public static class Source extends VesperbileFluid {
        @Override
        public int getAmount(FluidState state) {
            return 8;
        }

        @Override
        public boolean isSource(FluidState state) {
            return true;
        }
    }
}
