package phanastrae.mirthdew_encore.structure.intermediate;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;

public class BoxedContainer {

    private final PalettedContainer<BlockState> container;

    private boolean hasBox = false;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public BoxedContainer() {
        this.container = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.STRUCTURE_VOID.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
    }

    public void set(int x, int y, int z, BlockState state) {
        this.container.set(x, y, z, state);
        this.expandBoxToFit(x, y, z);
    }

    public BlockState get(int x, int y, int z) {
        return this.container.get(x, y, z);
    }

    public void expandBoxToFit(int x, int y, int z) {
        if(!this.hasBox) {
            this.minX = x;
            this.minY = y;
            this.minZ = z;
            this.maxX = x;
            this.maxY = y;
            this.maxZ = z;
            this.hasBox = true;
        } else {
            if(x < this.minX) this.minX = x;
            if(y < this.minY) this.minY = y;
            if(z < this.minZ) this.minZ = z;
            if(x > this.maxX) this.maxX = x;
            if(y > this.maxY) this.maxY = y;
            if(z > this.maxZ) this.maxZ = z;
        }
    }

    @Nullable
    public BoundingBox getBox() {
        return this.hasBox ? new BoundingBox(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ) : null;
    }
}
