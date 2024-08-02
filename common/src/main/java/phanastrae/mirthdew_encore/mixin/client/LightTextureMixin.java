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
            skyDarkenWithFlash.set(Math.max(skyDarkenWithFlash.get(), 0.35F));
            skyLightRGB.set(0.45f, 0.8f, 1.0f);
        }
    }

    @Inject(method = "updateLightTexture", at = @At(value = "INVOKE", target = "Lorg/joml/Vector3f;set(FFF)Lorg/joml/Vector3f;", ordinal = 0, shift = At.Shift.AFTER))
    private void mirthdew_encore$afterBlocklightSet(float partialTicks, CallbackInfo ci, @Local(ordinal=0) ClientLevel clientLevel, @Local(ordinal = 1) Vector3f lightRGB) {
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // swap block light r and b
            lightRGB.set(lightRGB.z, lightRGB.y, lightRGB.x);
            // add ambient light
            lightRGB.add(0.01f, 0.03f, 0.12f);
        }
    }
}
