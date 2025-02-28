package phanastrae.mirthdew_encore.structure.intermediate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IntermediateStructureStorage {
    private static final int CACHE_SIZE = 4; // TODO is this size ideal?

    private final HashMap<SectionPos, BoxedContainer> blockContainers;
    private final HashMap<SectionPos, BoxedContainer> fragileBlockContainers;
    private final HashMap<BlockPos, BlockEntity> blockEntities;
    private final List<Entity> entities;

    private final SectionPos[] lastSectionPos = new SectionPos[CACHE_SIZE];
    private final BoxedContainer[] lastContainer = new BoxedContainer[CACHE_SIZE];

    public IntermediateStructureStorage() {
        this.blockContainers = new HashMap<>();
        this.fragileBlockContainers = new HashMap<>();
        this.blockEntities = new HashMap<>();
        this.entities = new ObjectArrayList<>();
    }

    public void forEachContainer(BiConsumer<? super SectionPos, ? super BoxedContainer> biConsumer) {
        this.blockContainers.forEach(biConsumer);
    }

    public void forEachFragileContainer(BiConsumer<? super SectionPos, ? super BoxedContainer> biConsumer) {
        this.fragileBlockContainers.forEach(biConsumer);
    }

    public void forEachBlockEntity(BiConsumer<? super BlockPos, ? super BlockEntity> biConsumer) {
        this.blockEntities.forEach(biConsumer);
    }

    public void forEachEntity(Consumer<? super Entity> consumer) {
        this.entities.forEach(consumer);
    }

    public void addFragileContainer(SectionPos sectionPos, BoxedContainer fragileContainer) {
        this.fragileBlockContainers.put(sectionPos, fragileContainer);
    }

    public BoxedContainer getContainer(SectionPos sectionPos) {
        for (int j = 0; j < CACHE_SIZE; j++) {
            if (sectionPos.equals(this.lastSectionPos[j])) {
                BoxedContainer container = this.lastContainer[j];
                if (container != null) {
                    return container;
                }
            }
        }

        BoxedContainer container;
        if(this.blockContainers.containsKey(sectionPos)) {
            container = this.blockContainers.get(sectionPos);
        } else {
            container = new BoxedContainer();
            this.blockContainers.put(sectionPos, container);
        }

        this.storeInCache(sectionPos, container);
        return container;
    }

    @Nullable
    public BoxedContainer getFragileContainer(SectionPos sectionPos) {
        if(this.fragileBlockContainers.containsKey(sectionPos)) {
            return this.fragileBlockContainers.get(sectionPos);
        } else {
            return null;
        }
    }

    private void storeInCache(SectionPos sectionPos, BoxedContainer palettedContainer) {
        for (int i = CACHE_SIZE - 1; i > 0; i--) {
            this.lastSectionPos[i] = this.lastSectionPos[i - 1];
            this.lastContainer[i] = this.lastContainer[i - 1];
        }

        this.lastSectionPos[0] = sectionPos;
        this.lastContainer[0] = palettedContainer;
    }

    private void clearCache() {
        Arrays.fill(this.lastSectionPos, null);
        Arrays.fill(this.lastContainer, null);
    }

    public BoxedContainer getContainer(BlockPos pos) {
        SectionPos sectionPos = SectionPos.of(pos);
        return getContainer(sectionPos);
    }

    @Nullable
    public BoxedContainer getFragileContainer(BlockPos pos) {
        SectionPos sectionPos = SectionPos.of(pos);
        return getFragileContainer(sectionPos);
    }

    public boolean setBlockState(BlockPos pos, BlockState state) {
        getContainer(pos).set(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF, state);
        if(state.hasBlockEntity() && state.getBlock() instanceof EntityBlock entityBlock) {
            this.blockEntities.remove(pos);
            BlockEntity blockEntity = entityBlock.newBlockEntity(pos, state);
            if(blockEntity != null) {
                this.blockEntities.put(pos, blockEntity);
            }
        }
        return true;
    }

    public BlockState getBlockState(BlockPos pos) {
        return getContainer(pos).get(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
    }

    public BlockState getFragileBlockState(BlockPos pos) {
        BoxedContainer fragileContainer = this.getFragileContainer(pos);
        if(fragileContainer == null) {
            return Blocks.STRUCTURE_VOID.defaultBlockState();
        } else {
            return fragileContainer.get(pos.getX() & 0xF, pos.getY() & 0xF, pos.getZ() & 0xF);
        }
    }

    @Nullable
    public BlockEntity getBlockEntity(BlockPos pos) {
        if(this.blockEntities.containsKey(pos)) {
            BlockEntity entity = this.blockEntities.get(pos);
            return entity;
        } else {
            return null;
        }
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }
}
