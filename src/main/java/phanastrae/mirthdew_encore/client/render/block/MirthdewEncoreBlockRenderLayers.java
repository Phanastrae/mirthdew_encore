package phanastrae.mirthdew_encore.client.render.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

public class MirthdewEncoreBlockRenderLayers {

    public static void init() {
        BlockRenderLayerMap map = BlockRenderLayerMap.INSTANCE;
        map.putBlocks(RenderLayer.getCutout(),
                MirthdewEncoreBlocks.DREAMSEED,
                MirthdewEncoreBlocks.SLUMBERSOCKET,
                MirthdewEncoreBlocks.VERIC_DREAMSNARE);

        map.putBlocks(RenderLayer.getTranslucent(),
                MirthdewEncoreBlocks.SLUMBERVEIL);
    }
}
