package phanastrae.mirthdew_encore.dreamtwirl.stage.design;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import phanastrae.mirthdew_encore.dreamtwirl.stage.StageAreaData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map.RoomCollisionMap;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph.RoomGraph;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;

import java.util.List;
import java.util.Optional;

public class StageDesignData {

    private final List<Room> roomList;
    private final RoomCollisionMap collisionMap;
    private final RoomGraph roomGraph;

    public StageDesignData(StageAreaData stageAreaData) {
        this.roomList = new ObjectArrayList<>();
        this.collisionMap = createChunkMap(stageAreaData);
        this.roomGraph = new RoomGraph();
    }

    public static RoomCollisionMap createChunkMap(StageAreaData stageAreaData) {
        ChunkPos minCornerPos = stageAreaData.getRegionPos().getChunkPos(1, 1);
        return new RoomCollisionMap(new Vec3i(minCornerPos.x, stageAreaData.getMinBuildHeight() >> 4, minCornerPos.z), 30, stageAreaData.getHeight() >> 4, 30);
    }

    public void addRoom(Room room) {
        this.roomList.add(room);
        this.collisionMap.addRoom(room);
        this.roomGraph.addRoomToGraph(room);
    }

    public void removeRoom(Room room) {
        this.roomList.remove(room);
        this.collisionMap.removeRoom(room);
        this.roomGraph.removeRoom(room);
    }

    public Optional<Room> getRandomRoom(RandomSource random) {
        if(this.roomList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(this.roomList.get(random.nextInt(this.roomList.size())));
        }
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public RoomCollisionMap getCollisionMap() {
        return collisionMap;
    }

    public RoomGraph getRoomGraph() {
        return roomGraph;
    }
}
