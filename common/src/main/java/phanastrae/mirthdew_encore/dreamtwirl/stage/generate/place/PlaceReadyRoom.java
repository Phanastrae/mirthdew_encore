package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.structure.intermediate.BoxedContainer;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateGenLevel;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateStructureStorage;

import java.util.List;

public class PlaceReadyRoom {

    private boolean isEntrance = false;
    private final Room room;
    private final int roomId;

    private List<LychsealDoorEntry> placeAfter = new ObjectArrayList<>();

    private boolean isPlaced = false;
    private boolean canPlace = false;

    @Nullable
    private BlockPos placementOrigin;
    private int spawnTime = 0;
    private int maxSpawnTime = 0;
    private boolean spawnInstantly = false;

    @Nullable
    private IntermediateStructureStorage intermediateStructureStorage;
    private boolean storageFilled = false;

    public PlaceReadyRoom(Room prefab, int roomId) {
        this.room = prefab;
        this.roomId = roomId;
    }

    public void beginPlacementImmediately() {
        this.spawnInstantly = true;
        this.beginPlacement(this.room.getBoundingBox().getCenter());
    }

    public void beginPlacementFromDoor(RoomDoor startDoor) {
        this.beginPlacement(startDoor.getPos().immutable());
    }

    public void beginPlacement(BlockPos startPos) {
        if(!this.canPlace) {
            this.canPlace = true;
            this.placementOrigin = startPos;
        }
    }

