package phanastrae.mirthdew_encore.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import phanastrae.mirthdew_encore.MirthdewEncore;

public record FoodDebtUpdatePayload(int foodLevelDebt) implements CustomPayload {
    public static final PacketCodec<RegistryByteBuf, FoodDebtUpdatePayload> PACKET_CODEC = CustomPayload.codecOf(FoodDebtUpdatePayload::write, FoodDebtUpdatePayload::new);
    public static final CustomPayload.Id<FoodDebtUpdatePayload> PACKET_ID = new CustomPayload.Id<>(MirthdewEncore.id("food_debt_update"));

    public FoodDebtUpdatePayload(PacketByteBuf buf) {
        this(buf.readInt());
    }

    public void write(PacketByteBuf buf) {
        buf.writeInt(this.foodLevelDebt);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return PACKET_ID;
    }
}
