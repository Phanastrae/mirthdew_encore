package phanastrae.mirthdew_encore.client.render.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.renderer.RenderType;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

public class MirthdewEncoreBlockRenderLayers {

    public static void init() {
        BlockRenderLayerMap map = BlockRenderLayerMap.INSTANCE;
        map.putBlocks(RenderType.cutout(),
                MirthdewEncoreBlocks.DREAMSEED,
                MirthdewEncoreBlocks.SLUMBERSOCKET,
                MirthdewEncoreBlocks.VERIC_DREAMSNARE);

        map.putBlocks(RenderType.translucent(),
                MirthdewEncoreBlocks.SLUMBERVEIL);
    }
}
