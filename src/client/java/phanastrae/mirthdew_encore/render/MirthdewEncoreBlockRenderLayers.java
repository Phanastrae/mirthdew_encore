package phanastrae.mirthdew_encore.render;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

public class MirthdewEncoreBlockRenderLayers {

    public static void init() {
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                MirthdewEncoreBlocks.DREAMSEED,
                MirthdewEncoreBlocks.VERIC_DREAMSNARE);
    }
}
