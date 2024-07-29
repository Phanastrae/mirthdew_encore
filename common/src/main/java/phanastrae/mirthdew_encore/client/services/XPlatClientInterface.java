package phanastrae.mirthdew_encore.client.services;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.client.network.MirthdewEncoreClientPacketHandler;
import phanastrae.mirthdew_encore.services.Services;

import java.util.function.Supplier;

public interface XPlatClientInterface {
    XPlatClientInterface INSTANCE = Services.load(XPlatClientInterface.class);

    <T extends CustomPacketPayload> void registerGlobalReceiver(CustomPacketPayload.Type<T> type, MirthdewEncoreClientPacketHandler.PayloadHandler<T> handler);

    void registerBlockRenderLayers(RenderType renderLayer, Block... blocks);

    <E extends Entity> void registerEntityRenderer(EntityType<? extends E> entityType, EntityRendererProvider<E> entityRendererFactory);

    void registerModelLayer(ModelLayerLocation modelLayer, Supplier<LayerDefinition> provider);
}
