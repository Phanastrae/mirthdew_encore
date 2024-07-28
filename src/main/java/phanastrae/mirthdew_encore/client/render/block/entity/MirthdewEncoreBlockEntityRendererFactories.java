package phanastrae.mirthdew_encore.client.render.block.entity;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;

public class MirthdewEncoreBlockEntityRendererFactories {

    public static void init() {
        register(MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, SlumbersocketBlockEntityRenderer::new);
        register(MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE, VericDreamsnareBlockEntityRenderer::new);
    }

    public static <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererFactory<T> factory) {
        BlockEntityRendererFactories.register(type, factory);
    }
}
