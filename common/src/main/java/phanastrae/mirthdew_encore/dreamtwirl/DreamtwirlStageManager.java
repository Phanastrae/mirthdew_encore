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

import java.util.*;
import java.util.function.BiConsumer;

public class DreamtwirlStageManager extends SavedData {
    public static final String KEY_MIN_RX = "min_region_x";
    public static final String KEY_MIN_RZ = "min_region_z";
    public static final String KEY_MAX_RX = "max_region_x";
    public static final String KEY_MAX_RZ = "max_region_z";
    // default values are the furthest out dreamtwirls that don't intersect or bypass the world border
    public static final int DEFAULT_MIN_REGION_BOUND = -58593;
    public static final int DEFAULT_MAX_REGION_BOUND = 58592;

    private static final int CACHE_SIZE = 4;

    private final Map<Long, BasicStageData> basicStageDatas = new Object2ObjectOpenHashMap<>();
    private final Map<Long, DreamtwirlStage> dreamtwirls = new Object2ObjectOpenHashMap<>();

    private final ServerLevel level;
    private final RandomSource random = RandomSource.create();

    private boolean hasCheckedForDeletingStages = false;

    private final Long[] lastId = new Long[CACHE_SIZE];
    private final DreamtwirlStage[] lastStage = new DreamtwirlStage[CACHE_SIZE];

    // region position bounds for naturally generating new dreamtwirls
    private int minRegionX = DEFAULT_MIN_REGION_BOUND;
    private int minRegionZ = DEFAULT_MIN_REGION_BOUND;
    private int maxRegionX = DEFAULT_MAX_REGION_BOUND;
    private int maxRegionZ = DEFAULT_MAX_REGION_BOUND;

    public DreamtwirlStageManager(ServerLevel level) {
        this.level = level;
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

        nbt.putInt(KEY_MIN_RX, this.minRegionX);
        nbt.putInt(KEY_MIN_RZ, this.minRegionZ);
        nbt.putInt(KEY_MAX_RX, this.maxRegionX);
        nbt.putInt(KEY_MAX_RZ, this.maxRegionZ);

        return nbt;
    }

    public static DreamtwirlStageManager fromNbt(ServerLevel level, CompoundTag nbt) {
        DreamtwirlStageManager dreamtwirlStageManager = new DreamtwirlStageManager(level);

        ListTag nbtList = nbt.getList("DreamtwirlStages", Tag.TAG_COMPOUND);

        for(int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            BasicStageData bsd = BasicStageData.fromNbt(nbtCompound);
            dreamtwirlStageManager.basicStageDatas.put(bsd.getId(), bsd);
        }

        if(nbt.contains(KEY_MIN_RX, Tag.TAG_INT)) {
            dreamtwirlStageManager.minRegionX = nbt.getInt(KEY_MIN_RX);
        }
        if(nbt.contains(KEY_MIN_RZ, Tag.TAG_INT)) {
            dreamtwirlStageManager.minRegionZ = nbt.getInt(KEY_MIN_RZ);
        }
        if(nbt.contains(KEY_MAX_RX, Tag.TAG_INT)) {
            dreamtwirlStageManager.maxRegionX = nbt.getInt(KEY_MAX_RX);
        }
        if(nbt.contains(KEY_MAX_RZ, Tag.TAG_INT)) {
            dreamtwirlStageManager.maxRegionZ = nbt.getInt(KEY_MAX_RZ);
        }

        return dreamtwirlStageManager;
    }

    @Nullable
    public DreamtwirlStage getDreamtwirlIfPresent(RegionPos regionPos) {
        return this.getDreamtwirlIfPresent(regionPos.id);
    }

    @Nullable
    public DreamtwirlStage getDreamtwirlIfPresent(long id) {
        for (int j = 0; j < CACHE_SIZE; j++) {
            if (Objects.equals(id, this.lastId[j])) {
                DreamtwirlStage stage = this.lastStage[j];
                if (stage != null && !stage.isRemoved()) {
                    return stage;
                }
            }
        }

        DreamtwirlStage stage;
        if(this.dreamtwirls.containsKey(id)) {
            stage = this.dreamtwirls.get(id);
        } else if(this.basicStageDatas.containsKey(id)) {
            BasicStageData basicStageData = this.basicStageDatas.get(id);

            DreamtwirlStage dreamtwirlStage = getExistingSavedStageData(basicStageData);
            this.dreamtwirls.put(id, dreamtwirlStage);

            this.setDirty();
            stage = dreamtwirlStage;
        } else {
            stage = null;
        }
        if(stage != null) {
            this.storeInCache(id, stage);
        }
        return stage;
    }

    private void storeInCache(long id, DreamtwirlStage stage) {
        for (int i = CACHE_SIZE - 1; i > 0; i--) {
            this.lastId[i] = this.lastId[i - 1];
            this.lastStage[i] = this.lastStage[i - 1];
        }

        this.lastId[0] = id;
        this.lastStage[0] = stage;
    }

    private void clearCache() {
        Arrays.fill(this.lastId, null);
        Arrays.fill(this.lastStage, null);
    }

