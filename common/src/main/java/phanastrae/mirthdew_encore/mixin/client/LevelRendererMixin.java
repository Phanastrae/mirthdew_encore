package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow @Final private ObjectArrayList<SectionRenderDispatcher.RenderSection> visibleSections;

    @Shadow private @Nullable ClientLevel level;

    @Shadow private int lastCameraSectionX;

    @Shadow private int lastCameraSectionZ;

    @Inject(method = "applyFrustum", at = @At("RETURN"))
    private void mirthdew_encore$hideChunks(Frustum frustum, CallbackInfo ci) {
        // hide chunks that are outside the current dreamtwirl
        DreamtwirlBorderRenderer.hideChunks(this.visibleSections, this.level, this.lastCameraSectionX, this.lastCameraSectionZ);
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void mirthdew_encore$hideEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, CallbackInfo ci) {
        // hide entities that are outside the current dreamtwirl
        if(DreamtwirlBorderRenderer.shouldHideEntity(entity, cameraX, cameraY, cameraZ)) {
            ci.cancel();
        }
    }
}
