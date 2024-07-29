package phanastrae.mirthdew_encore.client;

import net.minecraft.client.Minecraft;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.client.render.block.MirthdewEncoreBlockRenderLayers;
import phanastrae.mirthdew_encore.client.render.block.entity.MirthdewEncoreBlockEntityRendererFactories;
import phanastrae.mirthdew_encore.client.render.entity.MirthdewEncoreEntityRenderers;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;

public class MirthdewEncoreClient {

    public static void init() {
        MirthdewEncoreClientPacketHandler.init();

        MirthdewEncoreBlockRenderLayers.init();
        MirthdewEncoreBlockEntityRendererFactories.init();

        MirthdewEncoreEntityRenderers.init();
        MirthdewEncoreEntityModelLayers.init();

        MirthdewEncoreDimensionEffects.getInstance().init();
    }

    public static void onClientStop(Minecraft minecraftClient) {
        MirthdewEncoreDimensionEffects.getInstance().close();
        DreamtwirlBorderRenderer.close();
    }
}
