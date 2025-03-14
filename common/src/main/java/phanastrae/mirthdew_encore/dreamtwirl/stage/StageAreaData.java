package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.util.RegionPos;

public class StageAreaData {

    private final RegionPos regionPos;
    private final int minBuildHeight;
    private final int height;
    private final int maxBuildHeight;
    private final BoundingBox inBoundsBoundingBox;
    private final BoundingBox entireStageBoundingBox;
    private final ChunkPos stageChunkCenter;

    public StageAreaData(RegionPos regionPos, int minBuildHeight, int height, int maxBuildHeight) {
        this.regionPos = regionPos;

        this.minBuildHeight = minBuildHeight;
        this.height = height;
        this.maxBuildHeight = maxBuildHeight;

        DreamtwirlBorder border = new DreamtwirlBorder(this.regionPos);
        this.inBoundsBoundingBox = new BoundingBox(
                border.minX,
                minBuildHeight,
                border.minZ,
                border.maxX - 1,
                maxBuildHeight - 1,
                border.maxZ - 1
        );
        this.entireStageBoundingBox = new BoundingBox(
                regionPos.worldX,
                minBuildHeight,
                regionPos.worldZ,
                regionPos.worldX + DreamtwirlBorder.REGION_SIZE - 1,
                maxBuildHeight - 1,
                regionPos.worldZ + DreamtwirlBorder.REGION_SIZE - 1
        );

        this.stageChunkCenter = this.regionPos.getCenterChunkPos();
    }

    public boolean isBoundingBoxInBounds(BoundingBox box) {
        BoundingBox inBoundsBox = this.getInBoundsBoundingBox();
        return box.minX() >= inBoundsBox.minX()
                && box.minY() >= inBoundsBox.minY()
                && box.minZ() >= inBoundsBox.minZ()
                && box.maxX() <= inBoundsBox.maxX()
                && box.maxY() <= inBoundsBox.maxY()
                && box.maxZ() <= inBoundsBox.maxZ();
    }

    public BlockPos offsetMinBlockPos(int dx, int dy, int dz) {
        BlockPos regionMin = new BlockPos(this.regionPos.worldX, this.minBuildHeight, this.regionPos.worldZ);
        return regionMin.offset(dx, dy, dz);
    }

    public BlockPos offsetCenterBlockPos(int dx, int dy, int dz) {
        return offsetMinBlockPos(dx + 256, dy + this.height / 2, dz + 256);
    }

    public RegionPos getRegionPos() {
        return regionPos;
    }

    public int getMinBuildHeight() {
        return minBuildHeight;
    }

    public int getHeight() {
        return height;
    }

    public int getMaxBuildHeight() {
        return maxBuildHeight;
    }

    public BoundingBox getEntireStageBoundingBox() {
        return entireStageBoundingBox;
    }

    public BoundingBox getInBoundsBoundingBox() {
        return inBoundsBoundingBox;
    }

    public ChunkPos getStageChunkCenter() {
        return stageChunkCenter;
    }
}
