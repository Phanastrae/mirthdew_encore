package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

public record SetDoorMarkerBlockPayload(BlockPos blockPos, String finalState, String lychsealTarget, RoomDoor.DoorType doorType) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, SetDoorMarkerBlockPayload> PACKET_CODEC = CustomPacketPayload.codec(SetDoorMarkerBlockPayload::write, SetDoorMarkerBlockPayload::new);
    public static final Type<SetDoorMarkerBlockPayload> PACKET_ID = new Type<>(MirthdewEncore.id("set_door_marker_block"));

    public SetDoorMarkerBlockPayload(FriendlyByteBuf buf) {
        this(buf.readBlockPos(), buf.readUtf(), buf.readUtf(), buf.readEnum(RoomDoor.DoorType.class));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.blockPos);
        buf.writeUtf(this.finalState);
        buf.writeUtf(this.lychsealTarget);
        buf.writeEnum(this.doorType);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
