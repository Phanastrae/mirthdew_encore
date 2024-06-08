package phanastrae.mirthlight_encore.render.world;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gl.VertexBuffer;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import phanastrae.mirthlight_encore.MirthlightEncore;

public class DreamtwirlSkyRenderer {
    private static final Identifier DREAMTWIRL_SKY_SPIRIT = MirthlightEncore.id("textures/environment/dreamtwirl_sky_spirit.png");
    private static final Identifier DREAMTWIRL_NOVA = MirthlightEncore.id("textures/environment/dreamtwirl_nova.png");
    private static final Identifier DREAMTWIRL_NOVA_SWIRL = MirthlightEncore.id("textures/environment/dreamtwirl_nova_swirl.png");
    private static final Identifier DREAMTWIRL_LANDSCAPE_SKY_TWIRL = MirthlightEncore.id("textures/environment/dreamtwirl_landscape_sky_twirl.png");
    private static final Identifier DREAMTWIRL_LANDSCAPE_CIRCUS = MirthlightEncore.id("textures/environment/dreamtwirl_landscape_circus.png");
    private static final Identifier DREAMTWIRL_LANDSCAPE_BACK_MOUNTAINS = MirthlightEncore.id("textures/environment/dreamtwirl_landscape_back_mountains.png");
    private static final Identifier DREAMTWIRL_LANDSCAPE_WATER = MirthlightEncore.id("textures/environment/dreamtwirl_landscape_water.png");
    private static final Identifier END_SKY = Identifier.ofVanilla("textures/environment/end_sky.png");
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

