package phanastrae.mirthdew_encore.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.card_spell.PlayerEntityMirthData;
import phanastrae.mirthdew_encore.duck.PlayerDuckInterface;

public class MirthdewEncorePlayerEntityAttachment {

    private final Player player;
    private final PlayerEntityHungerData hungerData;
    private final PlayerEntityMirthData mirthData;

    public MirthdewEncorePlayerEntityAttachment(Player player) {
        this.player = player;
        this.hungerData = new PlayerEntityHungerData(player);
        this.mirthData = new PlayerEntityMirthData();
    }

    public void tick() {
        this.hungerData.tick();
    }

    public void writeNbt(CompoundTag nbt) {
        CompoundTag hungerData = new CompoundTag();
        this.hungerData.writeNbt(hungerData);
        nbt.put("HungerData", hungerData);

        CompoundTag mirthData = new CompoundTag();
        this.mirthData.writeNbt(mirthData);
        nbt.put("MirthData", mirthData);
    }

    public void readNbt(CompoundTag nbt) {
        if(nbt.contains("HungerData", Tag.TAG_COMPOUND)) {
            CompoundTag hungerData = nbt.getCompound("HungerData");
            this.hungerData.readNbt(hungerData);
        }
        if(nbt.contains("MirthData", Tag.TAG_COMPOUND)) {
            CompoundTag mirthData = nbt.getCompound("MirthData");
            this.mirthData.readNbt(mirthData);
        }
    }

    public PlayerEntityHungerData getHungerData() {
        return hungerData;
    }

    public PlayerEntityMirthData getMirthData() {
        return mirthData;
    }

    public static MirthdewEncorePlayerEntityAttachment fromPlayer(Player player) {
        return ((PlayerDuckInterface)player).mirthdew_encore$getAttachment();
    }
}
