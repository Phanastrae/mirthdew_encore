package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.Structure;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class RoomSourceCollection {

    public final List<RoomSource> roomTypes;

    public RoomSourceCollection(List<RoomSource> roomTypes) {
        this.roomTypes = roomTypes;
    }

    public Optional<RoomSource> getRandomPrefabSet(RandomSource random) {
        if(random.nextInt(4) == 0) {
            return getRoom(random);
        } else {
            return getPath(random);
        }
    }

    public Optional<RoomSource> getPath(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isPath());
    }

    public Optional<RoomSource> getRoom(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isRoom());
    }

    public Optional<RoomSource> getEntrance(RandomSource random) {
        return getRandomMatching(random, prefabSet -> prefabSet.getRoomType().isEntrance());
    }

    public Optional<RoomSource> getRandomMatching(RandomSource random, Predicate<RoomSource> predicate) {
        List<RoomSource> prefabSets = this.roomTypes.stream().filter(predicate).toList();

        if(prefabSets.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(prefabSets.get(random.nextInt(prefabSets.size())));
    }

    public static RoomSourceCollection create(ServerLevel serverLevel, VistaType vistaSettings) {
        RegistryAccess registryAccess = serverLevel.registryAccess();

        List<RoomSource> roomTypes = new ArrayList<>();
        vistaSettings.roomTypes.forEach(setting -> getRoomType(setting, registryAccess).ifPresent(roomTypes::add));

        return new RoomSourceCollection(roomTypes);
    }

    public static Optional<RoomSource> getRoomType(RoomType settings, RegistryAccess registryAccess) {
        Optional<Structure> structureOptional = getStructure(settings.getResourceLocation(), registryAccess);
        return structureOptional.map(structure -> new RoomSource(settings, structure));
    }

    public static Optional<Structure> getStructure(ResourceLocation identifier, RegistryAccess registryAccess) {
        Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> registryKey = ResourceKey.create(Registries.STRUCTURE, identifier);
        return structureRegistry.getOptional(registryKey);
    }
}
