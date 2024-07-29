package phanastrae.mirthdew_encore.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.DimensionRenderingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

public class MirthdewEncoreClientFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthdewEncoreClient.init();

		DimensionRenderingRegistry.registerDimensionEffects(MirthdewEncoreDimensions.DREAMTWIRL_ID, MirthdewEncoreDimensionEffects.getDreamtwirlDimensionEffects());
		DimensionRenderingRegistry.registerSkyRenderer(MirthdewEncoreDimensions.DREAMTWIRL_WORLD, context -> MirthdewEncoreDimensionEffects.renderSky(context.positionMatrix(), context.tickCounter(), context.gameRenderer(), context.camera(), context.world(), context.projectionMatrix()));

		CoreShaderRegistrationCallback.EVENT.register((context -> MirthdewEncoreShaders.registerShaders(context::register)));

		ClientLifecycleEvents.CLIENT_STOPPING.register(MirthdewEncoreClient::onClientStop);
		WorldRenderEvents.AFTER_SETUP.register(context -> DreamtwirlBorderRenderer.render(context.positionMatrix(), context.world(), context.camera()));
	}
}