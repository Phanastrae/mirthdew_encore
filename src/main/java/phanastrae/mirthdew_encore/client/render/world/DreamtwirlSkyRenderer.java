package phanastrae.mirthdew_encore.client.render.world;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import phanastrae.mirthdew_encore.MirthdewEncore;

public class DreamtwirlSkyRenderer {
    private static final ResourceLocation DREAMTWIRL_SKY_SPIRIT = MirthdewEncore.id("textures/environment/dreamtwirl_sky_spirit.png");
    private static final ResourceLocation DREAMTWIRL_NOVA = MirthdewEncore.id("textures/environment/dreamtwirl_nova.png");
    private static final ResourceLocation DREAMTWIRL_NOVA_SWIRL = MirthdewEncore.id("textures/environment/dreamtwirl_nova_swirl.png");
    private static final ResourceLocation DREAMTWIRL_LANDSCAPE_SKY_TWIRL = MirthdewEncore.id("textures/environment/dreamtwirl_landscape_sky_twirl.png");
    private static final ResourceLocation DREAMTWIRL_LANDSCAPE_CIRCUS = MirthdewEncore.id("textures/environment/dreamtwirl_landscape_circus.png");
    private static final ResourceLocation DREAMTWIRL_LANDSCAPE_BACK_MOUNTAINS = MirthdewEncore.id("textures/environment/dreamtwirl_landscape_back_mountains.png");
    private static final ResourceLocation DREAMTWIRL_LANDSCAPE_WATER = MirthdewEncore.id("textures/environment/dreamtwirl_landscape_water.png");
    private static final ResourceLocation END_SKY = ResourceLocation.withDefaultNamespace("textures/environment/end_sky.png");
    private static final int DREAMTWIRL_SKY_SPIRT_COUNT = 413;

    protected final SkySpiritData[] skySpiritDatas;
    @Nullable
    private VertexBuffer starsBuffer;
    @Nullable
    private VertexBuffer lightSkyBuffer;
    @Nullable
    private VertexBuffer cylinderBuffer;

    public DreamtwirlSkyRenderer() {
        this.skySpiritDatas = new SkySpiritData[DREAMTWIRL_SKY_SPIRT_COUNT];

        RandomSource random = RandomSource.create();
        random.setSeed(612);
        for(int i = 0; i < DREAMTWIRL_SKY_SPIRT_COUNT; i++) {
            this.skySpiritDatas[i] = SkySpiritData.create(i / (float)DREAMTWIRL_SKY_SPIRT_COUNT, random);
        }
    }

    public void close() {
        closeIfNotNull(starsBuffer);
        closeIfNotNull(lightSkyBuffer);
        closeIfNotNull(cylinderBuffer);
    }

    private void closeIfNotNull(VertexBuffer vertexBuffer) {
        if(vertexBuffer != null) {
            vertexBuffer.close();
        }
    }

