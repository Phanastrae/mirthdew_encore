package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record MirthUpdatePayload(long mirth) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, MirthUpdatePayload> PACKET_CODEC = CustomPayload.codecOf(MirthUpdatePayload::write, MirthUpdatePayload::new);
    public static final Id<MirthUpdatePayload> PACKET_ID = new Id<>(MirthdewEncore.id("mirth_update"));

    public MirthUpdatePayload(PacketByteBuf buf) {
        this(buf.readLong());
    }

    public void write(PacketByteBuf buf) {
        buf.writeLong(this.mirth);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
