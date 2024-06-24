package phanastrae.mirthdew_encore.entity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.duck.PlayerEntityDuckInterface;

public class MirthdewEncorePlayerEntityAttachment {

    private final PlayerEntity player;
    private final PlayerEntityHungerData hungerData;
    private final PlayerEntityMirthData mirthData;

    public MirthdewEncorePlayerEntityAttachment(PlayerEntity player) {
        this.player = player;
        this.hungerData = new PlayerEntityHungerData(player);
        this.mirthData = new PlayerEntityMirthData();
    }

    public void tick() {
        this.hungerData.tick();
    }

    public void writeNbt(NbtCompound nbt) {
        NbtCompound hungerData = new NbtCompound();
        this.hungerData.writeNbt(hungerData);
        nbt.put("HungerData", hungerData);

        NbtCompound mirthData = new NbtCompound();
        this.mirthData.writeNbt(mirthData);
        nbt.put("MirthData", hungerData);
    }

    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("HungerData", NbtElement.COMPOUND_TYPE)) {
            NbtCompound hungerData = nbt.getCompound("HungerData");
            this.hungerData.readNbt(hungerData);
        }
        if(nbt.contains("MirthData", NbtElement.COMPOUND_TYPE)) {
            NbtCompound mirthData = nbt.getCompound("MirthData");
            this.mirthData.readNbt(mirthData);
        }
    }

    public PlayerEntityHungerData getHungerData() {
        return hungerData;
    }

    public PlayerEntityMirthData getMirthData() {
        return mirthData;
    }

    public static MirthdewEncorePlayerEntityAttachment fromPlayer(PlayerEntity player) {
        return ((PlayerEntityDuckInterface)player).mirthdew_encore$getAttachment();
    }
}
