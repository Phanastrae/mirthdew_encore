package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;

import java.util.function.BiConsumer;

public class MirthdewEncoreBlockEntityTypes {

    public static final BlockEntityType<SlumbersocketBlockEntity> SLUMBERSOCKET = create(
            "slumbersocket", SlumbersocketBlockEntity::new, MirthdewEncoreBlocks.SLUMBERSOCKET);

    public static final BlockEntityType<VericDreamsnareBlockEntity> VERIC_DREAMSNARE = create(
            "veric_dreamsnare", VericDreamsnareBlockEntity::new, MirthdewEncoreBlocks.VERIC_DREAMSNARE);

    public static final BlockEntityType<DoorMarkerBlockEntity> DOOR_MARKER = create(
            "door_marker", DoorMarkerBlockEntity::new, MirthdewEncoreBlocks.DOOR_MARKER);

    public static void init(BiConsumer<ResourceLocation, BlockEntityType<?>> r) {
        r.accept(id("slumbersocket"), SLUMBERSOCKET);
        r.accept(id("veric_dreamsnare"), VERIC_DREAMSNARE);
        r.accept(id("door_marker"), DOOR_MARKER);
    }

    private static ResourceLocation id(String path) {
        return MirthdewEncore.id(path);
    }

    private static <T extends BlockEntity> BlockEntityType<T> create(String id, BlockEntityType.BlockEntitySupplier<? extends T> factory, Block... blocks) {
        if (blocks.length == 0) {
            MirthdewEncore.LOGGER.warn("Block entity type {} requires at least one valid block to be defined!", id);
        }
        return BlockEntityType.Builder.<T>of(factory, blocks).build(null);
    }
}
