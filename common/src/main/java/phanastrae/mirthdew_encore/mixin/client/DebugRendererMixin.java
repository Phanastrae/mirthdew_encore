package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.client.dreamtwirl.DreamtwirlDebugRenderer;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void mirthdew_encore$doDebugRendering(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, CallbackInfo ci) {
        DreamtwirlDebugRenderer.tryRender(poseStack, bufferSource, camX, camY, camZ);
    }
}
