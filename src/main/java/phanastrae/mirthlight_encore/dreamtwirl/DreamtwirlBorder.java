package phanastrae.mirthlight_encore.dreamtwirl;

import net.minecraft.entity.Entity;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import phanastrae.mirthlight_encore.util.RegionPos;

public class DreamtwirlBorder {
    public static final int BORDER_SIZE = 16;

    public final RegionPos regionPos;
    public final int minX;
    public final int minZ;
    public final int maxX;
    public final int maxZ;
    public final VoxelShape voxelShape;

    public DreamtwirlBorder(RegionPos regionPos) {
        this.regionPos = regionPos;

        this.minX = regionPos.worldX + BORDER_SIZE;
        this.minZ = regionPos.worldZ + BORDER_SIZE;
        this.maxX = regionPos.worldX + 512 - BORDER_SIZE;
        this.maxZ = regionPos.worldZ + 512 - BORDER_SIZE;

        this.voxelShape = VoxelShapes.combineAndSimplify(
                VoxelShapes.UNBOUNDED,
                VoxelShapes.cuboid(
                        minX,
                        Double.NEGATIVE_INFINITY,
                        minZ,
                        maxX,
                        Double.POSITIVE_INFINITY,
                        maxZ
                ),
                BooleanBiFunction.ONLY_FIRST
        );
    }

    public boolean entityOutsideBorder(Entity entity) {
        return VoxelShapes.matchesAnywhere(this.voxelShape, VoxelShapes.cuboid(entity.getBoundingBox()), BooleanBiFunction.AND);
    }

    public boolean entityTouchingBorder(Entity entity) {
        return VoxelShapes.matchesAnywhere(this.voxelShape, VoxelShapes.cuboid(entity.getBoundingBox().expand(1E-6)), BooleanBiFunction.AND);
    }

    public boolean contains(double x, double z, double margin) {
        if(x < this.minX + margin) {
            return false;
        } else if(x > this.maxX - margin) {
            return false;
        } else if(z < this.minZ + margin) {
            return false;
        } else if(z > this.maxZ - margin) {
            return false;
        } else {
            return true;
        }
    }
}
