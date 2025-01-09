package phanastrae.mirthdew_encore.block;

import com.google.common.collect.ImmutableMap;
import net.minecraft.world.level.block.Block;

import java.util.Map;

import static phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks.*;

public class MirthdewEncoreLogStripping {
    public static final Map<Block, Block> MIRTHDEW_STRIPPABLES = new ImmutableMap.Builder<Block, Block>()
            .put(DECIDRHEUM_WOOD, STRIPPED_DECIDRHEUM_WOOD)
            .put(DECIDRHEUM_LOG, STRIPPED_DECIDRHEUM_LOG)
            .build();
}
