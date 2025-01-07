package phanastrae.mirthdew_encore.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

@Mixin(ClipContext.class)
public class ClipContextMixin {

    @Shadow @Final private ClipContext.Fluid fluid;

    @Inject(method = "getFluidShape", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$treatWaterEsqueFluidsAsWater(FluidState state, BlockGetter level, BlockPos pos, CallbackInfoReturnable<VoxelShape> cir) {
        if(this.fluid == ClipContext.Fluid.WATER) {
            if(MirthdewEncoreFluids.fluidStateIsMirthdewZeroFallDamage(state)) {
                cir.setReturnValue(state.getShape(level, pos));
            }
        }
    }
}