        Random random = Random.create();
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
        this.starsBuffer.upload(this.createStars(Tessellator.getInstance()));
        VertexBuffer.unbind();
    }

    private void createLightSky() {
        closeIfNotNull(lightSkyBuffer);

        this.lightSkyBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.lightSkyBuffer.bind();
        this.lightSkyBuffer.upload(createLightSky(Tessellator.getInstance(), 16.0F));
        VertexBuffer.unbind();
    }

    private void createCylinder() {
        closeIfNotNull(cylinderBuffer);

        this.cylinderBuffer = new VertexBuffer(VertexBuffer.Usage.STATIC);
        this.cylinderBuffer.bind();
        this.cylinderBuffer.upload(createCylinder(Tessellator.getInstance()));
        VertexBuffer.unbind();
    }

    private BuiltBuffer createStars(Tessellator tessellator) {
        Random random = Random.create(1025);
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);

        int STAR_COUNT = 6120;
        for(int j = 0; j < STAR_COUNT; ++j) {
            float x = random.nextFloat() * 2.0F - 1.0F;
            float y = random.nextFloat() * 2.0F - 1.0F;
            float z = random.nextFloat() * 2.0F - 1.0F;
            float m = MathHelper.magnitude(x, y, z);
            if (!(m <= 0.010000001F) && !(m >= 1.0F)) {
                Vector3f vector3f = new Vector3f(x, y, z).normalize(100.0F);
                float zAngle = (float)(random.nextDouble() * (float) Math.PI * 2.0);
                Quaternionf quaternionf = new Quaternionf().rotateTo(new Vector3f(0.0F, 0.0F, -1.0F), vector3f).rotateZ(zAngle);

                float phase = random.nextFloat();
                float red = 0.75F + 0.25F * (float)Math.sin(MathHelper.TAU * phase);
                float green = 0.85F + 0.15F * (float)Math.sin(MathHelper.TAU * (phase + 1/3F));
                float blue = 0.95F + 0.05F * (float)Math.sin(MathHelper.TAU * (phase - 1/3F));

                float l = 0.15F + random.nextFloat() * 0.1F;
                bufferBuilder.vertex(vector3f.add(new Vector3f(l, -l, 0.0F).rotate(quaternionf))).color(red, green, blue, 1);
                bufferBuilder.vertex(vector3f.add(new Vector3f(l, l, 0.0F).rotate(quaternionf))).color(red, green, blue, 1);
                bufferBuilder.vertex(vector3f.add(new Vector3f(-l, l, 0.0F).rotate(quaternionf))).color(red, green, blue, 1);
                bufferBuilder.vertex(vector3f.add(new Vector3f(-l, -l, 0.0F).rotate(quaternionf))).color(red, green, blue, 1);
            }
        }

        return bufferBuilder.end();
    }

    private static BuiltBuffer createLightSky(Tessellator tessellator, float f) {
        float g = Math.signum(f) * 512.0F;
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION);
        bufferBuilder.vertex(0.0F, f, 0.0F);

        for(int i = -180; i <= 180; i += 45) {
            bufferBuilder.vertex(g * MathHelper.cos((float)i * (float) (Math.PI / 180.0)), f, 512.0F * MathHelper.sin((float)i * (float) (Math.PI / 180.0)));
        }

        return bufferBuilder.end();
    }

    private static BuiltBuffer createCylinder(Tessellator tessellator) {
        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        float d = 100F;

        float yMax = d * MathHelper.PI * 0.5F;
        float yMin = -yMax;
        for(int i = 0; i < 128; i++) {
            float p0 = i / 128F;
            float p1 = (i+1) / 128F;

            float s0 = d * MathHelper.sin(p0 * MathHelper.TAU);
            float c0 = d * MathHelper.cos(p0 * MathHelper.TAU);
            float s1 = d * MathHelper.sin(p1 * MathHelper.TAU);
            float c1 = d * MathHelper.cos(p1 * MathHelper.TAU);

            bufferBuilder.vertex(c0, yMax, s0).texture(p0, 0.0F).color(1F, 1F, 1F, 1F);
            bufferBuilder.vertex(c0, yMin, s0).texture(p0, 1.0F).color(0.1F, 0.2F, 0.3F, 1F);
            bufferBuilder.vertex(c1, yMin, s1).texture(p1, 1.0F).color(0.1F, 0.2F, 0.3F, 1F);
            bufferBuilder.vertex(c1, yMax, s1).texture(p1, 0.0F).color(1F, 1F, 1F, 1F);
        }

        return bufferBuilder.end();
    }

    public void renderSky(ClientWorld clientWorld, MatrixStack matrices, Matrix4f projectionMatrix, float tickDelta, Camera camera, Runnable fogCallback) {
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

        Tessellator tessellator = Tessellator.getInstance();
        float time = ((clientWorld.getTime() % 24000) + tickDelta) / 24000.0F;

        BackgroundRenderer.applyFogColor();

        matrices.push();
        matrices.scale(0.25F, 0.25F, 0.25F);
        matrices.multiply(RotationAxis.NEGATIVE_Y.rotationDegrees(20));
        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5 * MathHelper.sin(time * 120)));
        matrices.translate(0, 1 * MathHelper.sin(time * 240), 0);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(25));

        RenderSystem.depthMask(false);

        // Begin Rendering Light Sky
        BackgroundRenderer.clearFog();
        BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, 192, false, tickDelta);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360F * time * 2));
        this.lightSkyBuffer.bind();

        ShaderProgram shaderProgram = RenderSystem.getShader();

        matrices.translate(0, 80, 0);
        RenderSystem.setShaderColor(0.1F, 0.25F, 0.25F, 1.0F);
        this.lightSkyBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, shaderProgram);

        VertexBuffer.unbind();
        matrices.pop();
        // End Rendering Light Sky

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        // Begin Rendering Stars
        BackgroundRenderer.clearFog();

        RenderSystem.setShaderColor(0.7F, 1, 1, 1);

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360F * time * -3));
        this.starsBuffer.bind();
        this.starsBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionProgram());
        VertexBuffer.unbind();
        matrices.pop();

        fogCallback.run();
        // End Rendering Stars


        // Begin Rendering Nova
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(0.157F, 0.157F, 0.157F, 1.0F);

        matrices.push();

        // Render Void
        matrices.push();

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360 * time * -60));
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, END_SKY);

        BufferBuilder bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);
        float w = 10F;
        bufferBuilder.vertex(matrix4f, w, 100.0F, -w).texture(0.0F, 0.0F);
        bufferBuilder.vertex(matrix4f, w, 100.0F, w).texture(0.0F, 2.0F);
        bufferBuilder.vertex(matrix4f, -w, 100.0F, w).texture(2.0F, 2.0F);
        bufferBuilder.vertex(matrix4f, -w, 100.0F, -w).texture(2.0F, 0.0F);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();
        // Render Gate
        RenderSystem.setShaderColor(0.3F, 0.4F, 0.4F, 1.0F);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360 * time * 3));
        matrix4f = matrices.peek().getPositionMatrix();

        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_NOVA);

        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE);

        float k = 15.0F;
        bufferBuilder.vertex(matrix4f, -k, 100.0F, -k).texture(0.0F, 0.0F);
        bufferBuilder.vertex(matrix4f, k, 100.0F, -k).texture(1.0F, 0.0F);
        bufferBuilder.vertex(matrix4f, k, 100.0F, k).texture(1.0F, 1.0F);
        bufferBuilder.vertex(matrix4f, -k, 100.0F, k).texture(0.0F, 1.0F);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        RenderSystem.enableBlend();
        // Render Swirls
        RenderSystem.setShaderColor(0.2F, 0.4F, 0.4F, 1F);

        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_NOVA_SWIRL);

        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);

        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360 * time * 20));
        int SWIRLS = 15;
        for(int i = 0; i < SWIRLS; i++) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(360 * time * -3 + i * 413));
            matrix4f = matrices.peek().getPositionMatrix();

            float p = i / (float)SWIRLS;
            float r = (1 - p) * 0.5F;
            float g = p;
            float b = 1.0F;
            int color = ColorHelper.Argb.fromFloats(1, r, g, b);

            float m = 8.0F + 3.0F * i;
            bufferBuilder.vertex(matrix4f, -m, 100.0F, -m).texture(0.0F, 0.0F).color(color);
            bufferBuilder.vertex(matrix4f, m, 100.0F, -m).texture(1.0F, 0.0F).color(color);
            bufferBuilder.vertex(matrix4f, m, 100.0F, m).texture(1.0F, 1.0F).color(color);
            bufferBuilder.vertex(matrix4f, -m, 100.0F, m).texture(0.0F, 1.0F).color(color);
        }

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());

        matrices.pop();
        // End Rendering Nova

        // Begin Rendering Landscape
        RenderSystem.defaultBlendFunc();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_BACK_MOUNTAINS);
        for(int i = 1; i <= 4; i++) {
            // Back mountains
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((4 + 2 * i) * MathHelper.sin(time * 120 + 0.4F * i)));
            matrices.translate(0, -(10 + i * 2) + (1 + i * 0.5F) * MathHelper.sin(time * 240 + 0.4F * i), 0);
            RenderSystem.setShaderColor(0.03F * i, 0.08F * i, 0.08F * i, i / 4F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();
            matrices.pop();
        }

        matrices.pop();

        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(-10F));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_SKY_TWIRL);
        for(int i = 1; i <= 7; i++) {
            // Outer Sky Twirls
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((12 + 5 * i) * MathHelper.sin(time * 60 * (8 - i) + 0.4F * i)));
            matrices.translate(0, 30 - 15 * i + (2 + i * 3F) * MathHelper.sin(time * 240 + 0.4F * i), 0);
            RenderSystem.setShaderColor(1F - 0.12F * i, 0.7F - 0.02F * i, 0.9F - 0.05F * i, 0.2F + i * 0.1F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();
            matrices.pop();
        }

        matrices.pop();
        // End Rendering Landscape

        // Begin Rendering Spirits

        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_SKY_SPIRIT);

        matrices.push();
        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        renderSpirits(matrices, bufferBuilder, time);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        matrices.pop();
        // End Rendering Spirits

        matrices.pop();

        // Begin Rendering Landscape
        RenderSystem.defaultBlendFunc();

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_CIRCUS);
        for(int i = 1; i <= 6; i++) {
            // Front mountains and Circus
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((8 + 2 * i) * MathHelper.sin(time * 120 + 0.2F * (i + 4))));
            matrices.translate(0, (2 + i) * MathHelper.sin(time * 240 + 0.2F * (i + 4)), 0);
            RenderSystem.setShaderColor(0.1F + 0.03F * i, 0.2F + 0.08F * i, 0.2F + 0.08F * i, (7 - i) / 8F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();
            matrices.pop();
        }

        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        matrices.push();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(15F));

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_SKY_TWIRL);
        for(int i = 1; i <= 3; i++) {
            // Inner Sky Twirls
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((12 + 5 * i) * MathHelper.sin(time * 30 * (8 - i) + 0.6F * i)));
            matrices.translate(0, -40 - 18 * i + (2 + i * 3F) * MathHelper.sin(time * 60 + 0.4F * i), 0);
            RenderSystem.setShaderColor(0.1F + 0.2F * i, 0.8F - 0.2F * i, 0.7F - 0.1F * i, 0.05F + i * 0.1F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();
            matrices.pop();
        }

        matrices.pop();

        // Start Rendering Base
        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderColor(0, 0, 0, 1);
        BackgroundRenderer.clearFog();
        RenderSystem.setShader(GameRenderer::getPositionProgram);

        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION);

        float j = 100.0F;
        matrix4f = matrices.peek().getPositionMatrix();
        bufferBuilder.vertex(matrix4f, j, -100.0F, j);
        bufferBuilder.vertex(matrix4f, j, -100.0F, -j);
        bufferBuilder.vertex(matrix4f, -j, -100.0F, -j);
        bufferBuilder.vertex(matrix4f, -j, -100.0F, j);

        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        fogCallback.run();
        // End Rendering Base

        RenderSystem.defaultBlendFunc();

        RenderSystem.setShaderTexture(0, DREAMTWIRL_LANDSCAPE_WATER);
        for(int i = 1; i <= 6; i++) {
            // Water
            matrices.push();
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees((10 + 3 * i) * MathHelper.sin(time * 120 + 0.5F * (i + 6))));
            matrices.translate(0, (3 + i) * MathHelper.sin(time * 240 + 0.5F * (i + 6)), 0);
            RenderSystem.setShaderColor(0.04F + 0.04F * i, 0.24F + 0.09F * i, 0.24F + 0.09F * i, (7 - i) / 8F);

            this.cylinderBuffer.bind();
            this.cylinderBuffer.draw(matrices.peek().getPositionMatrix(), projectionMatrix, GameRenderer.getPositionTexColorProgram());
            VertexBuffer.unbind();
            matrices.pop();
        }

        matrices.pop();
        // End Rendering Landscape

        // Begin Rendering More Spirits
        RenderSystem.blendFuncSeparate(
                GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE, GlStateManager.SrcFactor.ONE, GlStateManager.DstFactor.ZERO
        );

        RenderSystem.setShaderColor(0.3F, 0.2F, 0.4F, 1F);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.setShaderTexture(0, DREAMTWIRL_SKY_SPIRIT);

        matrices.push();
        bufferBuilder = tessellator.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        renderSpirits(matrices, bufferBuilder, time);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        matrices.pop();
        // End Rendering More Spirits

        matrices.pop();
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
    }

    private void renderSpirits(MatrixStack matrices, VertexConsumer vertexConsumer, float time) {
        for(SkySpiritData data : skySpiritDatas) {
            matrices.push();

            float timeOffset = data.timeOffset;
            float angleOffset = data.angleOffset;
            float localTime = (time + timeOffset);

            int spiritSpeed = 1 + MathHelper.floor(6 * ((angleOffset) % 1));

            float height = ((localTime * 7 * spiritSpeed) % 1) * 2 - 1;
            float distFromMiddle = MathHelper.abs(height);
            float distFromEndpoints = (1 - distFromMiddle);
            float distFromGate = 1 - height;

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(angleOffset + height * 100 * (timeOffset - 0.5F)));

            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(40 * height));

            float sin = MathHelper.sin(localTime * 400);

            matrices.translate(0.1 * sin, height * 48, (-64 + 8 * data.depth) * (distFromGate * 0.5 + 0.8));

            matrices.scale(2, 2, 2);

            Matrix4f matrix4f = matrices.peek().getPositionMatrix();

            float alpha = 0.3F * distFromEndpoints;
            drawSpirit(vertexConsumer, matrix4f, localTime, distFromMiddle * alpha * data.red, alpha * data.green, alpha * data.blue);

            matrices.pop();
        }
    }

    private static void drawSpirit(VertexConsumer vertexConsumer, Matrix4f matrix4f, float time, float red, float green, float blue) {
        int r = 0xFF & (int)(red * 255);
        int g = 0xFF & (int)(green * 255);
        int b = 0xFF & (int)(blue * 255);
        int color = 0xFF000000 | (r << 16) | (g << 8) | b;

        float wiggle1 = 0.5F * MathHelper.sin(time * 2400);
        float wiggle2 = 0.5F * MathHelper.sin(time * 2400 + 0.4F);
        float wiggle3 = 0.5F * MathHelper.sin(time * 2400 + 0.8F);
        float wiggle4 = 0.5F * MathHelper.sin(time * 2400 + 1.2F);
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

            vertexConsumer.vertex(matrix4f, topWiggle - 1, topY, 0).texture(minU, topV).color(color);
            vertexConsumer.vertex(matrix4f, bottomWiggle - 1, bottomY, 0).texture(minU, bottomV).color(color);
            vertexConsumer.vertex(matrix4f, bottomWiggle + 1, bottomY, 0).texture(maxU, bottomV).color(color);
            vertexConsumer.vertex(matrix4f, topWiggle + 1, topY, 0).texture(maxU, topV).color(color);
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

        public static SkySpiritData create(float depth, Random random) {
            float timeOffset = random.nextFloat();
            float angleOffset = random.nextFloat() * 360.0F;

            float phase = random.nextFloat();
            float red = 0.9F + 0.1F * (float)Math.sin(MathHelper.TAU * phase);
            float green = 0.9F + 0.1F * (float)Math.sin(MathHelper.TAU * (phase + 1/3F));
            float blue = 0.9F + 0.1F * (float)Math.sin(MathHelper.TAU * (phase - 1/3F));
            return new SkySpiritData(depth, timeOffset, angleOffset, red, green, blue);
        }
    }
}
