package phanastrae.mirthdew_encore.client.render.world;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlLevelAttachment;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.Objects;

public class DreamtwirlBorderRenderer {
    @Nullable
    private static RenderTarget framebuffer;
    private static int fbWidth;
    private static int fbHeight;

    public static void render(Matrix4f positionMatrix, ClientLevel clientLevel, Camera camera) {
        // only render border inside dreamtwirl
        if(clientLevel.dimensionTypeRegistration().is(MirthdewEncoreDimensions.DREAMTWIRL_DIM_TYPE)) {
            // calculate offsets
            RegionPos regionPos = getRegionPosFromEntityOrElseCamera(camera);
            int minBuildHeight = clientLevel.getMinBuildHeight();
            int maxBuildHeight = clientLevel.getMaxBuildHeight();

            int centerX = regionPos.getCenterX();
            int centerY = (minBuildHeight + maxBuildHeight) / 2;
            int centerZ = regionPos.getCenterZ();

            Vec3 camPos = camera.getPosition();
            float offsetX = (float)(centerX - camPos.x);
            float offsetY = (float)(centerY - camPos.y);
            float offsetZ = (float)(centerZ - camPos.z);

            // render border
            renderDreamtwirlBorder(positionMatrix, offsetX, offsetY, offsetZ);
        }
    }

    public static void renderDreamtwirlBorder(Matrix4f positionMatrix, float offsetX, float offsetY, float offsetZ) {
        // setup matrices
        Matrix4fStack modelViewStack = RenderSystem.getModelViewStack();
        modelViewStack.pushMatrix();
        modelViewStack.identity();
        modelViewStack.mul(positionMatrix);

        // setup matrices for outer border
        modelViewStack.pushMatrix();
        modelViewStack.translate(offsetX, 0, offsetZ);
        RenderSystem.applyModelViewMatrix();

        // render outer border
        renderOuterDepthBorder();

        // setup matrices for inner border
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();

        // setup framebuffer
        copyScreenToFramebuffer();

        // render inner border
        renderInnerGlowyBorder(offsetX, offsetY, offsetZ);

        // restore matrices
        modelViewStack.popMatrix();
        RenderSystem.applyModelViewMatrix();
    }

    public static void renderOuterDepthBorder() {
        RenderSystem.setShader(GameRenderer::getPositionShader);

        RenderSystem.enableDepthTest();
        RenderSystem.colorMask(false, false, false, false);

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        drawBorderQuads(bufferBuilder, 256, 1024);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.colorMask(true, true, true, true);
        RenderSystem.disableDepthTest();
    }

    public static void copyScreenToFramebuffer() {
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

        ShaderInstance shaderInstance = Objects.requireNonNull(client.gameRenderer.blitShader, "Blit shader not loaded");
        shaderInstance.setSampler("DiffuseSampler", clientFramebuffer.getColorTextureId());
        shaderInstance.apply();

        framebuffer.bindWrite(false);
        RenderSystem.depthMask(false);
        RenderSystem.disableBlend();

        BufferBuilder bufferBuilder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLIT_SCREEN);
        bufferBuilder.addVertex(0.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 0.0F, 0.0F);
        bufferBuilder.addVertex(1.0F, 1.0F, 0.0F);
        bufferBuilder.addVertex(0.0F, 1.0F, 0.0F);
        BufferUploader.draw(bufferBuilder.buildOrThrow());

        RenderSystem.enableBlend();
        RenderSystem.depthMask(true);
        clientFramebuffer.bindWrite(false);

        shaderInstance.clear();
    }

    public static void renderInnerGlowyBorder(float offsetX, float offsetY, float offsetZ) {
        RenderSystem.setShader(MirthdewEncoreShaders::getDreamtwirlBarrierShader);
        ShaderInstance shaderInstance = RenderSystem.getShader();
        if(shaderInstance == null || framebuffer == null) {
            return;
        }

        Uniform uniform = shaderInstance.CHUNK_OFFSET;
        RenderSystem.setShaderTexture(0, framebuffer.getColorTextureId());

        if (uniform != null) {
            uniform.set(offsetX, offsetY, offsetZ);
        }

        RenderSystem.enableDepthTest();

        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);
        drawBorderQuads(bufferBuilder, 240 + 1/32F, 2048);
        drawBorderQuads(bufferBuilder, 240 + 1/32F, -2048);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.disableDepthTest();

        if (uniform != null) {
            uniform.set(0.0F, 0.0F, 0.0F);
        }

        shaderInstance.clear();
    }

    public static void drawBorderQuads(BufferBuilder bufferBuilder, float horizontalRadius, float verticalRadius) {
        // +x
        drawBorderQuad(bufferBuilder,
                horizontalRadius, horizontalRadius,
                verticalRadius,
                -horizontalRadius, horizontalRadius);
        // -x
        drawBorderQuad(bufferBuilder,
                -horizontalRadius, -horizontalRadius,
                verticalRadius,
                horizontalRadius, -horizontalRadius);
        // +z
        drawBorderQuad(bufferBuilder,
                horizontalRadius, -horizontalRadius,
                verticalRadius,
                horizontalRadius, horizontalRadius);
        // -z
        drawBorderQuad(bufferBuilder,
                -horizontalRadius, horizontalRadius,
                verticalRadius,
                -horizontalRadius, -horizontalRadius);
    }

    public static void drawBorderQuad(BufferBuilder bufferBuilder, float x1, float x2, float y, float z1, float z2) {
        bufferBuilder.addVertex(x1,  y, z1);
        bufferBuilder.addVertex(x1, -y, z1);
        bufferBuilder.addVertex(x2, -y, z2);
        bufferBuilder.addVertex(x2,  y, z2);
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
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
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
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(world);
        if(DTWA == null) {
            return false;
        }

        RegionPos currentRegion = getRegionPosFromEntityOrElseVec3d(new Vec3(cameraX, cameraY, cameraZ));
        RegionPos entityRegion = RegionPos.fromEntity(entity);
        return !currentRegion.equals(entityRegion);
    }
}