    public @Nullable BasicStageData getBasicStageDataIfPresent(long id) {
        return this.basicStageDatas.getOrDefault(id, null);
    }

    public void tick(boolean runsNormally) {
        if(!this.hasCheckedForDeletingStages) {
            for(BasicStageData bsd : this.basicStageDatas.values()) {
                if(bsd.isDeletingSelf()) {
                    this.getDreamtwirlIfPresent(bsd.getId()); // make sure dreamtwirl is loaded
                }
            }

            this.hasCheckedForDeletingStages = true;
        }

        List<DreamtwirlStage> stages = this.dreamtwirls.values().stream().toList(); // copy list to allow for safe removal from stage manager mid-ticking
        for( DreamtwirlStage dreamtwirlStage : stages) {
            dreamtwirlStage.tick(this.level, runsNormally);
        }
    }

    public void forEach(BiConsumer<Long, BasicStageData> biConsumer) {
        this.basicStageDatas.forEach(biConsumer);
    }

    public Map<Long, BasicStageData> getBasicStageDatas() {
        return basicStageDatas;
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

                if(!regionPosInBounds(candidatePos)) continue;

                long candidateId = candidatePos.id;
                if(!this.basicStageDatas.containsKey(candidateId)) {
                    return Optional.of(candidatePos);
                }
            }
        }

        return Optional.empty();
    }

    public boolean regionPosInBounds(RegionPos regionPos) {
        int rx = regionPos.regionX;
        int rz = regionPos.regionZ;
        if(rx < this.minRegionX || this.maxRegionX < rx) {
            return false;
        } else if(rz < this.minRegionZ || this.maxRegionZ < rz) {
            return false;
        } else {
            return true;
        }
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

            DreamtwirlStage dreamtwirlStage = createNewSavedStageData(basicStageData);
            this.dreamtwirls.put(id, dreamtwirlStage);

            this.setDirty();
            return dreamtwirlStage;
        }
    }

    public boolean deleteDreamtwirlStage(RegionPos regionPos) {
        long id = regionPos.id;

        boolean deleted = false;
        if(this.basicStageDatas.containsKey(id)) {
            this.basicStageDatas.remove(id);
            deleted = true;
        }
        this.getDreamtwirlIfPresent(id); // make sure dreamtwirl is loaded
        if(this.dreamtwirls.containsKey(id)) {
            this.dreamtwirls.remove(id).setRemoved(true);
            deleted = true;
        }

        if(deleted) {
            this.clearCache();
            this.setDirty();
        }

        return deleted;
    }

    public BasicStageData getOrCreateBasicStageData(RegionPos regionPos) {
        long id = regionPos.id;
        if(this.basicStageDatas.containsKey(id)) {
            return this.basicStageDatas.get(id);
        } else {
            BasicStageData basicStageData = new BasicStageData(regionPos.id, this.level.getGameTime());
            this.basicStageDatas.put(id, basicStageData);

            this.setDirty();
            return basicStageData;
        }
    }

    public DreamtwirlStage getExistingSavedStageData(BasicStageData basicStageData) {
        String name = DreamtwirlStage.nameFor(basicStageData.getRegionPos());
        SavedData.Factory<DreamtwirlStage> factory = DreamtwirlStage.getPersistentStateType(this.level, basicStageData);

        return this.level.getDataStorage().computeIfAbsent(factory, name);
    }

    public DreamtwirlStage createNewSavedStageData(BasicStageData basicStageData) {
        String name = DreamtwirlStage.nameFor(basicStageData.getRegionPos());
        SavedData.Factory<DreamtwirlStage> factory = DreamtwirlStage.getPersistentStateType(this.level, basicStageData);

        DreamtwirlStage stage = factory.constructor().get();
        this.level.getDataStorage().set(name, stage);
        return stage;
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

    public boolean setRegionBounds(int minX, int minZ, int maxX, int maxZ) {
        if(minX < DEFAULT_MIN_REGION_BOUND) {
            minX = DEFAULT_MIN_REGION_BOUND;
        }
        if(minZ < DEFAULT_MIN_REGION_BOUND) {
            minZ = DEFAULT_MIN_REGION_BOUND;
        }
        if(maxX > DEFAULT_MAX_REGION_BOUND) {
            minX = DEFAULT_MAX_REGION_BOUND;
        }
        if(maxZ > DEFAULT_MAX_REGION_BOUND) {
            maxZ = DEFAULT_MAX_REGION_BOUND;
        }

        if(minX > maxX || minZ > maxZ) {
            return false;
        }

        this.minRegionX = minX;
        this.minRegionZ = minZ;
        this.maxRegionX = maxX;
        this.maxRegionZ = maxZ;
        this.setDirty();

        return true;
    }

    public int getMinRegionX() {
        return minRegionX;
    }

    public int getMinRegionZ() {
        return minRegionZ;
    }

    public int getMaxRegionX() {
        return maxRegionX;
    }

    public int getMaxRegionZ() {
        return maxRegionZ;
    }
}
