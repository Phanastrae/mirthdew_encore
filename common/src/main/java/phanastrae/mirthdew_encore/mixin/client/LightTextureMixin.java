package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreFluidTags;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;<init>()V", ordinal = 0, shift = At.Shift.AFTER))
    private void mirthdew_encore$modifySkyLight(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 0) Vector3f skyLightRGB, @Local(ordinal=2) LocalFloatRef skyDarkenWithFlash, @Local(ordinal = 7) LocalFloatRef LR_darkVision) {
        // modify sky light
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // change sky light color to a cyan
            skyLightRGB.set(0.45f, 0.8f, 1.0f);
            skyDarkenWithFlash.set(Math.max(skyDarkenWithFlash.get(), 0.35F));
        }

        // vesperbile lighting
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        BlockPos cameraBlockPos = camera.getBlockPosition();
        BlockState cameraBlockState = clientLevel.getBlockState(cameraBlockPos);
        FluidState cameraFluidState = cameraBlockState.getFluidState();
        if(cameraFluidState.is(MirthdewEncoreFluidTags.VESPERBILE)) {
            float fluidHeight = cameraFluidState.getHeight(clientLevel, cameraBlockPos);
            double fluidY = cameraBlockPos.getY() + fluidHeight;
            double camY = camera.getPosition().y;
            if(fluidY > camY) {
                // night vision
                LR_darkVision.set(Math.max(LR_darkVision.get(), 0.85F));
                // sky light
                skyLightRGB.set(1.0F, 1.0F, 0.0F);
                skyDarkenWithFlash.set(Math.max(skyDarkenWithFlash.get(), 0.85F));
            }
        }
    }

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;set(FFF)Lorg/joml/Vector3f;", ordinal = 0, shift = At.Shift.AFTER))
    private void mirthdew_encore$afterBlocklightSet(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 1) Vector3f lightRGB) {
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // swap block light's red and blue, to make lights cyan-ish instead of orange-ish
            float r = lightRGB.z;
            float g = lightRGB.y;
            float b = lightRGB.x;

            // tweak light curves to increase block light
            float omr = 1 - r;
            float omg = 1 - g;
            float omb = 1 - b;
            r = 1 - omr*omr*omr;
            g = 1 - omg*omg*omg;
            b = 1 - omb*omb*omb;

            // add a dark blue ambient light
            r += 0.01f;
            g += 0.03f;
            b += 0.12f;

            // set values
            lightRGB.set(r, g, b);
        }
    }

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;<init>(FFF)V", ordinal = 2, shift = At.Shift.AFTER))
    private void mirthdew_encore$tweakGamma(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 4) LocalFloatRef f3) {
        // want to tweak gamma (f14) but this doesn't work on neoforge for some reason (mixin fails to get float f14 for some reason),
        // however gamma (f14) is only used once (f14 - f3), and is the only time f3 is used in or after the double-loop,
        // so can just tweak f3 instead based on gamma to simulate tweaking gamma
        // let f14' be the tweaked gamma
        // then if we take f3 -> f3 + f14 - f14'
        // then f14 - f3 -> f14 - (f3 + f14 - f14') = f14 - f3 - f14 + f14' = f14' - f3
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // tweak gamma values to bring min and max gamma closer together
            float gamma = this.minecraft.options.gamma().get().floatValue();
            float effectiveNewGamma = gamma * 0.66F;

            // take f3 -> f3 + f14 - f14'
            f3.set(f3.get() + gamma - effectiveNewGamma);
        }
    }
}