    private void createStars() {
        closeIfNotNull(starsBuffer);

        this.starsBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.starsBuffer.bind();
        this.starsBuffer.upload(this.createStars(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private void createLightSky() {
        closeIfNotNull(lightSkyBuffer);

        this.lightSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.upload(createLightSky(Tesselator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private void createCylinder() {
        closeIfNotNull(cylinderBuffer);

        this.cylinderBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.cylinderBuffer.bind();
        this.cylinderBuffer.upload(createCylinder(Tesselator.getInstance()));
        VertexBuffer.unbind();
    }

    private MeshData createStars(Tesselator tessellator) {
        RandomSource random = RandomSource.create(1025);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        int STAR_COUNT = 6120;
        for(int j = 0; j < STAR_COUNT; ++j) {
            float x = random.nextFloat() * 2.0F - 1.0F;
            float y = random.nextFloat() * 2.0F - 1.0F;
            float z = random.nextFloat() * 2.0F - 1.0F;
            float m = Mth.lengthSquared(x, y, z);
            if (!(m <= 0.010000001F) && !(m >= 1.0F)) {
                Vector3f vector3f = new Vector3f(x, y, z).normalize(100.0F);
                float zAngle = (float)(random.nextDouble() * (float) Math.PI * 2.0);
                Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(zAngle);

                float phase = random.nextFloat();
                float red = 0.75F + 0.25F * (float)Math.sin(Mth.TWO_PI * phase);
                float green = 0.85F + 0.15F * (float)Math.sin(Mth.TWO_PI * (phase + 1/3F));
                float blue = 0.95F + 0.05F * (float)Math.sin(Mth.TWO_PI * (phase - 1/3F));

                float l = 0.15F + random.nextFloat() * 0.1F;
                bufferBuilder.addVertex(vector3f.add(new Vector3f(l, -l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(l, l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(-l, l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
                bufferBuilder.addVertex(vector3f.add(new Vector3f(-l, -l, 0.0F).rotate(quaternionf))).setColor(red, green, blue, 1);
            }
        }

        return bufferBuilder.buildOrThrow();
    }

    private static MeshData createLightSky(Tesselator tessellator, float f) {
        float g = Math.signum(f) * 512.0F;
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION);
        bufferBuilder.addVertex(0.0F, f, 0.0F);

        for(int i = -180; i <= 180; i += 45) {
            bufferBuilder.addVertex(g * Mth.cos((float)i * (float) (Math.PI / 180.0)), f, 512.0F * Mth.sin((float)i * (float) (Math.PI / 180.0)));
        }

        return bufferBuilder.buildOrThrow();
    }

    private static MeshData createCylinder(Tesselator tessellator) {
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        float d = 100F;

        float yMax = d * Mth.PI * 0.5F;
        float yMin = -yMax;
        for(int i = 0; i < 128; i++) {
            float p0 = i / 128F;
            float p1 = (i+1) / 128F;

            float s0 = d * Mth.sin(p0 * Mth.TWO_PI);
            float c0 = d * Mth.cos(p0 * Mth.TWO_PI);
            float s1 = d * Mth.sin(p1 * Mth.TWO_PI);
            float c1 = d * Mth.cos(p1 * Mth.TWO_PI);

            bufferBuilder.addVertex(c0, yMax, s0).setUv(p0, 0.0F).setColor(1F, 1F, 1F, 1F);
            bufferBuilder.addVertex(c0, yMin, s0).setUv(p0, 1.0F).setColor(0.1F, 0.2F, 0.3F, 1F);
            bufferBuilder.addVertex(c1, yMin, s1).setUv(p1, 1.0F).setColor(0.1F, 0.2F, 0.3F, 1F);
            bufferBuilder.addVertex(c1, yMax, s1).setUv(p1, 0.0F).setColor(1F, 1F, 1F, 1F);
        }

        return bufferBuilder.buildOrThrow();
    }

    public void renderSky(ClientLevel clientWorld, PoseStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, Runnable fogCallback) {
        // doing this at initialisation apparently causes a crash for some reason, so we do it here instead
        if(this.starsBuffer == null) {
            createStars();
        }
        if(this.lightSkyBuffer == null) {
            createLightSky();
        }
        if(this.cylinderBuffer == null) {
            createCylinder();
        }

        Tesselator tessellator = Tesselator.getInstance();
        float time = ((clientWorld.getGameTime() % 24000) + tickDelta) / 24000.0F;

        FogRenderer.levelFogColor();

        matrices.pushPose();
        matrices.scale(0.25F, 0.25F, 0.25F);
        matrices.mulPose(Axis.YN.rotationDegrees(20));
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(5 * Mth.sin(Mth.TWO_PI * time * 20)));
        matrices.translate(0, 1 * Mth.sin(Mth.TWO_PI * time * 40), 0);
        matrices.mulPose(Axis.ZN.rotationDegrees(25));

        RenderSystem.depthMask(false);

        // Begin Rendering Light Sky
        FogRenderer.setupNoFog();
        FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, 192, false, tickDelta);

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(360F * time * 2));
        this.lightSkyBuffer.bind();

        ShaderInstance shaderProgram = RenderSystem.getShader();

        matrices.translate(0, 80, 0);
        RenderSystem.setShaderColor(0.1F, 0.25F, 0.25F, 1.0F);
        this.lightSkyBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, shaderProgram);

        VertexBuffer.unbind();
        matrices.popPose();
        // End Rendering Light Sky

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        // Begin Rendering Stars
        FogRenderer.setupNoFog();

        RenderSystem.setShaderColor(0.7F, 1, 1, 1);

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(360F * time * -3));
        this.starsBuffer.bind();
        this.starsBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionShader());
        VertexBuffer.unbind();
        matrices.popPose();

        fogCallback.run();
        // End Rendering Stars


        // Begin Rendering Nova
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0.157F, 0.157F, 0.157F, 1.0F);

        matrices.pushPose();

        // Render Void
        matrices.pushPose();

        matrices.mulPose(Axis.YP.rotationDegrees(360 * time * -60));
        Matrix4f matrix4f = matrices.last().pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, END_SKY);

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        float w = 10F;
        bufferBuilder.addVertex(matrix4f, w, 100.0F, -w).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, w, 100.0F, w).setUv(0.0F, 2.0F);
        bufferBuilder.addVertex(matrix4f, -w, 100.0F, w).setUv(2.0F, 2.0F);
        bufferBuilder.addVertex(matrix4f, -w, 100.0F, -w).setUv(2.0F, 0.0F);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        matrices.popPose();
        // Render Gate
        RenderSystem.setShaderColor(0.3F, 0.4F, 0.4F, 1.0F);

        matrices.mulPose(Axis.YP.rotationDegrees(360 * time * 3));
        matrix4f = matrices.last().pose();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_NOVA);

        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

        float k = 15.0F;
        bufferBuilder.addVertex(matrix4f, -k, 100.0F, -k).setUv(0.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, k, 100.0F, -k).setUv(1.0F, 0.0F);
        bufferBuilder.addVertex(matrix4f, k, 100.0F, k).setUv(1.0F, 1.0F);
        bufferBuilder.addVertex(matrix4f, -k, 100.0F, k).setUv(0.0F, 1.0F);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        RenderSystem.enableBlend();
        // Render Swirls
        RenderSystem.setShaderColor(0.2F, 0.4F, 0.4F, 1F);

        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_NOVA_SWIRL);

        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);

        matrices.mulPose(Axis.YP.rotationDegrees(360 * time * 20));
        int SWIRLS = 15;
        for(int i = 0; i < SWIRLS; i++) {
            matrices.mulPose(Axis.YP.rotationDegrees(360 * time * -3 + i * 413));
            matrix4f = matrices.last().pose();

            float p = i / (float)SWIRLS;
            float r = (1 - p) * 0.5F;
            float g = p;
            float b = 1.0F;
            int color = FastColor.ARGB32.colorFromFloat(1, r, g, b);

            float m = 8.0F + 3.0F * i;
            bufferBuilder.addVertex(matrix4f, -m, 100.0F, -m).setUv(0.0F, 0.0F).setColor(color);
            bufferBuilder.addVertex(matrix4f, m, 100.0F, -m).setUv(1.0F, 0.0F).setColor(color);
            bufferBuilder.addVertex(matrix4f, m, 100.0F, m).setUv(1.0F, 1.0F).setColor(color);
            bufferBuilder.addVertex(matrix4f, -m, 100.0F, m).setUv(0.0F, 1.0F).setColor(color);
        }

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());

        matrices.popPose();
        // End Rendering Nova

        // Begin Rendering Landscape
        RenderSystem.defaultBlendFunc();

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_BACK_MOUNTAINS);
        for(int i = 1; i <= 4; i++) {
            // Back mountains
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees((4 + 2 * i) * Mth.sin(Mth.TWO_PI * time * 20 + 0.4F * i)));
            matrices.translate(0, -(10 + i * 2) + (1 + i * 0.5F) * Mth.sin(Mth.TWO_PI * time * 40 + 0.4F * i), 0);
            RenderSystem.setShaderColor(0.03F * i, 0.08F * i, 0.08F * i, i / 4F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionTexColorShader());
            VertexBuffer.unbind();
            matrices.popPose();
        }

        matrices.popPose();

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180));
        matrices.mulPose(Axis.ZN.rotationDegrees(-10F));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_SKY_TWIRL);
        for(int i = 1; i <= 7; i++) {
            // Outer Sky Twirls
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees((12 + 5 * i) * Mth.sin(Mth.TWO_PI * time * 10 * (8 - i) + 0.4F * i)));
            matrices.translate(0, 30 - 15 * i + (2 + i * 3F) * Mth.sin(Mth.TWO_PI * time * 40 + 0.4F * i), 0);
            RenderSystem.setShaderColor(1F - 0.12F * i, 0.7F - 0.02F * i, 0.9F - 0.05F * i, 0.2F + i * 0.1F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionTexColorShader());
            VertexBuffer.unbind();
            matrices.popPose();
        }

        matrices.popPose();
        // End Rendering Landscape

        // Begin Rendering Spirits

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_SKY_SPIRIT);

        matrices.pushPose();
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        renderSpirits(matrices, bufferBuilder, time);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        matrices.popPose();
        // End Rendering Spirits

        matrices.popPose();

        // Begin Rendering Landscape
        RenderSystem.defaultBlendFunc();

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_CIRCUS);
        for(int i = 1; i <= 6; i++) {
            // Front mountains and Circus
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees((8 + 2 * i) * Mth.sin(Mth.TWO_PI * time * 20 + 0.2F * (i + 4))));
            matrices.translate(0, (2 + i) * Mth.sin(Mth.TWO_PI * time * 40 + 0.2F * (i + 4)), 0);
            RenderSystem.setShaderColor(0.1F + 0.03F * i, 0.2F + 0.08F * i, 0.2F + 0.08F * i, (7 - i) / 8F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionTexColorShader());
            VertexBuffer.unbind();
            matrices.popPose();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180));
        matrices.mulPose(Axis.ZN.rotationDegrees(15F));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_SKY_TWIRL);
        for(int i = 1; i <= 3; i++) {
            // Inner Sky Twirls
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees((12 + 5 * i) * Mth.sin(Mth.TWO_PI * time * 5 * (8 - i) + 0.6F * i)));
            matrices.translate(0, -40 - 18 * i + (2 + i * 3F) * Mth.sin(Mth.TWO_PI * time * 10 + 0.4F * i), 0);
            RenderSystem.setShaderColor(0.1F + 0.2F * i, 0.8F - 0.2F * i, 0.7F - 0.1F * i, 0.05F + i * 0.1F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionTexColorShader());
            VertexBuffer.unbind();
            matrices.popPose();
        }

        matrices.popPose();

        // Start Rendering Base
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(0, 0, 0, 1);
        FogRenderer.setupNoFog();
        RenderSystem.setShader(GameRenderer::getPositionShader);

        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION);

        float j = 100.0F;
        matrix4f = matrices.last().pose();
        bufferBuilder.addVertex(matrix4f, j, -100.0F, j);
        bufferBuilder.addVertex(matrix4f, j, -100.0F, -j);
        bufferBuilder.addVertex(matrix4f, -j, -100.0F, -j);
        bufferBuilder.addVertex(matrix4f, -j, -100.0F, j);

        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        fogCallback.run();
        // End Rendering Base

        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_WATER);
        for(int i = 1; i <= 6; i++) {
            // Water
            matrices.pushPose();
            matrices.mulPose(Axis.YP.rotationDegrees((10 + 3 * i) * Mth.sin(Mth.TWO_PI * time * 20 + 0.5F * (i + 6))));
            matrices.translate(0, (3 + i) * Mth.sin(Mth.TWO_PI * time * 40 + 0.5F * (i + 6)), 0);
            RenderSystem.setShaderColor(0.04F + 0.04F * i, 0.24F + 0.09F * i, 0.24F + 0.09F * i, (7 - i) / 8F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.drawWithShader(matrices.last().pose(), projectionMatrix, GameRenderer.getPositionTexColorShader());
            VertexBuffer.unbind();
            matrices.popPose();
        }

        matrices.popPose();
        // End Rendering Landscape

        // Begin Rendering More Spirits
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );

        RenderSystem.setShaderColor(0.3F, 0.2F, 0.4F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_SKY_SPIRIT);

        matrices.pushPose();
        bufferBuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        renderSpirits(matrices, bufferBuilder, time);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        matrices.popPose();
        // End Rendering More Spirits

        matrices.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private void renderSpirits(PoseStack matrices, VertexConsumer vertexConsumer, float time) {
        for(SkySpiritData data : skySpiritDatas) {
            matrices.pushPose();

            float timeOffset = data.timeOffset;
            float angleOffset = data.angleOffset;
            float localTime = (time + timeOffset);

            int spiritSpeed = 1 + Mth.floor(6 * ((angleOffset) % 1));

            float height = ((localTime * 7 * spiritSpeed) % 1) * 2 - 1;
            float distFromMiddle = Mth.abs(height);
            float distFromEndpoints = (1 - distFromMiddle);
            float distFromGate = 1 - height;

            matrices.mulPose(Axis.YP.rotationDegrees(angleOffset + height * 100 * (timeOffset - 0.5F)));

            matrices.mulPose(Axis.XP.rotationDegrees(40 * height));

            float sin = Mth.sin(Mth.TWO_PI * localTime * 65);

            matrices.translate(0.1 * sin, height * 48, (-64 + 8 * data.depth) * (distFromGate * 0.5 + 0.8));

            matrices.scale(2, 2, 2);

            Matrix4f matrix4f = matrices.last().pose();

            float alpha = 0.3F * distFromEndpoints;
            drawSpirit(vertexConsumer, matrix4f, localTime, distFromMiddle * alpha * data.red, alpha * data.green, alpha * data.blue);

            matrices.popPose();
        }
    }

    private static void drawSpirit(VertexConsumer vertexConsumer, Matrix4f matrix4f, float time, float red, float green, float blue) {
        int r = 0xFF & (int)(red * 255);
        int g = 0xFF & (int)(green * 255);
        int b = 0xFF & (int)(blue * 255);
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;

        float wiggle1 = 0.5F * Mth.sin(Mth.TWO_PI * time * 400);
        float wiggle2 = 0.5F * Mth.sin(Mth.TWO_PI * time * 400 + 0.4F);
        float wiggle3 = 0.5F * Mth.sin(Mth.TWO_PI * time * 400 + 0.8F);
        float wiggle4 = 0.5F * Mth.sin(Mth.TWO_PI * time * 400 + 1.2F);
        float[] wiggles = new float[]{wiggle1, wiggle2, wiggle3, wiggle4};

        int animationFrame = ((int)(time * 12000)) % 4;
        float minU = animationFrame / 4F;
        float maxU = (animationFrame + 1) / 4F;

        int SEGMENTS = 3;
        for(int i = 0; i < SEGMENTS; i++) {
            float topWiggle = wiggles[i];
            float bottomWiggle = wiggles[i+1];

            float topV = (i / (float)SEGMENTS);
            float bottomV = ((i + 1) / (float)SEGMENTS);

            float topY = 4 * (1 - 2 * topV);
            float bottomY = 4 * (1 - 2 * bottomV);

            vertexConsumer.addVertex(matrix4f, topWiggle - 1, topY, 0).setUv(minU, topV).setColor(color);
            vertexConsumer.addVertex(matrix4f, bottomWiggle - 1, bottomY, 0).setUv(minU, bottomV).setColor(color);
            vertexConsumer.addVertex(matrix4f, bottomWiggle + 1, bottomY, 0).setUv(maxU, bottomV).setColor(color);
            vertexConsumer.addVertex(matrix4f, topWiggle + 1, topY, 0).setUv(maxU, topV).setColor(color);
        }
    }

    public static class SkySpiritData {
        public final float depth;
        public final float timeOffset;
        public final float angleOffset;
        public final float red;
        public final float green;
        public final float blue;

        public SkySpiritData(float depth, float timeOffset, float angleOffset, float red, float green, float blue) {
            this.depth = depth;
            this.timeOffset = timeOffset;
            this.angleOffset = angleOffset;
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public static SkySpiritData create(float depth, RandomSource random) {
            float timeOffset = random.nextFloat();
            float angleOffset = random.nextFloat() * 360.0F;

            float phase = random.nextFloat();
            float red = 0.9F + 0.1F * (float)Math.sin(Mth.TWO_PI * phase);
            float green = 0.9F + 0.1F * (float)Math.sin(Mth.TWO_PI * (phase + 1/3F));
            float blue = 0.9F + 0.1F * (float)Math.sin(Mth.TWO_PI * (phase - 1/3F));
            return new SkySpiritData(depth, timeOffset, angleOffset, red, green, blue);
        }
    }
}
