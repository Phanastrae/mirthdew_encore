package phanastrae.mirthdew_encore.neoforge.client.services;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;

public class XPlatClientNeoForge implements XPlatClientInterface {

    @Override
    public void registerBlockRenderLayers(RenderType renderLayer, Block... blocks) {
        for(Block block : blocks) {
            ItemBlockRenderTypes.setRenderLayer(block, renderLayer);
        }
    }

    @Override
    public void registerFluidRenderLayers(RenderType renderLayer, Fluid... fluids) {
        for(Fluid fluid : fluids) {
            ItemBlockRenderTypes.setRenderLayer(fluid, renderLayer);
        }
    }
}
