package phanastrae.mirthdew_encore.client.services;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;

import java.util.function.Supplier;

public class XPlatClientFabric implements XPlatClientInterface {

    @Override
    public <T extends CustomPacketPayload> void registerGlobalReceiver(CustomPacketPayload.Type<T> type, MirthdewEncoreClientPacketHandler.PayloadHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> handler.receive(payload, context.client(), context.player()));
    }

    @Override
    public void registerBlockRenderLayers(RenderType renderLayer, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(renderLayer, blocks);
    }

    @Override
    public <E extends Entity> void registerEntityRenderer(EntityType<? extends E> entityType, EntityRendererProvider<E> entityRendererFactory) {
        EntityRendererRegistry.register(entityType, entityRendererFactory);
    }

    @Override
    public void registerModelLayer(ModelLayerLocation modelLayer, Supplier<LayerDefinition> provider) {
        EntityModelLayerRegistry.registerModelLayer(modelLayer, provider::get);
    }
}
