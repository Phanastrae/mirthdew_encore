package phanastrae.mirthdew_encore.client.render.world;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

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

    public static void renderSky(Matrix4f positionMatrix, DeltaTracker deltaTracker, GameRenderer gameRenderer, Camera camera, ClientLevel level, Matrix4f projectionMatrix) {
        PoseStack matrices = new PoseStack();
        matrices.mulPose(positionMatrix);

        float tickDelta = deltaTracker.getGameTimeDeltaPartialTick(false);

        Minecraft client = gameRenderer.getMinecraft();

        boolean useThickFog = client.gui.getBossOverlay().shouldCreateWorldFog(); // dimension does not use thick fog by default
        Runnable fogCallback = () -> FogRenderer.setupFog(camera, FogRenderer.FogMode.FOG_SKY, gameRenderer.getRenderDistance(), useThickFog, tickDelta);
        INSTANCE.dreamtwirlSkyRenderer.renderSky(level, matrices, projectionMatrix, tickDelta, camera, fogCallback);
    }
}
