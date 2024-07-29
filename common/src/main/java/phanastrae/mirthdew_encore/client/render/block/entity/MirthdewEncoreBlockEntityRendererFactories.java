package phanastrae.mirthdew_encore.client.render.block.entity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.mirthdew_encore.block.entity.MirthdewEncoreBlockEntityTypes;
import phanastrae.mirthdew_encore.mixin.client.BlockEntityRenderersAccessor;

public class MirthdewEncoreBlockEntityRendererFactories {

    public static void init() {
        register(MirthdewEncoreBlockEntityTypes.SLUMBERSOCKET, SlumbersocketBlockEntityRenderer::new);
        register(MirthdewEncoreBlockEntityTypes.VERIC_DREAMSNARE, VericDreamsnareBlockEntityRenderer::new);
    }

    public static <T extends BlockEntity> void register(BlockEntityType<? extends T> type, BlockEntityRendererProvider<T> factory) {
        BlockEntityRenderersAccessor.invokeRegister(type, factory);
    }
}
