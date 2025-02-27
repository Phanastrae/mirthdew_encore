package phanastrae.mirthdew_encore.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;

public class LychsealBlockEntity extends BlockEntity {
    public static final String KEY_LYCHSEAL_NAME = "name";
    public static final String KEY_LINKED_ROOM_ID = "room_id";

    int roomId = -1;
    String lychsealName = "";

    public LychsealBlockEntity(BlockPos pos, BlockState blockState) {
        super(MirthdewEncoreBlockEntityTypes.LYCHSEAL, pos, blockState);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putString(KEY_LYCHSEAL_NAME, this.lychsealName);
        tag.putInt(KEY_LINKED_ROOM_ID, this.roomId);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        if(tag.contains(KEY_LYCHSEAL_NAME, CompoundTag.TAG_STRING)) {
            this.lychsealName = tag.getString(KEY_LYCHSEAL_NAME);
        }
        if(tag.contains(KEY_LINKED_ROOM_ID, CompoundTag.TAG_INT)) {
            this.roomId = tag.getInt(KEY_LINKED_ROOM_ID);
        }
    }

    public static void tickClient(Level level, BlockPos pos, BlockState state, LychsealBlockEntity blockEntity) {

    }

    public static void tickServer(Level level, BlockPos pos, BlockState state, LychsealBlockEntity blockEntity) {

    }

    public void open(Level level, BlockPos pos, BlockState state) {
        level.destroyBlock(pos, false);

        DreamtwirlStage stage = DreamtwirlStageManager.getStage(level, pos);
        if(stage != null) {
            stage.openLychseal(this.roomId, this.lychsealName);
        }
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
        this.setChanged();
    }

    public void setLychsealName(String lychsealName) {
        this.lychsealName = lychsealName;
        this.setChanged();
    }
}
