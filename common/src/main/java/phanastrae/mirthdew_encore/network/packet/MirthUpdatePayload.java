package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record MirthUpdatePayload(long mirth) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, MirthUpdatePayload> PACKET_CODEC = CustomPacketPayload.codec(MirthUpdatePayload::write, MirthUpdatePayload::new);
    public static final Type<MirthUpdatePayload> PACKET_ID = new Type<>(MirthdewEncore.id("mirth_update"));

    public MirthUpdatePayload(FriendlyByteBuf buf) {
        this(buf.readLong());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeLong(this.mirth);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
