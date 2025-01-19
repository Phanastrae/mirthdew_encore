package phanastrae.mirthdew_encore.dreamtwirl.stage.play;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import phanastrae.mirthdew_encore.util.RegionPos;

public class DreamtwirlBorder {
    public static final int REGION_SIZE = 512;
    public static final int PADDING_SIZE = 16;

    public final RegionPos regionPos;
    public final int minX;
    public final int minZ;
    public final int maxX;
    public final int maxZ;
    public final VoxelShape voxelShape;

    public DreamtwirlBorder(RegionPos regionPos) {
        this.regionPos = regionPos;

        this.minX = regionPos.worldX + PADDING_SIZE;
        this.minZ = regionPos.worldZ + PADDING_SIZE;
        this.maxX = regionPos.worldX + REGION_SIZE - PADDING_SIZE;
        this.maxZ = regionPos.worldZ + REGION_SIZE - PADDING_SIZE;

        VoxelShape inside = Shapes.box(
                minX,
                Double.NEGATIVE_INFINITY,
                minZ,
                maxX,
                Double.POSITIVE_INFINITY,
                maxZ
        );
        VoxelShape outside = Shapes.box(
                regionPos.worldX - PADDING_SIZE,
                Double.NEGATIVE_INFINITY,
                regionPos.worldZ - PADDING_SIZE,
                regionPos.worldX + REGION_SIZE + PADDING_SIZE,
                Double.POSITIVE_INFINITY,
                regionPos.worldZ + REGION_SIZE + PADDING_SIZE
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