    public void createStructure(ServerLevel level, BoundingBox stageBB) {
        if(this.intermediateStructureStorage == null) {
            this.intermediateStructureStorage = new IntermediateStructureStorage();
        }
        IntermediateGenLevel igl = new IntermediateGenLevel(this.intermediateStructureStorage, level);
        if(RoomPrePlacement.placeStructure(this.room, level, igl, stageBB, this.isEntrance, this.roomId)) {
            this.storageFilled = true;

            BoundingBox storageBox = intermediateStructureStorage.calculateBoundingBox();
            if(storageBox == null || this.placementOrigin == null) {
                this.maxSpawnTime = 0;
            } else {
                this.maxSpawnTime = 0;
                for(int i = 0; i < 8; i++) {
                    int x = ((i & 0x1) == 0) ? storageBox.minX() : storageBox.maxX();
                    int y = ((i & 0x2) == 0) ? storageBox.minY() : storageBox.maxY();
                    int z = ((i & 0x4) == 0) ? storageBox.minZ() : storageBox.maxZ();
                    BlockPos pos = new BlockPos(x, y, z);

                    int candidateMaxSpawnTime = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, pos, true, RoomActivePlacement.MAX_NOISE_DELAY_TICKS);
                    if(candidateMaxSpawnTime > this.maxSpawnTime) {
                        this.maxSpawnTime = candidateMaxSpawnTime;
                    }
                }
            }
        }
    }

    public void tick(ServerLevel level, BoundingBox stageBB, DreamtwirlStage stage) {
        if(this.canPlace && !this.isPlaced) {
            if(!this.storageFilled) {
                this.createStructure(level, stageBB);
            }

            if(this.spawnTime <= this.maxSpawnTime) {
                if(this.placeForTime(level, this.spawnTime)) {
                    this.spawnTime++;
                }
            } else {
                if(this.place(level)) {
                    this.intermediateStructureStorage = null;
                    this.storageFilled = false;

                    // empty lychseals should already all be open, but do it again just in case
                    this.openLychseal("");

                    // TODO serialization
                    stage.setDirty();
                }
            }
        }
    }

    public boolean placeForTime(ServerLevel level, int time) {
        if(!this.storageFilled || this.intermediateStructureStorage == null) {
            return false;
        } else {
            // TODO limit to loaded chunks

            RandomSource random = level.getRandom();

            // place blocks
            this.intermediateStructureStorage.forEachContainer((sectionPos, boxedContainer) -> {
                BoundingBox box = boxedContainer.getBox();
                if(box == null) return;

                BoxedContainer fragile = this.intermediateStructureStorage.getFragileContainer(sectionPos);
                if(fragile == null) {
                    fragile = new BoxedContainer();
                    this.intermediateStructureStorage.addFragileContainer(sectionPos, fragile);
                }

                int mx = sectionPos.minBlockX();
                int my = sectionPos.minBlockY();
                int mz = sectionPos.minBlockZ();

                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                for(int x = box.minX(); x <= box.maxX(); x++) {
                    for(int y = box.minY(); y <= box.maxY(); y++) {
                        for(int z = box.minZ(); z <= box.maxZ(); z++) {
                            BlockState state = boxedContainer.get(x, y, z);

                            if(!state.is(Blocks.STRUCTURE_VOID)) {
                                mutableBlockPos.set(mx + x, my + y, mz + z);

                                int targetTimeFoam = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, mutableBlockPos, false, mx + x, mz + z);
                                int targetTimeBlock = RoomActivePlacement.getTimeToReachPos(this.placementOrigin, mutableBlockPos, true, mx + x, mz + z);
                                // TODO optimise
                                if(time == targetTimeBlock || time == targetTimeFoam) {
                                    BlockState targetState = boxedContainer.get(x, y, z);
                                    if(targetState.is(Blocks.STRUCTURE_VOID)) {
                                        continue;
                                    }
                                    BlockState oldState = level.getBlockState(mutableBlockPos);

                                    if(time == targetTimeBlock) {
                                        // place block
                                        if(!targetState.isAir() || !oldState.isAir()) {
                                            if(RoomActivePlacement.isStateFragile(targetState, level, mutableBlockPos)) {
                                                fragile.set(x, y, z, targetState);
                                            } else {
                                                RoomActivePlacement.setBlock(level, mutableBlockPos, targetState, true);
                                                RoomActivePlacement.playBlockPlaceEffects(level, random, mutableBlockPos);
                                            }
                                        }
                                    } else {
                                        // place foam
                                        if(!targetState.isAir() || !oldState.isAir()) {
                                            BlockState newState = MirthdewEncoreBlocks.MEMORY_FOAM.defaultBlockState();
                                            RoomActivePlacement.setBlock(level, mutableBlockPos, newState, true);
                                            RoomActivePlacement.playFoamPlaceEffects(level, random, mutableBlockPos);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            });
            for(LychsealDoorEntry entry : this.placeAfter) {
                BlockPos doorPos = entry.startDoor.getPos();
                if(RoomActivePlacement.getTimeToReachPos(this.placementOrigin, doorPos, false, doorPos.getX(), doorPos.getZ()) == time) {
                    if(!entry.startDoor.hasTargetLychseal()) {
                        entry.targetRoom.beginPlacementFromDoor(entry.targetDoor);
                    }
                }
            }

            return true;
        }
    }

    public boolean place(ServerLevel level) {
        if(!this.storageFilled || this.intermediateStructureStorage == null) {
            return false;
        } else {
            // TODO limit to loaded chunks

            RandomSource random = level.getRandom();

            // place blocks
            this.intermediateStructureStorage.forEachFragileContainer((sectionPos, boxedContainer) -> {
                BoundingBox box = boxedContainer.getBox();
                if(box == null) return;

                int mx = sectionPos.minBlockX();
                int my = sectionPos.minBlockY();
                int mz = sectionPos.minBlockZ();

                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                for(int x = box.minX(); x <= box.maxX(); x++) {
                    for(int y = box.minY(); y <= box.maxY(); y++) {
                        for(int z = box.minZ(); z <= box.maxZ(); z++) {
                            BlockState state = boxedContainer.get(x, y, z);

                            if(!state.is(Blocks.STRUCTURE_VOID)) {
                                mutableBlockPos.set(mx + x, my + y, mz + z);

                                RoomActivePlacement.setBlock(level, mutableBlockPos, state, false);
                                RoomActivePlacement.playBlockPlaceEffects(level, random, mutableBlockPos);
                            }
                        }
                    }
                }
            });

            // place block entities
            this.intermediateStructureStorage.forEachBlockEntity(((blockPos, blockEntity) -> {
                if(level.getBlockState(blockPos).getBlock().equals(blockEntity.getBlockState().getBlock())) {
                    level.setBlockEntity(blockEntity);
                }
            }));

            // update blocks
            this.intermediateStructureStorage.forEachContainer(((sectionPos, boxedContainer) -> {
                BoundingBox box = boxedContainer.getBox();
                if(box == null) return;

                int mx = sectionPos.minBlockX();
                int my = sectionPos.minBlockY();
                int mz = sectionPos.minBlockZ();

                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                for(int x = box.minX(); x <= box.maxX(); x++) {
                    for(int y = box.minY(); y <= box.maxY(); y++) {
                        for(int z = box.minZ(); z <= box.maxZ(); z++) {
                            BlockState state = boxedContainer.get(x, y, z);

                            if(!state.is(Blocks.STRUCTURE_VOID)) {
                                mutableBlockPos.set(mx + x, my + y, mz + z);

                                level.blockUpdated(mutableBlockPos, state.getBlock());
                                if (state.hasAnalogOutputSignal()) {
                                    level.updateNeighbourForOutputSignal(mutableBlockPos, state.getBlock());
                                }
                                RoomActivePlacement.tryUpdateSelf(level, mutableBlockPos, state);
                            }
                        }
                    }
                }
            }));

            // place entities
            this.intermediateStructureStorage.forEachEntity(level::addFreshEntity);

            this.isPlaced = true;
            return true;
        }
    }

    public void setEmptySealNeighborsCanSpawn() {
        this.openLychseal("");
    }

    public void openLychseal(String lychsealName) {
        for(LychsealDoorEntry entry : this.placeAfter) {
            if(entry.lychsealName.equals(lychsealName)) {
                entry.targetRoom.beginPlacementFromDoor(entry.targetDoor);
            }
        }
    }

    public void setIsEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void addToPlaceAfter(String lychseal, RoomDoor startDoor, RoomDoor targetDoor, PlaceReadyRoom room) {
        this.placeAfter.add(new LychsealDoorEntry(lychseal, startDoor, targetDoor, room));
    }

    public Room getRoom() {
        return room;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean shouldTick() {
        return this.canPlace && !this.isPlaced;
    }

    public record LychsealDoorEntry(String lychsealName, RoomDoor startDoor, RoomDoor targetDoor, PlaceReadyRoom targetRoom) {
    }
}
