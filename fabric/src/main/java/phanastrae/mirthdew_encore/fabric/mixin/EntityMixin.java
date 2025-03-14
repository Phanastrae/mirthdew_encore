package phanastrae.mirthdew_encore.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.fabric.duck.FabricEntityDuckInterface;
import phanastrae.mirthdew_encore.fluid.MirthdewEncoreFluids;

@Mixin(Entity.class)
public abstract class EntityMixin implements FabricEntityDuckInterface {

    @Shadow public abstract boolean updateFluidHeightAndDoFluidPushing(TagKey<Fluid> fluidTag, double motionScale);
    @Shadow public float fallDistance;
    @Shadow public abstract void clearFire();

    @Unique
    private boolean mirthdewEncore$forceInWaterTrue = false;

    @Inject(method = "isInWater", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$treatFluidsAsWater(CallbackInfoReturnable<Boolean> cir) {
        if(this.mirthdewEncore$forceInWaterTrue) {
            cir.setReturnValue(true);
            this.mirthdewEncore$forceInWaterTrue = false;
        }
    }

    @Inject(method = "updateInWaterStateAndDoFluidPushing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isInWater()Z"))
    private void mirthdewEncore$updateFluidHeights(CallbackInfoReturnable<Boolean> cir, @Local(ordinal = 0) LocalBooleanRef LBR_bl) {
        MirthdewEncoreFluids.forEachXPGF(xpgf -> {
            boolean touchingFluid = this.updateFluidHeightAndDoFluidPushing(xpgf.getFluidTag(), xpgf.getMotionScale());
            if(touchingFluid) {
                LBR_bl.set(true);

                this.fallDistance *= xpgf.getFallDistanceModifier();
                if(xpgf.canExtinguish()) {
                    this.clearFire();
                }
            }
        });
    }

    @Override
    public void mirthdewEncore$setForceInWaterTrue(boolean value) {
        this.mirthdewEncore$forceInWaterTrue = value;
    }
}
