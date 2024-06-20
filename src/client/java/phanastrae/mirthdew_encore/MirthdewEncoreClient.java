package phanastrae.mirthdew_encore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import phanastrae.mirthdew_encore.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.render.entity.MirthdewEncoreEntityRenderers;
import phanastrae.mirthdew_encore.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.render.world.MirthdewEncoreDimensionEffects;

public class MirthdewEncoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthdewEncoreEntityRenderers.init();
		MirthdewEncoreEntityModelLayers.init();

		MirthdewEncoreDimensionEffects.getInstance().init();
		CoreShaderRegistrationCallback.EVENT.register(MirthdewEncoreShaders::registerShaders);

		MirthdewEncoreClientPacketHandler.init();

		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
		WorldRenderEvents.AFTER_SETUP.register(context -> {
			MatrixStack matrices = new MatrixStack();
			matrices.multiplyPositionMatrix(context.positionMatrix());

			DreamtwirlBorderRenderer.render(context.world(), context.camera(), matrices);
		});
	}

	private void onClientStop(MinecraftClient minecraftClient) {
		MirthdewEncoreDimensionEffects.getInstance().close();
		DreamtwirlBorderRenderer.close();
	}
}