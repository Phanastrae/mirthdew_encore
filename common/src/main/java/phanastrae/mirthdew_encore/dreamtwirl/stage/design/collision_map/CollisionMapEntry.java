package phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map;

import net.minecraft.world.level.levelgen.structure.BoundingBox;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;

public class CollisionMapEntry {

    private final RoomCollisionMap collisionMap;
    private final Room room;

    private boolean existsInMap = false;
    private BoundingBox boundingBox;

    public CollisionMapEntry(RoomCollisionMap collisionMap, Room room) {
        this.collisionMap = collisionMap;
        this.room = room;

        this.boundingBox = this.room.getBoundingBox();
    }

    public BoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public boolean existsInMap() {
        return existsInMap;
    }

    public void setExistsInMap(boolean existsInMap) {
        this.existsInMap = existsInMap;
    }

    public void updateOnMap() {
        this.collisionMap.remove(this);
        this.boundingBox = this.room.getBoundingBox();
        this.collisionMap.add(this);
    }
}
