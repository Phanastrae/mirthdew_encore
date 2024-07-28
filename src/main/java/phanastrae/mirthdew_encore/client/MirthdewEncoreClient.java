package phanastrae.mirthdew_encore.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.Minecraft;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.client.render.block.MirthdewEncoreBlockRenderLayers;
import phanastrae.mirthdew_encore.client.render.block.entity.MirthdewEncoreBlockEntityRendererFactories;
import phanastrae.mirthdew_encore.client.render.entity.MirthdewEncoreEntityRenderers;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;

public class MirthdewEncoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthdewEncoreBlockRenderLayers.init();
		MirthdewEncoreBlockEntityRendererFactories.init();

		MirthdewEncoreEntityRenderers.init();
		MirthdewEncoreEntityModelLayers.init();

		MirthdewEncoreDimensionEffects.getInstance().init();
		CoreShaderRegistrationCallback.EVENT.register(MirthdewEncoreShaders::registerShaders);

		MirthdewEncoreClientPacketHandler.init();

		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
		WorldRenderEvents.AFTER_SETUP.register(context -> {
			PoseStack matrices = new PoseStack();
			matrices.mulPose(context.positionMatrix());

			DreamtwirlBorderRenderer.render(context.world(), context.camera(), matrices);
		});
	}

	private void onClientStop(Minecraft minecraftClient) {
		MirthdewEncoreDimensionEffects.getInstance().close();
		DreamtwirlBorderRenderer.close();
	}
}