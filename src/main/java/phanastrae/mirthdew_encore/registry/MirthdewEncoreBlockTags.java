package phanastrae.mirthdew_encore.registry;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import phanastrae.mirthdew_encore.MirthdewEncore;

public final class MirthdewEncoreBlockTags {
    public static final TagKey<Block> DREAMSPECK_OPAQUE = of("dreamspeck_opaque");
    public static final TagKey<Block> IS_XP_MANIPULATOR = of("is_xp_manipulator");
    public static final TagKey<Block> IS_NETHERITE = of("is_netherite");
    public static final TagKey<Block> IS_PURPUR = of("is_purpur");
    public static final TagKey<Block> IS_SCULK = of("is_sculk");
    public static final TagKey<Block> IS_SOUL_FILLED = of("is_soul_filled");
    public static final TagKey<Block> IS_NETHER_WART = of("is_nether_wart");

    private static TagKey<Block> of(String id) {
        return TagKey.of(RegistryKeys.BLOCK, MirthdewEncore.id(id));
    }
}
