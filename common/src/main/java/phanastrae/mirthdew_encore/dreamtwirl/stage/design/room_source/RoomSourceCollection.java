package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RoomSourceCollection {
    public static final String KEY_ROOM_SOURCES = "room_sources";

    public final List<Entry> roomSources;

    public RoomSourceCollection(List<Entry> roomSources) {
        this.roomSources = roomSources;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        ListTag sourceList = new ListTag();
        for(Entry source : this.roomSources) {
            sourceList.add(source.writeNbt(new CompoundTag(), registries, spsContext));
        }
        nbt.put(KEY_ROOM_SOURCES, sourceList);

        return nbt;
    }

    public CompoundTag readNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        this.roomSources.clear();
        if(nbt.contains(KEY_ROOM_SOURCES, Tag.TAG_LIST)) {
            ListTag sourceList = nbt.getList(KEY_ROOM_SOURCES, Tag.TAG_COMPOUND);

            for(int i = 0; i < sourceList.size(); i++) {
                CompoundTag tag = sourceList.getCompound(i);

                Entry source = Entry.fromNbt(tag, registries, spsContext);
                if(source != null) {
                    this.roomSources.add(source);
                }
            }
        }

        return nbt;
    }

    public Optional<RoomSource> getEntrance(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isEntrance());
    }

    public Optional<RoomSource> getRandomMatching(RandomSource random, Predicate<RoomSource> predicate) {
        // TODO optimise
        List<Entry> entries = this.roomSources.stream().filter(roomSource -> predicate.test(roomSource.roomSource)).toList();
        if(entries.isEmpty()) {
            return Optional.empty();
        }
        int totalWeight = 0;
        for(Entry entry : entries) {
            totalWeight += entry.weight;
        }

        if(totalWeight <= 0) {
            return Optional.empty();
        }
        int target = random.nextInt(totalWeight);

        int weightSoFar = 0;
        for(Entry entry : entries) {
            if(weightSoFar <= target && target < weightSoFar + entry.weight) {
                return Optional.of(entry.roomSource);
            }
            weightSoFar += entry.weight;
        }

        return Optional.empty();
    }

    public static RoomSourceCollection create(VistaType vistaSettings) {
        List<Entry> roomSources = new ArrayList<>();
        vistaSettings.roomTypeEntries().forEach(roomType -> roomSources.add(new Entry(new RoomSource(roomType.roomType().value()), roomType.weight())));
        return new RoomSourceCollection(roomSources);
    }

    public record Entry(RoomSource roomSource, int weight) {
        public static final String KEY_WEIGHT = "weight";

        public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
            this.roomSource.writeNbt(nbt, registries, spsContext);
            nbt.putInt(KEY_WEIGHT, this.weight);
            return nbt;
        }

        public static @Nullable Entry fromNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
            RoomSource roomSource = RoomSource.fromNbt(nbt, registries, spsContext);
            if(!nbt.contains(KEY_WEIGHT, Tag.TAG_INT)) {
                return null;
            }

            int weight = nbt.getInt(KEY_WEIGHT);

            return new Entry(roomSource, weight);
        }
    }
}
