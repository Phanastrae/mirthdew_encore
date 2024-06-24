package phanastrae.mirthdew_encore.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public record LocationComponent(double x, double y, double z, Identifier dimensionId) {
    public static final Codec<LocationComponent> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.DOUBLE.fieldOf("x").forGetter(LocationComponent::x),
                            Codec.DOUBLE.fieldOf("y").forGetter(LocationComponent::y),
                            Codec.DOUBLE.fieldOf("z").forGetter(LocationComponent::z),
                            Identifier.CODEC.fieldOf("dimension").forGetter(LocationComponent::dimensionId)
                    )
                    .apply(instance, LocationComponent::new)
    );
    public static final PacketCodec<RegistryByteBuf, LocationComponent> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.DOUBLE,
            LocationComponent::x,
            PacketCodecs.DOUBLE,
            LocationComponent::y,
            PacketCodecs.DOUBLE,
            LocationComponent::z,
            Identifier.PACKET_CODEC,
            LocationComponent::dimensionId,
            LocationComponent::new
    );

    public static LocationComponent fromPosAndWorld(Vec3d pos, World world) {
        return new LocationComponent(pos.x, pos.y, pos.z, world.getRegistryKey().getValue());
    }

    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
    }

    @Nullable
    public World getWorld(MinecraftServer server) {
        return server.getWorld(RegistryKey.of(RegistryKeys.WORLD, dimensionId));
    }
}
