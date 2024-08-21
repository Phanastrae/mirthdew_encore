package phanastrae.mirthdew_encore.client.render.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlBorder;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlWorldAttachment;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.Objects;

public class DreamtwirlBorderRenderer {

    @Nullable
    private static RenderTarget framebuffer;
    private static int fbWidth;
    private static int fbHeight;

    public static void render(Matrix4f positionMatrix, ClientLevel clientWorld, Camera camera) {
        if(!clientWorld.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            return;
        }

        PoseStack matrices = new PoseStack();
        matrices.mulPose(positionMatrix);

        RegionPos regionPos = getRegionPosFromEntityOrElseCamera(camera);

        double camX = camera.getPosition().x;
        double camY = camera.getPosition().y;
        double camZ = camera.getPosition().z;

        int centerX = regionPos.getCenterX();
        int centerY = (Mth.floor(camY) >> 4) << 4;
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

        RenderSystem.setShader(GameRenderer::getPositionShader);
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        matrices.pushPose();
        matrices.translate(centerX - camX, centerY - camY, centerZ - camZ);
        Matrix4f matrix4f = matrices.last().pose();

        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);

        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        matrices.popPose();

        RenderSystem.colorMask(true, true, true, true);

        // render border

        minX = border.minX - centerX + 1/32F;
        maxX = border.maxX - centerX - 1/32F;
        minZ = border.minZ - centerZ + 1/32F;
        maxZ = border.maxZ - centerZ - 1/32F;

        Minecraft client = Minecraft.getInstance();
        RenderTarget clientFramebuffer = client.getMainRenderTarget();
        int width = clientFramebuffer.viewWidth;
        int height = clientFramebuffer.viewHeight;
        if(framebuffer == null) {
            framebuffer = new TextureTarget(width, height, true, Minecraft.ON_OSX);
            fbWidth = width;
            fbHeight = height;
        } else if(width != fbWidth || height != fbHeight) {
            framebuffer.resize(width, height, Minecraft.ON_OSX);
            fbWidth = width;
            fbHeight = height;
        }

        framebuffer.setClearColor(0F, 0F, 0F, 0F);
        framebuffer.clear(Minecraft.ON_OSX);

        framebuffer.bindWrite(false);
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.viewport(0, 0, width, height);
        RenderSystem.disableBlend();

        ShaderInstance shaderProgram = Objects.requireNonNull(client.gameRenderer.blitShader, "Blit shader not loaded");
        shaderProgram.setSampler("DiffuseSampler", clientFramebuffer.getColorTextureId());
        shaderProgram.apply();
        bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());
        shaderProgram.clear();

        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        clientFramebuffer.bindWrite(false);



        RenderSystem.setShader(MirthdewEncoreShaders::getDreamtwirlBarrierShader);
        bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        RenderSystem.setShaderTexture(0, framebuffer.getColorTextureId());

        matrices.pushPose();
        matrices.translate(centerX - camX, centerY - camY, centerZ - camZ);
        matrix4f = matrices.last().pose();

        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);

        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);


        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);

        bufferBuilder.addVertex(matrix4f, minX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, minX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, minZ);
        bufferBuilder.addVertex(matrix4f, minX, minY, maxZ);

        bufferBuilder.addVertex(matrix4f, maxX, maxY, minZ);
        bufferBuilder.addVertex(matrix4f, maxX, maxY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, maxZ);
        bufferBuilder.addVertex(matrix4f, maxX, minY, minZ);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        matrices.popPose();

        RenderSystem.disableDepthTest();
    }

    public static void close() {
        if(framebuffer != null) {
            framebuffer.destroyBuffers();
            fbWidth = 0;
            fbHeight = 0;
        }
    }

    public static Vec3 getVec3dFromEntityOrElseDefault(Vec3 cameraPos) {
        Entity entity = Minecraft.getInstance().getCameraEntity();
        return entity == null ? cameraPos : entity.position();
    }

    public static RegionPos getRegionPosFromEntityOrElseVec3d(Vec3 cameraPos) {
        return RegionPos.fromVec3d(getVec3dFromEntityOrElseDefault(cameraPos));
    }

    public static RegionPos getRegionPosFromEntityOrElseCamera(Camera camera) {
        return getRegionPosFromEntityOrElseVec3d(camera.getPosition());
    }

    public static void hideChunks(ObjectArrayList<SectionRenderDispatcher.RenderSection> builtChunks, Level world, int cameraChunkX, int cameraChunkZ) {
        if(world == null) {
            return;
        }
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) {
            return;
        }

        Vec3 cameraPos = new Vec3(cameraChunkX << 4, 0, cameraChunkZ << 4);
        RegionPos cameraRegionPos = getRegionPosFromEntityOrElseVec3d(cameraPos);

        builtChunks.removeIf(builtChunk -> {
            RegionPos chunkRegionPos = RegionPos.fromBlockPos(builtChunk.getOrigin());
            return !chunkRegionPos.equals(cameraRegionPos);
        });
    }

    public static boolean shouldHideEntity(Entity entity, double cameraX, double cameraY, double cameraZ) {
        Level world = entity.level();
        if(world == null) {
            return false;
        }
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) {
            return false;
        }

        RegionPos currentRegion = getRegionPosFromEntityOrElseVec3d(new Vec3(cameraX, cameraY, cameraZ));
        RegionPos entityRegion = RegionPos.fromEntity(entity);
        return !currentRegion.equals(entityRegion);
    }
}
