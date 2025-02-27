package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.stage.BasicStageData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.DreamtwirlStage;
import phanastrae.mirthdew_encore.util.RegionPos;
import phanastrae.mirthdew_encore.world.dimension.MirthdewEncoreDimensions;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;

public class DreamtwirlStageManager extends SavedData {

    private final Map<Long, BasicStageData> basicStageDatas = new Object2ObjectOpenHashMap<>();
    private final Map<Long, DreamtwirlStage> dreamtwirls = new Object2ObjectOpenHashMap<>();

    private final ServerLevel level;
    private final RandomSource random = RandomSource.create();

    public DreamtwirlStageManager(ServerLevel level) {
        this.level = level;
        this.setDirty();
    }

    public static Factory<DreamtwirlStageManager> getPersistentStateType(ServerLevel level) {
        return new Factory<>(
                () -> new DreamtwirlStageManager(level),
                (nbt, registryLookup) -> fromNbt(level, nbt),
                null
        );
    }

    public static String nameFor(Holder<DimensionType> dimensionTypeEntry) {
        return "mirthdew_encore_dreamtwirls";
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        ListTag nbtList = new ListTag();

        for(BasicStageData bsd : this.basicStageDatas.values()) {
            CompoundTag nbtCompound = new CompoundTag();
            bsd.writeNbt(nbtCompound);
            nbtList.add(nbtCompound);
        }

        nbt.put("DreamtwirlStages", nbtList);

        return nbt;
    }

    public static DreamtwirlStageManager fromNbt(ServerLevel level, CompoundTag nbt) {
        DreamtwirlStageManager dreamtwirlStageManager = new DreamtwirlStageManager(level);

        ListTag nbtList = nbt.getList("DreamtwirlStages", Tag.TAG_COMPOUND);

        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            BasicStageData bsd = BasicStageData.fromNbt(level, nbtCompound);
            dreamtwirlStageManager.basicStageDatas.put(bsd.getId(), bsd);
        }

        return dreamtwirlStageManager;
    }

    @Nullable
    public DreamtwirlStage getDreamtwirlIfPresent(RegionPos regionPos) {
        return this.getDreamtwirlIfPresent(regionPos.id);
    }

    @Nullable
    public DreamtwirlStage getDreamtwirlIfPresent(long id) {
        if(this.dreamtwirls.containsKey(id)) {
            return this.dreamtwirls.get(id);
        } else if(this.basicStageDatas.containsKey(id)) {
            BasicStageData basicStageData = this.basicStageDatas.get(id);

            DreamtwirlStage dreamtwirlStage = getStageSavedData(basicStageData);
            this.dreamtwirls.put(id, dreamtwirlStage);

            this.setDirty();
            return dreamtwirlStage;
        } else {
            return null;
        }
    }

    public BasicStageData getBasicStageData(long id) {
        return this.basicStageDatas.get(id);
    }

    public void tick() {
        for(DreamtwirlStage dreamtwirlStage : this.dreamtwirls.values()) {
            dreamtwirlStage.tick(this.level);
        }
    }

    public void forEach(BiConsumer<Long, BasicStageData> biConsumer) {
        this.basicStageDatas.forEach(biConsumer);
    }

    public int getDreamtwirlStageCount() {
        return this.basicStageDatas.size();
    }

    public int getDreamtwirlLoadedStagesCount() {
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
                if(!this.basicStageDatas.containsKey(candidateId)) {
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
            return this.dreamtwirls.get(id);
        } else {
            BasicStageData basicStageData = this.getOrCreateBasicStageData(regionPos);

            DreamtwirlStage dreamtwirlStage = getStageSavedData(basicStageData);
            this.dreamtwirls.put(id, dreamtwirlStage);

            this.setDirty();
            return dreamtwirlStage;
        }
    }

    public BasicStageData getOrCreateBasicStageData(RegionPos regionPos) {
        long id = regionPos.id;
        if(this.basicStageDatas.containsKey(id)) {
            return this.getBasicStageData(id);
        } else {
            BasicStageData basicStageData = new BasicStageData(regionPos.id, this.level.getGameTime());
            this.basicStageDatas.put(id, basicStageData);

            this.setDirty();
            return basicStageData;
        }
    }

    public DreamtwirlStage getStageSavedData(BasicStageData basicStageData) {
        return this.level.getDataStorage().computeIfAbsent(
                DreamtwirlStage.getPersistentStateType(this.level, basicStageData),
                DreamtwirlStage.nameFor(basicStageData.getRegionPos()));
    }

    @Nullable
    public static DreamtwirlStageManager getDreamtwirlStageManager(Level level) {
        DreamtwirlLevelAttachment DTWA = DreamtwirlLevelAttachment.fromLevel(level);
        if(DTWA == null) {
            return null;
        }
        return DTWA.getDreamtwirlStageManager();
    }

    @Nullable
    public static DreamtwirlStageManager getMainDreamtwirlStageManager(MinecraftServer server) {
        ServerLevel dreamtwirlLevel = server.getLevel(MirthdewEncoreDimensions.DREAMTWIRL_WORLD);
        if(dreamtwirlLevel == null) {
            return null;
        } else {
            return getDreamtwirlStageManager(dreamtwirlLevel);
        }
    }

    @Nullable
    public static DreamtwirlStage getStage(Level level, RegionPos regionPos) {
        DreamtwirlStageManager dsm = getDreamtwirlStageManager(level);
        if(dsm != null) {
            return dsm.getDreamtwirlIfPresent(regionPos);
        }
        return null;
    }

    @Nullable
    public static DreamtwirlStage getStage(Level level, BlockPos pos) {
        return getStage(level, RegionPos.fromBlockPos(pos));
    }

    @Nullable
    public static DreamtwirlStage getStage(Level level, long stageId) {
        return getStage(level, new RegionPos(stageId));
    }
}
