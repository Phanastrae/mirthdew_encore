package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public record LocationComponent(double x, double y, double z, ResourceLocation dimensionId) {
    public static final Codec<LocationComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.DOUBLE.fieldOf("x").forGetter(LocationComponent::x),
                            Codec.DOUBLE.fieldOf("y").forGetter(LocationComponent::y),
                            Codec.DOUBLE.fieldOf("z").forGetter(LocationComponent::z),
                            ResourceLocation.CODEC.fieldOf("dimension").forGetter(LocationComponent::dimensionId)
                    )
                    .apply(instance, LocationComponent::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LocationComponent> PACKET_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE,
            LocationComponent::x,
            ByteBufCodecs.DOUBLE,
            LocationComponent::y,
            ByteBufCodecs.DOUBLE,
            LocationComponent::z,
            ResourceLocation.STREAM_CODEC,
            LocationComponent::dimensionId,
            LocationComponent::new
    );

    public static LocationComponent fromPosAndWorld(Vec3 pos, Level world) {
        return new LocationComponent(pos.x, pos.y, pos.z, world.dimension().location());
    }

    public Vec3 getPos() {
        return new Vec3(this.x, this.y, this.z);
    }

    @Nullable
    public Level getWorld(MinecraftServer server) {
        return server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionId));
    }
}
