package phanastrae.mirthdew_encore.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class RegionPos {
    public static final int REGION_SIZE_BITS = 9;

    public final long id;
    public final int regionX;
    public final int regionZ;
    public final int worldX;
    public final int worldZ;

    public RegionPos(int regionX, int regionZ) {
        this.regionX = regionX;
        this.regionZ = regionZ;
        this.id = (long)regionX & 4294967295L | ((long)regionZ & 4294967295L) << 32;
        this.worldX = regionX << REGION_SIZE_BITS;
        this.worldZ = regionZ << REGION_SIZE_BITS;
    }

    public RegionPos (long id) {
        this.id = id;
        this.regionX = (int)(id & 4294967295L);
        this.regionZ = (int)((id >>> 32) & 4294967295L);
        this.worldX = this.regionX << REGION_SIZE_BITS;
        this.worldZ = this.regionZ << REGION_SIZE_BITS;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof RegionPos otherPos) {
            return this.id == otherPos.id;
        } else {
            return false;
        }
    }

    public int getCenterX() {
        return this.worldX + 256;
    }

    public int getCenterZ() {
        return this.worldZ + 256;
    }

    public ChunkPos getMinChunkPos() {
        return getChunkPos(0, 0);
    }

    public ChunkPos getMaxChunkPos() {
        return getChunkPos(31, 31);
    }

    public ChunkPos getChunkPos(int x, int z) {
        return new ChunkPos(x + this.regionX << 5, z + this.regionZ << 5);
    }

    public static RegionPos fromWorldCoords(int x, int z) {
        return new RegionPos(x >> REGION_SIZE_BITS, z >> REGION_SIZE_BITS);
    }

    public static RegionPos fromWorldCoordsDoubles(double x, double z) {
        return fromWorldCoords((int)MathHelper.floor(x), (int)MathHelper.floor(z));
    }

    public static RegionPos fromBlockPos(BlockPos blockPos) {
        return RegionPos.fromWorldCoords(blockPos.getX(), blockPos.getZ());
    }

    public static RegionPos fromVec3d(Vec3d vec3d) {
        return RegionPos.fromWorldCoordsDoubles(vec3d.getX(), vec3d.getZ());
    }

    public static RegionPos fromEntity(Entity entity) {
        return RegionPos.fromVec3d(entity.getPos());
    }

    public static RegionPos fromChunkPos(ChunkPos chunkPos) {
        return new RegionPos(chunkPos.getRegionX(), chunkPos.getRegionZ());
    }
}
