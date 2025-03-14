package phanastrae.mirthdew_encore.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import phanastrae.mirthdew_encore.MirthdewEncore;

public final class MirthdewEncoreBlockTags {
    public static final TagKey<Block> DREAMSPECK_OPAQUE = of("dreamspeck_opaque");
    public static final TagKey<Block> IS_XP_MANIPULATOR = of("is_xp_manipulator");
    public static final TagKey<Block> IS_NETHERITE = of("is_netherite");
    public static final TagKey<Block> IS_PURPUR = of("is_purpur");
    public static final TagKey<Block> IS_SCULK = of("is_sculk");
    public static final TagKey<Block> IS_SOUL_FILLED = of("is_soul_filled");
    public static final TagKey<Block> IS_NETHER_WART = of("is_nether_wart");

    public static final TagKey<Block> NOVA_CLAG = of("nova_clag");

    private static TagKey<Block> of(String id) {
        return TagKey.create(Registries.BLOCK, MirthdewEncore.id(id));
    }
}
