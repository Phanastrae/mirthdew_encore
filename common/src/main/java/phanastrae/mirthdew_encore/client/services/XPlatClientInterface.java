package phanastrae.mirthdew_encore.client.services;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import phanastrae.mirthdew_encore.services.Services;

public interface XPlatClientInterface {
    XPlatClientInterface INSTANCE = Services.load(XPlatClientInterface.class);

    void registerBlockRenderLayers(RenderType renderLayer, Block... blocks);

    void registerFluidRenderLayers(RenderType renderLayer, Fluid... fluids);

    void sendPayload(CustomPacketPayload payload);
}
