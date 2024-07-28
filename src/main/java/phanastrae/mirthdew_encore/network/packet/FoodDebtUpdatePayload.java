package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record FoodDebtUpdatePayload(int foodLevelDebt) implements CustomPacketPayload {
    public static final StreamCodec<RegistryFriendlyByteBuf, FoodDebtUpdatePayload> PACKET_CODEC = CustomPacketPayload.codec(FoodDebtUpdatePayload::write, FoodDebtUpdatePayload::new);
    public static final CustomPacketPayload.Type<FoodDebtUpdatePayload> PACKET_ID = new CustomPacketPayload.Type<>(MirthdewEncore.id("food_debt_update"));

    public FoodDebtUpdatePayload(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeInt(this.foodLevelDebt);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
