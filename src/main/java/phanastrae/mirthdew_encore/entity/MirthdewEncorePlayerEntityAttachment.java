package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import phanastrae.mirthdew_encore.duck.PlayerEntityDuckInterface;

public class MirthdewEncorePlayerEntityAttachment {

    private final PlayerEntity player;
    private final PlayerEntityHungerData entityHungerData;

    public MirthdewEncorePlayerEntityAttachment(PlayerEntity player) {
        this.player = player;
        this.entityHungerData = new PlayerEntityHungerData(player);
    }

    public void tick() {
        this.entityHungerData.tick();
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound hungerData = new NbtCompound();
        this.entityHungerData.writeNbt(hungerData);
        nbt.put("HungerData", hungerData);
    }

    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("HungerData", NbtElement.COMPOUND_TYPE)) {
            NbtCompound hungerData = nbt.getCompound("HungerData");
            this.entityHungerData.readNbt(hungerData);
        }
    }

    public PlayerEntityHungerData getEntityHungerData() {
        return entityHungerData;
    }

    public static MirthdewEncorePlayerEntityAttachment fromPlayer(PlayerEntity player) {
        return ((PlayerEntityDuckInterface)player).mirthdew_encore$getAttachment();
    }
}
