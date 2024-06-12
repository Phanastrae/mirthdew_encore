package phanastrae.mirthlight_encore.dreamtwirl;

import com.google.common.collect.Maps;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthlight_encore.util.RegionPos;
import phanastrae.mirthlight_encore.world.dimension.MirthlightEncoreDimensions;

import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;

public class DreamtwirlStageManager extends PersistentState {

    private final Map<Long, DreamtwirlStage> dreamtwirls = Maps.newHashMap();
    private final ServerWorld world;

    public static Type<DreamtwirlStageManager> getPersistentStateType(ServerWorld world) {
        return new Type<>(
                () -> new DreamtwirlStageManager(world),
                (nbt, registryLookup) -> fromNbt(world, nbt),
                null
        );
    }

    public static String nameFor(RegistryEntry<DimensionType> dimensionTypeEntry) {
        return "mirthlight_encore_dreamtwirls";
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

    public DreamtwirlStage getOrCreateDreamtwirlStage(RegionPos regionPos) {
        long id = regionPos.id;
        if(this.dreamtwirls.containsKey(id)) {
            return this.getDreamtwirl(id);
        } else {
            DreamtwirlStage dreamtwirlStage = new DreamtwirlStage(id, this.world.getTime());
            this.dreamtwirls.put(id, dreamtwirlStage);
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
        ServerWorld dreamtwirlWorld = server.getWorld(MirthlightEncoreDimensions.DREAMTWIRL_WORLD);
        if(dreamtwirlWorld == null) {
            return null;
        } else {
            return getDreamtwirlStageManager(dreamtwirlWorld);
        }
    }
}
