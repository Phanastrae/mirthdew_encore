package phanastrae.mirthdew_encore.dreamtwirl.stage.design.collision_map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.SectionPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RoomCollisionMap {

    public final SectionPos basePos;
    public final int sizeX;
    public final int sizeY;
    public final int sizeZ;

    public List<CollisionMapEntry>[][][] map;
    private final Map<Room, CollisionMapEntry> roomToEntryMap = new Object2ObjectOpenHashMap<>();

    public RoomCollisionMap(SectionPos basePos, int sizeX, int sizeY, int sizeZ) {
        this.basePos = basePos;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        // TODO: consider not making 14400 lists and instead doing something more intelligent here ie subdivide into smaller chunkmaps for an octree sort of thing
        this.map = new List[sizeX][sizeY][sizeZ];
        for(int x = 0; x < sizeX; x++) {
            List<CollisionMapEntry>[][] xarr = new List[sizeY][sizeZ];
            this.map[x] = xarr;
            for(int y = 0; y < sizeY; y++) {
                List<CollisionMapEntry>[] yarr = new List[sizeZ];
                xarr[y] = yarr;
                for(int z = 0; z < sizeZ; z++) {
                    List<CollisionMapEntry> zList = new ArrayList<>();
                    yarr[z] = zList;
                }
            }
        }
    }

    public void clear() {
        for(int x = 0; x < sizeX; x++) {
            for(int y = 0; y < sizeY; y++) {
                for(int z = 0; z < sizeZ; z++) {
                    this.map[x][y][z].clear();
                }
            }
        }
        this.roomToEntryMap.clear();
    }

    public void addRoom(Room room) {
        if(!this.roomToEntryMap.containsKey(room)) {
            CollisionMapEntry entry = new CollisionMapEntry(this, room);
            this.roomToEntryMap.put(room, entry);
            this.add(entry);
        } else {
            this.add(this.roomToEntryMap.get(room));
        }
    }

    public void removeRoom(Room room) {
        this.roomToEntryMap.remove(room);
    }

    public void updateRoom(Room room) {
        if(this.roomToEntryMap.containsKey(room)) {
            this.roomToEntryMap.get(room).updateOnMap();
        }
    }

    public void add(CollisionMapEntry room) {
        if(!room.existsInMap()) {
            BoundingBox box = room.getBoundingBox();
            int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
            int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
            int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
            int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
            int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
            int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        List<CollisionMapEntry> roomList = this.map[x][y][z];
                        roomList.add(room);
                    }
                }
            }

            room.setExistsInMap(true);
        }
    }

    public void remove(CollisionMapEntry room) {
        if(room.existsInMap()) {
            BoundingBox box = room.getBoundingBox();
            int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
            int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
            int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
            int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
            int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
            int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    for (int z = minZ; z <= maxZ; z++) {
                        List<CollisionMapEntry> roomList = this.map[x][y][z];
                        roomList.remove(room);
                    }
                }
            }

            room.setExistsInMap(false);
        }
    }

    public boolean doesRoomIntersectOtherRooms(Room room) {
        return this.doesBoxIntersectRooms(room.getBoundingBox(), room);
    }

    public boolean doesBoxIntersectRooms(BoundingBox box, @Nullable Room exclude) {
        Optional<CollisionMapEntry> intersectOptional = this.getFirstIntersection(box, exclude);
        return intersectOptional.isPresent();
    }

    public Optional<CollisionMapEntry> getFirstIntersection(BoundingBox box, @Nullable Room exclude) {
        if(this.roomToEntryMap.containsKey(exclude)) {
            return this.getFirstIntersection(box, this.roomToEntryMap.get(exclude));
        } else {
            return this.getFirstIntersection(box, (CollisionMapEntry)null);
        }
    }

    public Optional<CollisionMapEntry> getFirstIntersection(BoundingBox box, @Nullable CollisionMapEntry exclude) {
        int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
        int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    for (CollisionMapEntry room : this.map[x][y][z]) {
                        if(room == exclude) continue;

                        if(room.getBoundingBox().intersects(box)) {
                            return Optional.of(room);
                        }
                    }
                }
            }
        }
        return Optional.empty();
    }

    public List<CollisionMapEntry> getIntersections(BoundingBox box, @Nullable Room exclude) {
        if(this.roomToEntryMap.containsKey(exclude)) {
            return this.getIntersections(box, this.roomToEntryMap.get(exclude));
        } else {
            return this.getIntersections(box, (CollisionMapEntry)null);
        }
    }

    public List<CollisionMapEntry> getIntersections(BoundingBox box, @Nullable CollisionMapEntry exclude) {
        int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
        int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

        ObjectArrayList<CollisionMapEntry> collisionRooms = new ObjectArrayList<>();
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    for (CollisionMapEntry room : this.map[x][y][z]) {
                        if(room == exclude) continue;

                        if(room.getBoundingBox().intersects(box)) {
                            collisionRooms.add(room);
                        }
                    }
                }
            }
        }
        return collisionRooms;
    }

    public Optional<List<CollisionMapEntry>> getListForChunk(SectionPos chunkSectionPos) {
        int x = chunkSectionPos.getX() - this.basePos.getX();
        int y = chunkSectionPos.getY() - this.basePos.getY();
        int z = chunkSectionPos.getZ() - this.basePos.getZ();
        if(x < 0
                || y < 0
                || z < 0
                || x >= this.sizeX
                || y >= this.sizeY
                || z >= this.sizeZ
        ) {
            return Optional.empty();
        } else {
            return Optional.of(this.map[x][y][z]);
        }
    }
}
