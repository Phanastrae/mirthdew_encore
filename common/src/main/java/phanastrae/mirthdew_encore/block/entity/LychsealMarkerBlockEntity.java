package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LychsealMarkerBlockEntity extends BlockEntity {
    public static final String KEY_LYCHSEAL_NAME = "name";

    private String lychsealName = "";

    public LychsealMarkerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.LYCHSEAL_MARKER, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putString(KEY_LYCHSEAL_NAME, this.lychsealName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains(KEY_LYCHSEAL_NAME, CompoundTag.TAG_STRING)) {
            this.lychsealName = tag.getString(KEY_LYCHSEAL_NAME);
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public void setLychsealName(String lychsealName) {
        this.lychsealName = lychsealName;
        this.setChanged();
    }

    public String getLychsealName() {
        return this.lychsealName;
    }
}
