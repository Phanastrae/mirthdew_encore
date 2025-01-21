package phanastrae.mirthdew_encore.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlDebug;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;

@Mixin(DebugRenderer.class)
public class DebugRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void mirthdew_encore$doDebugRendering(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.getDebugOverlay().showDebugScreen()) {
            LocalPlayer player = minecraft.player;
            if(player != null) {
                Vec3 playerPos = player.position();

                RegionPos regionPos = RegionPos.fromEntity(player);
                Optional<DreamtwirlDebug.DebugInfo> debugInfoOptional = DreamtwirlDebug.getInstance().getDebugInfo(regionPos.id);
                if(debugInfoOptional.isPresent()) {
                    DreamtwirlDebug.DebugInfo debugInfo = debugInfoOptional.get();

                    debugInfo.getNodes().forEach(node -> {
                        BlockPos pos = node.pos();
                        Vec3 pv3 = pos.getCenter();
                        double distSqr = pv3.distanceToSqr(playerPos);
                        if(distSqr < 16 * 16) {
                            DebugRenderer.renderFloatingText(poseStack, bufferSource, "door", pos.getX(), pos.getY(), pos.getZ(), 0xFFFFFFFF);
                        }
                    });
                    VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
                    PoseStack.Pose pose = poseStack.last();
                    debugInfo.getEdges().forEach(edge -> {
                        DreamtwirlDebug.DebugNode start = debugInfo.getNodeOfId(edge.startId());
                        DreamtwirlDebug.DebugNode end = debugInfo.getNodeOfId(edge.endId());

                        if(start != null && end != null) {
                            Vec3 startPos = start.pos().getCenter();
                            Vec3 endPos = end.pos().getCenter();
                            Vec3 dif = endPos.subtract(startPos).normalize();

                            lines.addVertex(pose, (float)(startPos.x - camX), (float)(startPos.y - camY), (float)(startPos.z - camZ))
                                    .setColor(0.0F, 1.0F, 0.0F, 1.0F)
                                    .setNormal(pose, (float)dif.x, (float)dif.y, (float)dif.z);

                            lines.addVertex(pose, (float)(endPos.x - camX), (float)(endPos.y - camY), (float)(endPos.z - camZ))
                                    .setColor(0.0F, 0.0F, 1.0F, 1.0F)
                                    .setNormal(pose, (float)dif.x, (float)dif.y, (float)dif.z);
                        }
                    });
                }
            }
        }
    }
}
