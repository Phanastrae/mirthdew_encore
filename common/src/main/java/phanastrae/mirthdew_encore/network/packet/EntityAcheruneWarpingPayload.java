package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record EntityAcheruneWarpingPayload(int id, boolean warping) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, EntityAcheruneWarpingPayload> PACKET_CODEC = CustomPacketPayload.codec(EntityAcheruneWarpingPayload::write, EntityAcheruneWarpingPayload::new);
    public static final Type<EntityAcheruneWarpingPayload> PACKET_ID = new Type<>(MirthdewEncore.id("entity_acherune_warping"));

    public EntityAcheruneWarpingPayload(FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeBoolean(this.warping);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
