package phanastrae.mirthdew_encore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.MirthdewEncore;

public final class MirthdewEncoreItemTags {
    public static final TagKey<Item> DECIDRHEUM_LOGS = of("decidrheum_logs");

    private static TagKey<Item> of(String id) {
        return TagKey.create(Registries.ITEM, MirthdewEncore.id(id));
    }
}
