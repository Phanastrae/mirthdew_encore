package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

public class DoorMarkerBlockEntity extends BlockEntity {
    public static final String KEY_DOOR_TYPE = "door_type";
    public static final String KEY_TARGET_LYCHSEAL = "target_lychseal";
    public static final String KEY_FINAL_STATE = "final_state";

    private String targetLychsealName = "";
    private String finalState = "minecraft:air";
    private RoomDoor.DoorType doorType = RoomDoor.DoorType.TWOWAY;

    public DoorMarkerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.DOOR_MARKER, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putString(KEY_TARGET_LYCHSEAL, this.targetLychsealName);
        tag.putString(KEY_FINAL_STATE, this.getFinalState());
        tag.putString(KEY_DOOR_TYPE, this.doorType.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains(KEY_FINAL_STATE, CompoundTag.TAG_STRING)) {
            this.finalState = tag.getString(KEY_FINAL_STATE);
        }

        if(tag.contains(KEY_TARGET_LYCHSEAL, CompoundTag.TAG_STRING)) {
            this.targetLychsealName = tag.getString(KEY_TARGET_LYCHSEAL);
        }

        if(tag.contains(KEY_DOOR_TYPE, CompoundTag.TAG_STRING)) {
            this.doorType = RoomDoor.DoorType.byName(tag.getString(KEY_DOOR_TYPE))
                    .orElse(
                            RoomDoor.DoorType.TWOWAY
                    );
        }
    }

    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return this.saveCustomOnly(registries);
    }

    public void setDoorType(RoomDoor.DoorType doorType) {
        this.doorType = doorType;
    }

    public RoomDoor.DoorType getDoorType() {
        return this.doorType;
    }

    public void setTargetLychsealName(String targetLychsealName) {
        this.targetLychsealName = targetLychsealName;
    }

    public String getLychsealTargetName() {
        return this.targetLychsealName;
    }

    public void setFinalState(String finalState) {
        this.finalState = finalState;
    }

    public String getFinalState() {
        return finalState;
    }
}
