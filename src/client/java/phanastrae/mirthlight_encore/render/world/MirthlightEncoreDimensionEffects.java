package phanastrae.mirthlight_encore.render.world;

import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.DimensionEffects;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import phanastrae.mirthlight_encore.world.dimension.MirthlightEncoreDimensions;

public class MirthlightEncoreDimensionEffects {
    private static final MirthlightEncoreDimensionEffects INSTANCE = new MirthlightEncoreDimensionEffects();
    public static MirthlightEncoreDimensionEffects getInstance() {
        return INSTANCE;
    }

    public DreamtwirlSkyRenderer dreamtwirlSkyRenderer;

    private MirthlightEncoreDimensionEffects() {
    }

    public void close() {
        this.dreamtwirlSkyRenderer.close();
    }

    public void init() {
        this.dreamtwirlSkyRenderer = new DreamtwirlSkyRenderer();

        DimensionRenderingRegistry.registerDimensionEffects(MirthlightEncoreDimensions.DREAMTWIRL_ID, getDreamtwirlDimensionEffects());
        DimensionRenderingRegistry.registerSkyRenderer(MirthlightEncoreDimensions.DREAMTWIRL_WORLD, context -> {
            MatrixStack matrices = new MatrixStack();
            matrices.multiplyPositionMatrix(context.positionMatrix());

            float tickDelta = context.tickCounter().getTickDelta(false);

            MinecraftClient client = context.gameRenderer().getClient();

            Camera camera = context.camera();
            boolean useThickFog = client.inGameHud.getBossBarHud().shouldThickenFog(); // dimension does not use thick fog by default
            Runnable fogCallback = () -> BackgroundRenderer.applyFog(camera, BackgroundRenderer.FogType.FOG_SKY, context.gameRenderer().getViewDistance(), useThickFog, tickDelta);
            this.dreamtwirlSkyRenderer.renderSky(context.world(), matrices, context.projectionMatrix(), tickDelta, camera, fogCallback);
        });
    }

    public static DimensionEffects getDreamtwirlDimensionEffects() {
        return new DimensionEffects(Float.NaN, false, DimensionEffects.SkyType.NONE, false, false) {
            @Override
            public Vec3d adjustFogColor(Vec3d color, float sunHeight) {
                return color;
            }

            @Override
            public boolean useThickFog(int camX, int camY) {
                return false;
            }

            @Override
            public float[] getFogColorOverride(float skyAngle, float tickDelta) {
                return null;
            }
        };
    }
}
