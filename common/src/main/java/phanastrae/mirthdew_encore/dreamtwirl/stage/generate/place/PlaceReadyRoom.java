package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateGenLevel;
import phanastrae.mirthdew_encore.structure.intermediate.IntermediateStructureStorage;

import java.util.List;

public class PlaceReadyRoom {

    private boolean isEntrance = false;
    private final Room room;
    private boolean isPlaced = false;
    private boolean canPlace = false;
    private List<Pair<String, PlaceReadyRoom>> placeAfter = new ObjectArrayList<>();
    private final int roomId;

    private int spawnTime = 0;
    private final int maxSpawnTime;

    @Nullable
    private IntermediateStructureStorage intermediateStructureStorage;
    private boolean storageFilled = false;

    public PlaceReadyRoom(Room prefab, int roomId) {
        this.room = prefab;
        this.roomId = roomId;

        this.maxSpawnTime = 30; // TODO make this be based on the room type?
    }

    public void createStructure(ServerLevel level, BoundingBox stageBB) {
        if(this.intermediateStructureStorage == null) {
            this.intermediateStructureStorage = new IntermediateStructureStorage();
        }
        IntermediateGenLevel igl = new IntermediateGenLevel(this.intermediateStructureStorage, level);
        if(RoomPlacer.placeStructure(this.room, level, igl, stageBB, this.isEntrance, this.roomId)) {
            this.storageFilled = true;
        }
    }

    public void tick(ServerLevel level, BoundingBox stageBB, DreamtwirlStage stage) {
        if(this.canPlace && !this.isPlaced) {
            if(!this.storageFilled) {
                this.createStructure(level, stageBB);
            }

            if(this.spawnTime < this.maxSpawnTime) {
                this.spawnTime++;
            }

            if(this.spawnTime >= this.maxSpawnTime) {
                if(this.place(level, stageBB)) {
                    RoomPlacer.spawnParticles(level, this.getRoom());
                    this.openLychseal("");

                    // TODO serialization
                    stage.setDirty();
                }
            }
        }
    }

    public boolean place(ServerLevel level, BoundingBox stageBB) {
        if(!this.storageFilled || this.intermediateStructureStorage == null) {
            return false;
        } else {
            // TODO limit to loaded chunks

            // place blocks
            this.intermediateStructureStorage.forEachContainer((sectionPos, boxedContainer) -> {
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

                                setBlock(level, mutableBlockPos, state, false);
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
                                tryUpdateSelf(level, mutableBlockPos, state);
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




        /*
        if(!this.isPlaced) {
            if(RoomPlacer.placeStructure(this.room, level, stageBB, this.isEntrance, this.roomId)) {
                this.isPlaced = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
    }

    public static void setBlock(ServerLevel level, BlockPos pos, BlockState state, boolean updateNeighbors) {
        level.setBlock(pos, state, updateNeighbors ? 3 : 2, 512);
    }

    public static void tryUpdateSelf(ServerLevel level, BlockPos pos, BlockState state) {
        FluidState fluidState = state.getFluidState();
        if (!fluidState.isEmpty()) {
            fluidState.tick(level, pos);
        }

        Block block = state.getBlock();
        if (!(block instanceof LiquidBlock)) {
            BlockState newState = Block.updateFromNeighbourShapes(state, level, pos);
            if (!newState.equals(state)) {
                level.setBlock(pos, newState, 20);
            }
        }
    }

    public void setEmptySealNeighborsCanSpawn() {
        this.openLychseal("");
    }

    public void openLychseal(String lychsealName) {
        for(Pair<String, PlaceReadyRoom> pairs : placeAfter) {
            if(pairs.left().equals(lychsealName)) {
                pairs.right().setCanPlace(true);
            }
        }
    }

    public void setIsEntrance(boolean entrance) {
        isEntrance = entrance;
    }

    public boolean isEntrance() {
        return isEntrance;
    }

    public void addToPlaceAfter(String lychseal, PlaceReadyRoom room) {
        this.placeAfter.add(Pair.of(lychseal, room));
    }

    public Room getRoom() {
        return room;
    }

    public boolean isPlaced() {
        return isPlaced;
    }

    public boolean canPlace() {
        return canPlace;
    }

    public void setCanPlace(boolean canPlace) {
        this.canPlace = canPlace;
    }

    public int getRoomId() {
        return roomId;
    }
}
