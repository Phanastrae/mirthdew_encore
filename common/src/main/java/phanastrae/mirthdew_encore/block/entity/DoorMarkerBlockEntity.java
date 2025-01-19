package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

public class DoorMarkerBlockEntity extends BlockEntity {
    public static final String DOOR_TYPE = "door_type";

    private RoomDoor.DoorType doorType = RoomDoor.DoorType.TWOWAY; // TODO serialize etc

    public DoorMarkerBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.DOOR_MARKER, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putString(DOOR_TYPE, this.doorType.getSerializedName());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.doorType = RoomDoor.DoorType.byName(tag.getString(DOOR_TYPE))
                .orElse(
                        RoomDoor.DoorType.TWOWAY
                );
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
}
