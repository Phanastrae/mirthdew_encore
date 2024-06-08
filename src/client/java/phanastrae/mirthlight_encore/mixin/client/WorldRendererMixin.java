package phanastrae.mirthlight_encore.mixin.client;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthlight_encore.render.world.DreamtwirlBorderRenderer;

@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow @Final private ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks;

    @Shadow private @Nullable ClientWorld world;

    @Shadow private int cameraChunkX;

    @Shadow private int cameraChunkZ;

    @Inject(method = "applyFrustum", at = @At("RETURN"))
    private void mirthlight_encore$hideChunks(Frustum frustum, CallbackInfo ci) {
        // hide chunks that are outside the current dreamtwirl
        DreamtwirlBorderRenderer.hideChunks(this.builtChunks, this.world, this.cameraChunkX, this.cameraChunkZ);
    }

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    private void mirthlight_encore$hideEntity(Entity entity, double cameraX, double cameraY, double cameraZ, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, CallbackInfo ci) {
        // hide entities that are outside the current dreamtwirl
        if(DreamtwirlBorderRenderer.shouldHideEntity(entity, cameraX, cameraY, cameraZ)) {
            ci.cancel();
        }
    }
}
