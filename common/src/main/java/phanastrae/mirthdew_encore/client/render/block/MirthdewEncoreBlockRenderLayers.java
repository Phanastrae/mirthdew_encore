package phanastrae.mirthdew_encore.client.render.block;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.client.services.XPlatClientInterface;

import static phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks.*;

public class MirthdewEncoreBlockRenderLayers {

    public static void init() {
        putBlocks(RenderType.cutout(),
                DREAMSEED,
                SLUMBERSOCKET,
                VERIC_DREAMSNARE,

                CLINKERA_DOOR,
                CLINKERA_TRAPDOOR,

                RHEUMBRISTLES,
                POTTED_RHEUMBRISTLES,
                SOULSPOT_MUSHRHEUM,
                POTTED_SOULSPOT_MUSHRHEUM,

                ORANGE_FOGHAIR,
                LIME_FOGHAIR,
                CYAN_FOGHAIR,
                MAGNETA_FOGHAIR,
                POTTED_ORANGE_FOGHAIR,
                POTTED_LIME_FOGHAIR,
                POTTED_CYAN_FOGHAIR,
                POTTED_MAGNETA_FOGHAIR,

                DECIDRHEUM_DOOR,
                DECIDRHEUM_TRAPDOOR,

                PSYRITE_DOOR,
                PSYRITE_TRAPDOOR,
                PSYRITE_GRATE,
                PSYRITE_GRATE_SLAB,
                PSYRITE_BARS,
                PSYRITE_LATTICE
        );

        putBlocks(RenderType.cutoutMipped(),
                CLINKERA_LATTICE,
                DECIDRHEUM_LATTICE,
                DECIDRHEUM_LEAVES
        );

        putBlocks(RenderType.translucent(),
                SLUMBERVEIL
        );
    }

    private static void putBlocks(RenderType renderLayer, Block... blocks) {
        XPlatClientInterface.INSTANCE.registerBlockRenderLayers(renderLayer, blocks);
    }
}