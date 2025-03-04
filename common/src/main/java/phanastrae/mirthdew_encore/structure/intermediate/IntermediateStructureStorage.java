package phanastrae.mirthdew_encore.structure.intermediate;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class IntermediateStructureStorage {
    public static final String KEY_X = "x";
    public static final String KEY_Y = "y";
    public static final String KEY_Z = "z";
    public static final String KEY_BLOCK_CONTAINERS = "containers";
    public static final String KEY_FRAGILE_CONTAINERS = "fragile_containers";
    public static final String KEY_BLOCK_ENTITIES = "block_entities";
    public static final String KEY_ENTITIES = "entities";
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

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries) {
        ListTag blockContainers = new ListTag();
        this.blockContainers.forEach((sectionPos, boxedContainer) -> {
            CompoundTag tag = new CompoundTag();
            boxedContainer.writeNbt(tag, registries);
            tag.putInt(KEY_X, sectionPos.getX());
            tag.putInt(KEY_Y, sectionPos.getY());
            tag.putInt(KEY_Z, sectionPos.getZ());
            blockContainers.add(tag);
        });
        nbt.put(KEY_BLOCK_CONTAINERS, blockContainers);

        ListTag fragileContainers = new ListTag();
        this.fragileBlockContainers.forEach((sectionPos, boxedContainer) -> {
            CompoundTag tag = new CompoundTag();
            boxedContainer.writeNbt(tag, registries);
            tag.putInt(KEY_X, sectionPos.getX());
            tag.putInt(KEY_Y, sectionPos.getY());
            tag.putInt(KEY_Z, sectionPos.getZ());
            fragileContainers.add(tag);
        });
        nbt.put(KEY_FRAGILE_CONTAINERS, fragileContainers);

        ListTag blockEntityList = new ListTag();
        this.blockEntities.forEach((pos, blockEntity) -> {
            CompoundTag tag = blockEntity.saveWithId(registries);
            tag.putInt(KEY_X, pos.getX());
            tag.putInt(KEY_Y, pos.getY());
            tag.putInt(KEY_Z, pos.getZ());
            blockEntityList.add(tag);
        });
        nbt.put(KEY_BLOCK_ENTITIES, blockEntityList);

        ListTag entityList = new ListTag();
        for(Entity entity : this.entities) {
            if(entity.shouldBeSaved()) {
                CompoundTag tag = new CompoundTag();
                entity.save(tag);
                entityList.add(tag);
            }
        }
        nbt.put(KEY_ENTITIES, entityList);

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries, Level level) {
        this.clearCache();

        if(nbt.contains(KEY_BLOCK_CONTAINERS, Tag.TAG_LIST)) {
            this.blockContainers.clear();
            ListTag list = nbt.getList(KEY_BLOCK_CONTAINERS, Tag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                if(!tag.contains(KEY_X, Tag.TAG_INT) || !tag.contains(KEY_Y, Tag.TAG_INT) || !tag.contains(KEY_Z, Tag.TAG_INT)) continue;
                SectionPos pos = SectionPos.of(tag.getInt(KEY_X), tag.getInt(KEY_Y), tag.getInt(KEY_Z));

                BoxedContainer container = BoxedContainer.fromNbt(tag, registries);

                this.blockContainers.put(pos, container);
            }
        }

        if(nbt.contains(KEY_FRAGILE_CONTAINERS, Tag.TAG_LIST)) {
            this.fragileBlockContainers.clear();
            ListTag list = nbt.getList(KEY_FRAGILE_CONTAINERS, Tag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                if(!tag.contains(KEY_X, Tag.TAG_INT) || !tag.contains(KEY_Y, Tag.TAG_INT) || !tag.contains(KEY_Z, Tag.TAG_INT)) continue;
                SectionPos pos = SectionPos.of(tag.getInt(KEY_X), tag.getInt(KEY_Y), tag.getInt(KEY_Z));

                BoxedContainer container = BoxedContainer.fromNbt(tag, registries);

                this.fragileBlockContainers.put(pos, container);
            }
        }

        if(nbt.contains(KEY_BLOCK_ENTITIES, Tag.TAG_LIST)) {
            this.blockEntities.clear();
            ListTag list = nbt.getList(KEY_BLOCK_ENTITIES, Tag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                if(!tag.contains(KEY_X, Tag.TAG_INT) || !tag.contains(KEY_Y, Tag.TAG_INT) || !tag.contains(KEY_Z, Tag.TAG_INT)) continue;
                BlockPos pos = new BlockPos(tag.getInt(KEY_X), tag.getInt(KEY_Y), tag.getInt(KEY_Z));

                BlockState fragileState = this.getFragileBlockState(pos);
                BlockState blockState = this.getBlockState(pos);

                BlockState state = fragileState.is(Blocks.STRUCTURE_VOID) ? blockState : fragileState;
                BlockEntity blockEntity = createBlockEntity(pos, state, tag, registries);

                if(blockEntity != null) {
                    this.blockEntities.put(pos, blockEntity);
                }
            }
        }

        if(nbt.contains(KEY_ENTITIES, Tag.TAG_LIST)) {
            this.entities.clear();
            ListTag list = nbt.getList(KEY_ENTITIES, Tag.TAG_COMPOUND);
            for(int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);

                Optional<Entity> entityOptional = EntityType.create(tag, level);
                if(entityOptional.isPresent()) {
                    Entity entity = entityOptional.get();
                    this.entities.add(entity);
                }
            }
        }

        return nbt;
    }

    @Nullable
    public static BlockEntity createBlockEntity(BlockPos pos, BlockState state, CompoundTag tag, HolderLookup.Provider registries) {
        // trimmed, safer version of BlockEntity.loadStatic()

        String s = tag.getString("id");
        ResourceLocation resourcelocation = ResourceLocation.tryParse(s);
        if (resourcelocation == null) {
            return null;
        } else {
            return BuiltInRegistries.BLOCK_ENTITY_TYPE.getOptional(resourcelocation).map(blockEntityType -> {
                if(blockEntityType.isValid(state)) {
                    return blockEntityType.create(pos, state);
                } else {
                    return null;
                }
            }).map(blockEntity -> {
                try {
                    blockEntity.loadWithComponents(tag, registries);
                    return (BlockEntity)blockEntity;
                } catch (Throwable throwable) {
                    MirthdewEncore.LOGGER.error("Failed to load data for block entity {}", s, throwable);
                    return null;
                }
            }).orElse(null);
        }
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
                this.blockEntities.put(pos.immutable(), blockEntity);
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

    public @Nullable BoundingBox calculateBoundingBox() {
        if(this.blockContainers.isEmpty()) {
            return null;
        }

        AtomicInteger aMinX = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger aMinY = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger aMinZ = new AtomicInteger(Integer.MAX_VALUE);

        AtomicInteger aMaxX = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger aMaxY = new AtomicInteger(Integer.MIN_VALUE);
        AtomicInteger aMaxZ = new AtomicInteger(Integer.MIN_VALUE);

        this.blockContainers.forEach(((sectionPos, boxedContainer) -> {
            BoundingBox box = boxedContainer.getBox();
            if(box == null) return;
            int minX = sectionPos.minBlockX() + box.minX();
            int minY = sectionPos.minBlockY() + box.minY();
            int minZ = sectionPos.minBlockZ() + box.minZ();

            int maxX = sectionPos.minBlockX() + box.maxX();
            int maxY = sectionPos.minBlockY() + box.maxY();
            int maxZ = sectionPos.minBlockZ() + box.maxZ();

            aMinX.set(Math.min(aMinX.get(), minX));
            aMinY.set(Math.min(aMinY.get(), minY));
            aMinZ.set(Math.min(aMinZ.get(), minZ));

            aMaxX.set(Math.max(aMaxX.get(), maxX));
            aMaxY.set(Math.max(aMaxY.get(), maxY));
            aMaxZ.set(Math.max(aMaxZ.get(), maxZ));
        }));

        if(aMinX.get() == Integer.MAX_VALUE) {
            return null;
        } else {
            return new BoundingBox(
                    aMinX.get(), aMinY.get(), aMinZ.get(),
                    aMaxX.get(), aMaxY.get(), aMaxZ.get()
            );
        }
    }
}
