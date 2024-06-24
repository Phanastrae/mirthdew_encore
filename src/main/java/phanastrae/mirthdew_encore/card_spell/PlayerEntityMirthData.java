package phanastrae.mirthdew_encore.card_spell;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

public class PlayerEntityMirthData {

    private long mirth;

    public PlayerEntityMirthData() {
    }

    public void writeNbt(NbtCompound nbt) {
        nbt.putLong("mirth", this.mirth);
    }

    public void readNbt(NbtCompound nbt) {
        if(nbt.contains("mirth", NbtElement.INT_TYPE)) {
            this.mirth = nbt.getInt("mirth");
        }
    }

    public void setMirth(long mirth) {
        this.mirth = mirth;
    }

    public long getMirth() {
        return this.mirth;
    }

    public void addMirth(long mirth, long maxMirth) {
        if(mirth > maxMirth) {
            mirth = maxMirth;
        }
        long newMirth = this.mirth + mirth;
        if(newMirth > maxMirth) newMirth = maxMirth;
        if(newMirth > this.mirth) {
            this.mirth = newMirth;
        }
    }

    public static PlayerEntityMirthData fromPlayer(PlayerEntity player) {
        return MirthdewEncorePlayerEntityAttachment.fromPlayer(player).getMirthData();
    }
}
