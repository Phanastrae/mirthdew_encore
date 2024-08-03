package phanastrae.mirthdew_encore.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

@Mixin(LightTexture.class)
public class LightTextureMixin {

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;<init>()V", ordinal = 0, shift = At.Shift.AFTER))
    private void mirthdew_encore$modifySkyLight(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 0) Vector3f skyLightRGB, @Local(ordinal=2) LocalFloatRef skyDarkenWithFlash) {
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // change sky light color to a cyan
            skyLightRGB.set(0.45f, 0.8f, 1.0f);
            skyDarkenWithFlash.set(Math.max(skyDarkenWithFlash.get(), 0.35F));
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

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;<init>(FFF)V", ordinal = 4, shift = At.Shift.BEFORE))
    private void mirthdew_encore$tweakGamma(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 14) LocalFloatRef gamma) {
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // tweak gamma values to bring min and max gamma closer together
            gamma.set(gamma.get() * 0.66F);
        }
    }
}
