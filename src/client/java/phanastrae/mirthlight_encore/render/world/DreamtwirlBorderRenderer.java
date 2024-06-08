package phanastrae.mirthlight_encore.render.world;

import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.SimpleFramebuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthlight_encore.render.shader.MirthlightEncoreShaders;
import phanastrae.mirthlight_encore.util.RegionPos;
import phanastrae.mirthlight_encore.dreamtwirl.DreamtwirlBorder;
import phanastrae.mirthlight_encore.world.dimension.MirthlightEncoreDimensions;

import java.util.Objects;

public class DreamtwirlBorderRenderer {

    @Nullable
    private static Framebuffer framebuffer;
    private static int fbWidth;
    private static int fbHeight;

    public static void render(ClientWorld clientWorld, Camera camera, MatrixStack matrices) {
        if(!clientWorld.getDimensionEntry().matchesKey(MirthlightEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            return;
        }

        RegionPos regionPos = getRegionPosFromEntityOrElseCamera(camera);

        double camX = camera.getPos().x;
        double camY = camera.getPos().y;
        double camZ = camera.getPos().z;

        int centerX = regionPos.getCenterX();
        int centerY = (MathHelper.floor(camY) >> 4) << 4;
        int centerZ = regionPos.getCenterZ();

        DreamtwirlBorder border = new DreamtwirlBorder(regionPos);

        float minY = -1024 - centerY;
        float maxY = 1024 - centerY;

        float minX = border.minX - centerX - 16;
        float maxX = border.maxX - centerX + 16;
        float minZ = border.minZ - centerZ - 16;
        float maxZ = border.maxZ - centerZ + 16;

        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);

        RenderSystem.setShader(GameRenderer::getPositionProgram);
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

        matrices.push();
        matrices.translate(centerX - camX, centerY - camY, centerZ - camZ);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();

        bufferBuilder.vertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ);

        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ);

        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ);

        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        matrices.pop();

        RenderSystem.colorMask(true, true, true, true);

        // render border

        minX += 16;
        maxX -= 16;
        minZ += 16;
        maxZ -= 16;

        MinecraftClient client = MinecraftClient.getInstance();
        Framebuffer clientFramebuffer = client.getFramebuffer();
        int width = clientFramebuffer.viewportWidth;
        int height = clientFramebuffer.viewportHeight;
        if(framebuffer == null) {
            framebuffer = new SimpleFramebuffer(width, height, true, MinecraftClient.IS_SYSTEM_MAC);
            fbWidth = width;
            fbHeight = height;
        } else {
            if(width != fbWidth || height != fbHeight) {
                framebuffer.resize(fbWidth, fbHeight, MinecraftClient.IS_SYSTEM_MAC);
                fbWidth = width;
                fbHeight = height;
            }
        }

        framebuffer.setClearColor(0F, 0F, 0F, 0F);
        framebuffer.clear(MinecraftClient.IS_SYSTEM_MAC);

        framebuffer.beginWrite(false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.viewport(0, 0, width, height);
        RenderSystem.disableBlend();

        ShaderProgram shaderProgram = Objects.requireNonNull(client.gameRenderer.blitScreenProgram, "Blit shader not loaded");
        shaderProgram.addSampler("DiffuseSampler", clientFramebuffer.getColorAttachment());
        shaderProgram.bind();
        bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.DrawMode.QUADS, VertexFormats.BLIT_SCREEN);
        bufferBuilder.vertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.vertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.vertex(0.0F, 1.0F, 0.0F);
        BufferRenderer.draw(bufferBuilder.end());
        shaderProgram.unbind();

        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        clientFramebuffer.beginWrite(false);



        RenderSystem.setShader(MirthlightEncoreShaders::getDreamtwirlBarrierShader);
        bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);
        RenderSystem.setShaderTexture(0, framebuffer.getColorAttachment());

        matrices.push();
        matrices.translate(centerX - camX, centerY - camY, centerZ - camZ);
        matrix4f = matrices.peek().getPositionMatrix();

        bufferBuilder.vertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ);

        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ);

        bufferBuilder.vertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, minX, minY, minZ);
        bufferBuilder.vertex(matrix4f, minX, maxY, minZ);

        bufferBuilder.vertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.vertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.vertex(matrix4f, maxX, maxY, maxZ);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        matrices.pop();

        RenderSystem.disableDepthTest();
    }

    public static void close() {
        if(framebuffer != null) {
            framebuffer.delete();
        }
    }

    public static Vec3d getVec3dFromEntityOrElseDefault(Vec3d cameraPos) {
        Entity entity = MinecraftClient.getInstance().getCameraEntity();
        return entity == null ? cameraPos : entity.getPos();
    }

    public static RegionPos getRegionPosFromEntityOrElseVec3d(Vec3d cameraPos) {
        return RegionPos.fromVec3d(getVec3dFromEntityOrElseDefault(cameraPos));
    }

    public static RegionPos getRegionPosFromEntityOrElseCamera(Camera camera) {
        return getRegionPosFromEntityOrElseVec3d(camera.getPos());
    }

    public static void hideChunks(ObjectArrayList<ChunkBuilder.BuiltChunk> builtChunks, World world, int cameraChunkX, int cameraChunkZ) {
        if(world == null) {
            return;
        }
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) {
            return;
        }

        Vec3d cameraPos = new Vec3d(cameraChunkX << 4, 0, cameraChunkZ << 4);
        RegionPos cameraRegionPos = getRegionPosFromEntityOrElseVec3d(cameraPos);

        builtChunks.removeIf(builtChunk -> {
            RegionPos chunkRegionPos = RegionPos.fromBlockPos(builtChunk.getOrigin());
            return !chunkRegionPos.equals(cameraRegionPos);
        });
    }

    public static boolean shouldHideEntity(Entity entity, double cameraX, double cameraY, double cameraZ) {
        RegionPos currentRegion = getRegionPosFromEntityOrElseVec3d(new Vec3d(cameraX, cameraY, cameraZ));
        RegionPos entityRegion = RegionPos.fromEntity(entity);
        return !currentRegion.equals(entityRegion);
    }
}
