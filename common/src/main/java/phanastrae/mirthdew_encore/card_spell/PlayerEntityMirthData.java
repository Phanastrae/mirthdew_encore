package phanastrae.mirthdew_encore.card_spell;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import phanastrae.mirthdew_encore.entity.MirthdewEncorePlayerEntityAttachment;

public class PlayerEntityMirthData {

    private long mirth;

    public PlayerEntityMirthData() {
    }

    public void writeNbt(CompoundTag nbt) {
        nbt.putLong("mirth", this.mirth);
    }

    public void readNbt(CompoundTag nbt) {
        if(nbt.contains("mirth", Tag.TAG_LONG)) {
            this.mirth = nbt.getLong("mirth");
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

    public void removeMirth(long mirth) {
        this.mirth = Math.max(this.mirth - mirth, 0);
    }

    public static PlayerEntityMirthData fromPlayer(Player player) {
        return MirthdewEncorePlayerEntityAttachment.fromPlayer(player).getMirthData();
    }
}
