package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record SetLychsealMarkerBlockPayload(BlockPos blockPos, String lychsealTarget) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetLychsealMarkerBlockPayload> PACKET_CODEC = CustomPacketPayload.codec(SetLychsealMarkerBlockPayload::write, SetLychsealMarkerBlockPayload::new);
    public static final Type<SetLychsealMarkerBlockPayload> PACKET_ID = new Type<>(MirthdewEncore.id("set_lychseal_marker_block"));

    public SetLychsealMarkerBlockPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos);
        buf.writeUtf(this.lychsealTarget);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
