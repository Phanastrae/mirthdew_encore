package phanastrae.mirthdew_encore.client.render.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;

public class MirthdewEncoreBlockRenderLayers {

    public static void init() {
        putBlocks(RenderType.cutout(),
                MirthdewEncoreBlocks.DREAMSEED,
                MirthdewEncoreBlocks.SLUMBERSOCKET,
                MirthdewEncoreBlocks.VERIC_DREAMSNARE);

        putBlocks(RenderType.translucent(),
                MirthdewEncoreBlocks.SLUMBERVEIL);
    }

    private static void putBlocks(RenderType renderLayer, Block... blocks) {
        XPlatClientInterface.INSTANCE.registerBlockRenderLayers(renderLayer, blocks);
    }
}