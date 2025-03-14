package phanastrae.mirthdew_encore.neoforge.mixin;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.neoforge.fluid.MirthdewEncoreFluidTypes;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract double getFluidTypeHeight(FluidType par1);

    @Inject(method = "getFluidHeight", at = @At("HEAD"), cancellable = true)
    private void mirthdewEncore$returnVesperbileHeight(TagKey<Fluid> fluidTag, CallbackInfoReturnable<Double> cir) {
        if(fluidTag.equals(MirthdewEncoreFluidTags.VESPERBILE)) {
            cir.setReturnValue(getFluidTypeHeight(MirthdewEncoreFluidTypes.VESPERBILE));
        }
    }
}
