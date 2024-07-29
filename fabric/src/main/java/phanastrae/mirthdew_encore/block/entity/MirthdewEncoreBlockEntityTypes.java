package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

public class MirthdewEncoreBlockEntityTypes {

    public static final BlockEntityType<SlumbersocketBlockEntity> SLUMBERSOCKET = create(
            "slumbersocket", SlumbersocketBlockEntity::new, MirthdewEncoreBlocks.SLUMBERSOCKET);

    public static final BlockEntityType<VericDreamsnareBlockEntity> VERIC_DREAMSNARE = create(
            "veric_dreamsnare", VericDreamsnareBlockEntity::new, MirthdewEncoreBlocks.VERIC_DREAMSNARE);

    public static void init() {
        register(SLUMBERSOCKET, "slumbersocket");
        register(VERIC_DREAMSNARE, "veric_dreamsnare");
    }

    private static void register(BlockEntityType<?> blockEntityType, String name) {
        ResourceLocation identifier = MirthdewEncore.id(name);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... blocks) {
        if (blocks.length == 0) {
            MirthdewEncore.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }
        return BlockEntityType.Builder.<T>of(factory, blocks).build();
    }
}
