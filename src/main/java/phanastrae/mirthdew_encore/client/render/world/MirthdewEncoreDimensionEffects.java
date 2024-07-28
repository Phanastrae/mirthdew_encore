package phanastrae.mirthdew_encore.client.render.world;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.world.phys.Vec3;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

public class MirthdewEncoreDimensionEffects {
    private static final MirthdewEncoreDimensionEffects INSTANCE = new MirthdewEncoreDimensionEffects();
    public static MirthdewEncoreDimensionEffects getInstance() {
        return INSTANCE;
    }

    public DreamtwirlSkyRenderer dreamtwirlSkyRenderer;

    private MirthdewEncoreDimensionEffects() {
    }

    public void close() {
        this.dreamtwirlSkyRenderer.close();
    }

    public void init() {
        this.dreamtwirlSkyRenderer = new DreamtwirlSkyRenderer();

        DimensionRenderingRegistry.registerDimensionEffects(MirthdewEncoreDimensions.DREAMTWIRL_ID, getDreamtwirlDimensionEffects());
        DimensionRenderingRegistry.registerSkyRenderer(MirthdewEncoreDimensions.DREAMTWIRL_WORLD, context -> {
            PoseStack matrices = new PoseStack();
            matrices.mulPose(context.positionMatrix());

            float tickDelta = context.tickCounter().getGameTimeDeltaPartialTick(false);

            Minecraft client = context.gameRenderer().getMinecraft();

            Camera camera = context.camera();
            boolean useThickFog = client.gui.getBossOverlay().shouldCreateWorldFog(); // dimension does not use thick fog by default
            Runnable fogCallback = () -> FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, context.gameRenderer().getRenderDistance(), useThickFog, tickDelta);
            this.dreamtwirlSkyRenderer.renderSky(context.world(), matrices, context.projectionMatrix(), tickDelta, camera, fogCallback);
        });
    }

    public static DimensionSpecialEffects getDreamtwirlDimensionEffects() {
        return new DimensionSpecialEffects(Float.NaN, false, DimensionSpecialEffects.SkyType.NONE, false, false) {
            @Override
            public Vec3 getBrightnessDependentFogColor(Vec3 color, float sunHeight) {
                return color;
            }

            @Override
            public boolean isFoggyAt(int camX, int camY) {
                return false;
            }

            @Override
            public float[] getSunriseColor(float skyAngle, float tickDelta) {
                return null;
            }
        };
    }
}
