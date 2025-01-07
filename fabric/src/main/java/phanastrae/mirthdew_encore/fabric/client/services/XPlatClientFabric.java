package phanastrae.mirthdew_encore.fabric.client.services;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;

public class XPlatClientFabric implements XPlatClientInterface {

    @Override
    public void registerBlockRenderLayers(RenderType renderLayer, Block... blocks) {
        BlockRenderLayerMap.INSTANCE.putBlocks(renderLayer, blocks);
    }

    @Override
    public void registerFluidRenderLayers(RenderType renderLayer, Fluid... fluids) {
        BlockRenderLayerMap.INSTANCE.putFluids(renderLayer, fluids);
    }
}
