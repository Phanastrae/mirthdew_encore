package phanastrae.mirthdew_encore.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.*;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.client.MirthdewEncoreClient;
import phanastrae.mirthdew_encore.client.render.entity.MirthdewEncoreEntityRenderers;
import phanastrae.mirthdew_encore.client.render.entity.model.MirthdewEncoreEntityModelLayers;
import phanastrae.mirthdew_encore.client.render.shader.MirthdewEncoreShaders;
import phanastrae.mirthdew_encore.client.render.world.DreamtwirlBorderRenderer;
import phanastrae.mirthdew_encore.client.render.world.MirthdewEncoreDimensionEffects;
import phanastrae.mirthdew_encore.network.MirthdewEncorePayloads;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.function.BiConsumer;

public class MirthdewEncoreClientFabric implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		MirthdewEncoreClient.init();

		// payloads
		initClientPayloads();

		// entity renderers
		MirthdewEncoreEntityRenderers.init(this::registerEntityRenderer);

		// entity model layers
		MirthdewEncoreEntityModelLayers.init((modelLayerLocation, layerDefinition) -> EntityModelLayerRegistry.registerModelLayer(modelLayerLocation, layerDefinition::get));

		// register shaders
		CoreShaderRegistrationCallback.EVENT.register((context -> MirthdewEncoreShaders.registerShaders(context::register)));

		// register dimension effects
		DimensionRenderingRegistry.registerDimensionEffects(MirthdewEncoreDimensions.DREAMTWIRL_ID, MirthdewEncoreDimensionEffects.getDreamtwirlDimensionEffects());


		// client shutdown
		ClientLifecycleEvents.CLIENT_STOPPING.register(MirthdewEncoreClient::onClientStop);

		// render dreamtwirl sky
		DimensionRenderingRegistry.registerSkyRenderer(MirthdewEncoreDimensions.DREAMTWIRL_WORLD, context -> MirthdewEncoreDimensionEffects.renderSky(context.positionMatrix(), context.tickCounter(), context.gameRenderer(), context.camera(), context.world(), context.projectionMatrix()));

		// render dreamtwirl border
		WorldRenderEvents.AFTER_SETUP.register(context -> DreamtwirlBorderRenderer.render(context.positionMatrix(), context.world(), context.camera()));
	}

	public void initClientPayloads() {
		MirthdewEncorePayloads.init(new MirthdewEncorePayloads.Helper() {
			@Override
			public <T extends CustomPacketPayload> void registerS2C(CustomPacketPayload.Type<T> id, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, BiConsumer<T, Player> clientCallback) {
				ClientPlayNetworking.registerGlobalReceiver(id, (payload, context) -> clientCallback.accept(payload, context.player()));
			}
		});
	}

	public <E extends Entity> void registerEntityRenderer(EntityType<? extends E> entityType, EntityRendererProvider<E> entityRendererFactory) {
		EntityRendererRegistry.register(entityType, entityRendererFactory);
	}
}