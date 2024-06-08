package phanastrae.mirthlight_encore;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.MinecraftClient;
import phanastrae.mirthlight_encore.render.world.MirthlightEncoreDimensionEffects;

public class MirthlightEncoreClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthlightEncoreDimensionEffects.getInstance().init();

		ClientLifecycleEvents.CLIENT_STOPPING.register(this::onClientStop);
	}

	private void onClientStop(MinecraftClient minecraftClient) {
		MirthlightEncoreDimensionEffects.getInstance().close();
	}
}