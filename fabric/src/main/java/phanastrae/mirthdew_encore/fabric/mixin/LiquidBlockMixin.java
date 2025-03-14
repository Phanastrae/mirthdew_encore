package phanastrae.mirthdew_encore.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

import static net.minecraft.world.level.block.LiquidBlock.POSSIBLE_FLOW_DIRECTIONS;

@Mixin(LiquidBlock.class)
public abstract class LiquidBlockMixin {

    @Shadow protected abstract void fizz(LevelAccessor level, BlockPos pos);
    @Shadow @Final protected FlowingFluid fluid;

    @Inject(method = "shouldSpreadLiquid", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;getFluidState(Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/level/material/FluidState;", ordinal = 0), cancellable = true)
    private void mirthdewEncore$lavaInteractions(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 1) BlockPos blockpos) {
        // Lava + Vesperbile = Obsidian (Source Lava) / Scarabrim (Flowing Lava)
        if(level.getFluidState(blockpos).is(MirthdewEncoreFluidTags.VESPERBILE)) {
            Block block = level.getFluidState(pos).isSource() ? Blocks.OBSIDIAN : MirthdewEncoreBlocks.SCARABRIM;
            level.setBlockAndUpdate(pos, block.defaultBlockState());
            this.fizz(level, pos);
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldSpreadLiquid", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$otherFluidInteractions(Level level, BlockPos pos, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (this.fluid.is(MirthdewEncoreFluidTags.VESPERBILE)) {
            for (Direction direction : POSSIBLE_FLOW_DIRECTIONS) {
                BlockPos blockpos = pos.relative(direction.getOpposite());
                // Vesperbile + Water = Sunslaked Chalktissue
                if (level.getFluidState(blockpos).is(FluidTags.WATER)) {
                    Block block = MirthdewEncoreBlocks.SUNSLAKED_CHALKTISSUE;
                    level.setBlockAndUpdate(pos, block.defaultBlockState());
                    this.fizz(level, pos);
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
