package phanastrae.mirthdew_encore.client.dreamtwirl;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlDebug;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.Optional;

public class DreamtwirlDebugRenderer {

    public static void tryRender(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ) {
        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.getDebugOverlay().showDebugScreen()) {
            LocalPlayer player = minecraft.player;
            if(player != null) {
                Level level = player.level();
                // TODO currently this renders in all levels, and not just the dreamtwirl dim
                if(level != null) {
                    RegionPos regionPos = RegionPos.fromEntity(player);
                    Optional<DreamtwirlDebug.DebugInfo> debugInfoOptional = DreamtwirlDebug.getInstance().getDebugInfo(regionPos.id);
                    if(debugInfoOptional.isPresent()) {
                        DreamtwirlDebug.DebugInfo debugInfo = debugInfoOptional.get();
                        render(poseStack, bufferSource, camX, camY, camZ, player, debugInfo);
                    }
                }
            }
        }
    }

    public static void render(PoseStack poseStack, MultiBufferSource.BufferSource bufferSource, double camX, double camY, double camZ, Player player, DreamtwirlDebug.DebugInfo debugInfo) {
        Vec3 playerPos = player.position();

        for(DreamtwirlDebug.DebugNode node : debugInfo.getNodes()) {
            BlockPos pos = node.pos();
            Vec3 pv3 = pos.getCenter();
            double distSqr = pv3.distanceToSqr(playerPos);
            if(distSqr < 16 * 16) {
                DebugRenderer.renderFloatingText(poseStack, bufferSource, node.doorType().getSerializedName(), pos.getX(), pos.getY(), pos.getZ(), 0xFFFFFFFF);
            }
        }

        VertexConsumer lines = bufferSource.getBuffer(RenderType.lines());
        PoseStack.Pose pose = poseStack.last();
        for(DreamtwirlDebug.DebugEdge edge : debugInfo.getEdges()) {
            DreamtwirlDebug.DebugNode start = debugInfo.getNodeOfId(edge.startId());
            DreamtwirlDebug.DebugNode end = debugInfo.getNodeOfId(edge.endId());

            if(start != null && end != null) {
                Vec3 startPos = start.pos().getCenter();
                Vec3 endPos = end.pos().getCenter();
                Vec3 dif = endPos.subtract(startPos).normalize();

                drawLineVertex(pose, lines, startPos, dif, camX, camY, camZ, start.doorType());
                drawLineVertex(pose, lines, endPos, dif, camX, camY, camZ, end.doorType());
            }
        }
    }

    public static void drawLineVertex(PoseStack.Pose pose, VertexConsumer lines, Vec3 pos, Vec3 normal, double camX, double camY, double camZ, RoomDoor.DoorType doorType) {
        float r;
        float g;
        float b;
        float a;
        switch(doorType) {
            case ENTRANCE_ONLY -> {
                r = 0.25F;
                g = 1F;
                b = 0.25F;
                a = 1F;
            }
            case EXIT_ONLY -> {
                r = 1F;
                g = 0.5F;
                b = 0.25F;
                a = 1F;
            }
            default -> {
                r = 0.25F;
                g = 1F;
                b = 1F;
                a = 1F;
            }
        }
        drawLineVertex(pose, lines, pos, normal, camX, camY, camZ, r, g, b, a);
    }

    public static void drawLineVertex(PoseStack.Pose pose, VertexConsumer lines, Vec3 pos, Vec3 normal, double camX, double camY, double camZ, float r, float g, float b, float a) {
        lines.addVertex(pose, (float)(pos.x - camX), (float)(pos.y - camY), (float)(pos.z - camZ))
                .setColor(r, g, b, a)
                .setNormal(pose, (float)normal.x, (float)normal.y, (float)normal.z);
    }
}
