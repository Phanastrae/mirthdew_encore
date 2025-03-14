package phanastrae.mirthdew_encore.structure.intermediate;

import com.mojang.serialization.Codec;
import io.netty.buffer.Unpooled;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.Optional;

public class BoxedContainer {
    private static final Codec<PalettedContainer<BlockState>> BLOCK_STATE_CODEC = PalettedContainer.codecRW(
            Block.BLOCK_STATE_REGISTRY, BlockState.CODEC, PalettedContainer.Strategy.SECTION_STATES, Blocks.AIR.defaultBlockState()
    );

    public static final String KEY_BLOCK_DATA = "block_data";
    public static final String KEY_BOX = "box";

    private final PalettedContainer<BlockState> container;

    private boolean hasBox = false;
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public BoxedContainer() {
        this(new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.STRUCTURE_VOID.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES));
    }

    public BoxedContainer(PalettedContainer<BlockState> container) {
        this.container = container;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        BLOCK_STATE_CODEC
                .encodeStart(registryops, this.container)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode blockstate data for Boxed Container: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_BLOCK_DATA, bpdTag));

        if(this.hasBox) {
            nbt.putIntArray(KEY_BOX, new int[]{minX, minY, minZ, maxX, maxY, maxZ});
        }
        return nbt;
    }

    public static BoxedContainer fromNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        Optional<PalettedContainer<BlockState>> containerOptional = BLOCK_STATE_CODEC
                .parse(registryops, nbt.get(KEY_BLOCK_DATA))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse blockstate data for Boxed Container: '{}'", st));

        BoxedContainer bc;
        bc = containerOptional.map(BoxedContainer::new).orElseGet(BoxedContainer::new);

        if(nbt.contains(KEY_BLOCK_DATA, Tag.TAG_BYTE_ARRAY)) {
            FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
            buf.writeByteArray(nbt.getByteArray(KEY_BLOCK_DATA));
            bc.container.read(buf);
        }

        if(nbt.contains(KEY_BOX, Tag.TAG_INT_ARRAY)) {
            int[] data = nbt.getIntArray(KEY_BOX);
            if(data.length == 6) {
                bc.hasBox = true;
                bc.minX = data[0];
                bc.minY = data[1];
                bc.minZ = data[2];
                bc.maxX = data[3];
                bc.maxY = data[4];
                bc.maxZ = data[5];
            } else {
                bc.hasBox = false;
            }
        } else {
            bc.hasBox = false;
        }
        return bc;
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
