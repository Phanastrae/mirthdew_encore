package phanastrae.mirthdew_encore.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public record BlockPosDimensional(int x, int y, int z, ResourceLocation dimensionId) {
    public static final Codec<BlockPosDimensional> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.INT.fieldOf("x").forGetter(BlockPosDimensional::x),
                            Codec.INT.fieldOf("y").forGetter(BlockPosDimensional::y),
                            Codec.INT.fieldOf("z").forGetter(BlockPosDimensional::z),
                            ResourceLocation.CODEC.fieldOf("dimension").forGetter(BlockPosDimensional::dimensionId)
                    )
                    .apply(instance, BlockPosDimensional::new)
    );

    public static BlockPosDimensional fromPosAndLevel(BlockPos pos, Level level) {
        return new BlockPosDimensional(pos.getX(), pos.getY(), pos.getZ(), level.dimension().location());
    }

    public BlockPos getPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public @Nullable Level getLevel(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
    }

    @Override
    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if (o instanceof BlockPosDimensional bpd) {
            return this.x == bpd.x && this.y == bpd.y && this.z == bpd.z && this.dimensionId.equals(bpd.dimensionId);
        } else {
            return false;
        }
    }
}
