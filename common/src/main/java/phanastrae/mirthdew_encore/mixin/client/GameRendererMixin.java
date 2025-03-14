package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import phanastrae.mirthdew_encore.client.fluid.MirthdewEncoreClientFluids;

@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Shadow
    @Final
    Minecraft minecraft;

    @Inject(method = "getFov", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Camera;getFluidInCamera()Lnet/minecraft/world/level/material/FogType;", shift = At.Shift.AFTER))
    private void mirthdew_encore$fluidTweakFOV(Camera activeRenderInfo, float partialTicks, boolean useFOVSetting, CallbackInfoReturnable<Double> cir, @Local(ordinal = 0) LocalDoubleRef LDR_d) {
        MirthdewEncoreClientFluids.XPlatGenericClientFluid xpgcf = MirthdewEncoreClientFluids.getXPGCF(activeRenderInfo);
        if(xpgcf != null) {
            LDR_d.set(LDR_d.get() * Mth.lerp(this.minecraft.options.fovEffectScale().get(), 1.0, xpgcf.getFovScaleFactor()));
        }
    }
}
