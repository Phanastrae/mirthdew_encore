package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class ChunkMap {

    public final Vec3i basePos;
    public final int sizeX;
    public final int sizeY;
    public final int sizeZ;

    public List<DreamtwirlRoom>[][][] map;

    public ChunkMap(Vec3i basePos, int sizeX, int sizeY, int sizeZ) {
        this.basePos = basePos;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;

        // TODO: consider not making 14400 lists and instead doing something more intelligent here ie subdivide into smaller chunkmaps for an octree sort of thing
        this.map = new List[sizeX][sizeY][sizeZ];
        for(int x = 0; x < sizeX; x++) {
            List<DreamtwirlRoom>[][] xarr = new List[sizeY][sizeZ];
            this.map[x] = xarr;
            for(int y = 0; y < sizeY; y++) {
                List<DreamtwirlRoom>[] yarr = new List[sizeZ];
                xarr[y] = yarr;
                for(int z = 0; z < sizeZ; z++) {
                    List<DreamtwirlRoom> zList = new ArrayList<>();
                    yarr[z] = zList;
                }
            }
        }
    }

    public void add(DreamtwirlRoom room) {
        BoundingBox box = room.getBoundingBox();
        int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
        int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    List<DreamtwirlRoom> roomList = this.map[x][y][z];
                    roomList.add(room);
                }
            }
        }
    }

    public void remove(DreamtwirlRoom room) {
        BoundingBox box = room.getBoundingBox();
        int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
        int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    List<DreamtwirlRoom> roomList = this.map[x][y][z];
                    roomList.remove(room);
                }
            }
        }
    }

    public List<DreamtwirlRoom> getIntersections(BoundingBox box, @Nullable DreamtwirlRoom exclude) {
        int minX = Mth.clamp((box.minX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int minY = Mth.clamp((box.minY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int minZ = Mth.clamp((box.minZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);
        int maxX = Mth.clamp((box.maxX() >> 4) - this.basePos.getX(), 0, this.sizeX - 1);
        int maxY = Mth.clamp((box.maxY() >> 4) - this.basePos.getY(), 0, this.sizeY - 1);
        int maxZ = Mth.clamp((box.maxZ() >> 4) - this.basePos.getZ(), 0, this.sizeZ - 1);

        ObjectArrayList<DreamtwirlRoom> collisionRooms = new ObjectArrayList<>();
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    List<DreamtwirlRoom> roomList = this.map[x][y][z];
                    for (DreamtwirlRoom room : roomList) {
                        if (room != exclude) {
                            collisionRooms.add(room);
                        }
                    }
                }
            }
        }
        return collisionRooms;
    }

    public Optional<List<DreamtwirlRoom>> getListForChunk(SectionPos chunkSectionPos) {
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
