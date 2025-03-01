package phanastrae.mirthdew_encore.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.world.level.block.TransparentBlock;

public class MemoryFoamBlock extends TransparentBlock {
    public static final MapCodec<MemoryFoamBlock> CODEC = simpleCodec(MemoryFoamBlock::new);

    @Override
    public MapCodec<MemoryFoamBlock> codec() {
        return CODEC;
    }

    public MemoryFoamBlock(Properties properties) {
        super(properties);
    }
}
