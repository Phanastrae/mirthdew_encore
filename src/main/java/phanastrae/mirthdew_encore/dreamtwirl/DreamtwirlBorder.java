package phanastrae.mirthdew_encore.dreamtwirl;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.mirthdew_encore.util.RegionPos;

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

        VoxelShape inside = Shapes.box(
                minX,
                Double.NEGATIVE_INFINITY,
                minZ,
                maxX,
                Double.POSITIVE_INFINITY,
                maxZ
        );
        VoxelShape outside = Shapes.box(
                regionPos.worldX - BORDER_SIZE,
                Double.NEGATIVE_INFINITY,
                regionPos.worldZ - BORDER_SIZE,
                regionPos.worldX + 512 + BORDER_SIZE,
                Double.POSITIVE_INFINITY,
                regionPos.worldZ + 512 + BORDER_SIZE
        );
        this.voxelShape = Shapes.join(
                outside,
                inside,
                BooleanOp.ONLY_FIRST
        );
    }

    public boolean entityOutsideBorder(Entity entity) {
        return Shapes.joinIsNotEmpty(this.voxelShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
    }

    public boolean entityTouchingBorder(Entity entity) {
        return Shapes.joinIsNotEmpty(this.voxelShape, Shapes.create(entity.getBoundingBox().inflate(1E-6)), BooleanOp.AND);
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
