package phanastrae.mirthlight_encore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import phanastrae.mirthlight_encore.render.shader.MirthlightEncoreShaders;
import phanastrae.mirthlight_encore.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthlight_encore.render.world.MirthlightEncoreDimensionEffects;

public class MirthlightEncoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthlightEncoreDimensionEffects.getInstance().init();
		CoreShaderRegistrationCallback.EVENT.register(MirthlightEncoreShaders::registerShaders);

		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
		WorldRenderEvents.AFTER_SETUP.register(context -> {
			MatrixStack matrices = new MatrixStack();
			matrices.multiplyPositionMatrix(context.positionMatrix());

			DreamtwirlBorderRenderer.render(context.world(), context.camera(), matrices);
		});
	}

	private void onClientStop(MinecraftClient minecraftClient) {
		MirthlightEncoreDimensionEffects.getInstance().close();
		DreamtwirlBorderRenderer.close();
	}
}