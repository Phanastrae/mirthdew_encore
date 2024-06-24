package phanastrae.mirthdew_encore.dreamtwirl;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class DreamtwirlStageManager extends PersistentState {

    private final Map<Long, DreamtwirlStage> dreamtwirls = Maps.newHashMap();
    private final ServerWorld world;
    private final Random random = Random.create();

    public static Type<DreamtwirlStageManager> getPersistentStateType(ServerWorld world) {
        return new Type<>(
                () -> new DreamtwirlStageManager(world),
                (nbt, registryLookup) -> fromNbt(world, nbt),
                null
        );
    }

    public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
        return "mirthdew_encore_dreamtwirls";
    }

    public DreamtwirlStageManager(ServerWorld world) {
        this.world = world;
        this.markDirty();
    }

    @Nullable
    public DreamtwirlStage getDreamtwirlIfPresent(RegionPos regionPos) {
        return this.dreamtwirls.getOrDefault(regionPos.id, null);
    }

    public DreamtwirlStage getDreamtwirl(long id) {
        return this.dreamtwirls.get(id);
    }

    public void tick() {
        Iterator<DreamtwirlStage> iterator = this.dreamtwirls.values().iterator();

        boolean markDirty = false;
        while(iterator.hasNext()) {
            DreamtwirlStage dreamtwirlStage = iterator.next();

            dreamtwirlStage.tick(this.world);

            if(dreamtwirlStage.isDirty()) {
                markDirty = true;
                dreamtwirlStage.markDirty(false);
            }
        }

        if(markDirty) {
            this.markDirty();
        }
    }

    public void forEach(BiConsumer<Long, DreamtwirlStage> biConsumer) {
        this.dreamtwirls.forEach(biConsumer);
    }

    public int getDreamtwirlStageCount() {
        return this.dreamtwirls.size();
    }

    public Optional<DreamtwirlStage> createNewStage() {
        Optional<RegionPos> regionPosOptional = findUnusedRegionPos();
        if(regionPosOptional.isPresent()) {
            DreamtwirlStage stage = getOrCreateDreamtwirlStage(regionPosOptional.get());
            return Optional.of(stage);
        } else {
            return Optional.empty();
        }
    }

    public Optional<RegionPos> findUnusedRegionPos() {
        // TODO consider changing this implementation in the future? some sort of spiral pattern would probably work well
        int MAX_RADIUS = 100;
        for(int radius = 0; radius < MAX_RADIUS; radius++) {
            for(int i = 0; i < 1 + radius; i++) {
                // get random X,Z in range [-radius, radius]
                int randomX = random.nextInt(1 + 2 * radius) - radius;
                int randomZ = random.nextInt(1 + 2 * radius) - radius;
                // multiply by 2 to assure region sized gaps between all Dreamtwirls
                RegionPos candidatePos = new RegionPos(randomX * 2, randomZ * 2);
                long candidateId = candidatePos.id;
                if(!this.dreamtwirls.containsKey(candidateId)) {
                    return Optional.of(candidatePos);
                }
            }
        }

        return Optional.empty();
    }

    public DreamtwirlStage getOrCreateDreamtwirlStage(RegionPos regionPos) {
        if(!DreamtwirlStage.isIdAllowed(regionPos.id)) {
            MirthdewEncore.LOGGER.info("Created a Dreamtwirl with non-even region coordinates, this is probably fine, but shouldn't be possible???");
        }

        long id = regionPos.id;
        if(this.dreamtwirls.containsKey(id)) {
            return this.getDreamtwirl(id);
        } else {
            DreamtwirlStage dreamtwirlStage = new DreamtwirlStage(this.world, id, this.world.getTime());
            this.dreamtwirls.put(id, dreamtwirlStage);
            this.markDirty();
            return dreamtwirlStage;
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        NbtList nbtList = new NbtList();

        for(DreamtwirlStage dreamtwirlStage : this.dreamtwirls.values()) {
            NbtCompound nbtCompound = new NbtCompound();
            dreamtwirlStage.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }

        nbt.put("DreamtwirlStages", nbtList);

        return nbt;
    }

    public static DreamtwirlStageManager fromNbt(ServerWorld world, NbtCompound nbt) {
        DreamtwirlStageManager dreamtwirlStageManager = new DreamtwirlStageManager(world);

        NbtList nbtList = nbt.getList("DreamtwirlStages", NbtElement.COMPOUND_TYPE);

        for(int i = 0; i < nbtList.size(); ++i) {
            NbtCompound nbtCompound = nbtList.getCompound(i);
            DreamtwirlStage dreamtwirlStage = DreamtwirlStage.fromNbt(world, nbtCompound);
            dreamtwirlStageManager.dreamtwirls.put(dreamtwirlStage.getId(), dreamtwirlStage);
        }

        return dreamtwirlStageManager;
    }

    @Nullable
    public static DreamtwirlStageManager getDreamtwirlStageManager(World world) {
        DreamtwirlWorldAttachment DTWA = DreamtwirlWorldAttachment.fromWorld(world);
        if(DTWA == null) {
            return null;
        }
        return DTWA.getDreamtwirlStageManager();
    }

    @Nullable
    public static DreamtwirlStageManager getMainDreamtwirlStageManager(MinecraftServer server) {
        ServerWorld dreamtwirlWorld = server.getWorld(MirthdewEncoreDimensions.DREAMTWIRL_WORLD);
        if(dreamtwirlWorld == null) {
            return null;
        } else {
            return getDreamtwirlStageManager(dreamtwirlWorld);
        }
    }
}
