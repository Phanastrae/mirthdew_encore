package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

public class MirthdewEncoreBlockEntityTypes {

    public static final BlockEntityType<VericDreamsnareBlockEntity> VERIC_DREAMSNARE = create("veric_dreamsnare", VericDreamsnareBlockEntity::new, MirthdewEncoreBlocks.VERIC_DREAMSNARE);

    public static void init() {
        register(VERIC_DREAMSNARE, "veric_dreamsnare");
    }

    private static void register(BlockEntityType<?> blockEntityType, String name) {
        Identifier identifier = MirthdewEncore.id(name);
        Registry.register(Registries.BLOCK_ENTITY_TYPE, identifier, blockEntityType);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntityFactory<? extends T> factory, Block... blocks) {
        if (blocks.length == 0) {
            MirthdewEncore.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }
        return BlockEntityType.Builder.<T>create(factory, blocks).build();
    }
}
