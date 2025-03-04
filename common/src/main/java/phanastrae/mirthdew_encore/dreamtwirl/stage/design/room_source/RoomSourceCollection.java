package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RoomSourceCollection {
    public static final String KEY_ROOM_SOURCES = "room_sources";

    public final List<RoomSource> roomSources;

    public RoomSourceCollection(List<RoomSource> roomSources) {
        this.roomSources = roomSources;
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        ListTag sourceList = new ListTag();
        for(RoomSource source : this.roomSources) {
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

                RoomSource source = RoomSource.fromNbt(tag, registries, spsContext);
                if(source != null) {
                    this.roomSources.add(source);
                }
            }
        }

        return nbt;
    }

    public Optional<RoomSource> getRandomRoomSource(RandomSource random) {
        int i = random.nextInt(3);
        if(i == 0) {
            return getRoom(random);
        } else if(i == 1) {
            return getPath(random);
        } else {
            return getGate(random);
        }
    }

    public Optional<RoomSource> getPath(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isPath());
    }

    public Optional<RoomSource> getRoom(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isRoom());
    }

    public Optional<RoomSource> getGate(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isGate());
    }

    public Optional<RoomSource> getEntrance(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isEntrance());
    }

    public Optional<RoomSource> getRandomMatching(RandomSource random, Predicate<RoomSource> predicate) {
        List<RoomSource> prefabSets = this.roomSources.stream().filter(predicate).toList();

        if(prefabSets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(prefabSets.get(random.nextInt(prefabSets.size())));
    }

    public static RoomSourceCollection create(ServerLevel serverLevel, VistaType vistaSettings) {
        RegistryAccess registryAccess = serverLevel.registryAccess();

        List<RoomSource> roomSources = new ArrayList<>();
        vistaSettings.roomTypes.forEach(setting -> getRoomType(setting, registryAccess).ifPresent(roomSources::add));

        return new RoomSourceCollection(roomSources);
    }

    public static Optional<RoomSource> getRoomType(RoomType settings, RegistryAccess registryAccess) {
        Optional<Structure> structureOptional = getStructure(settings.resourceLocation(), registryAccess);
        return structureOptional.map(structure -> new RoomSource(structure, settings));
    }

    public static Optional<Structure> getStructure(ResourceLocation identifier, RegistryAccess registryAccess) {
        Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> registryKey = ResourceKey.create(Registries.STRUCTURE, identifier);
        return structureRegistry.getOptional(registryKey);
    }
}
