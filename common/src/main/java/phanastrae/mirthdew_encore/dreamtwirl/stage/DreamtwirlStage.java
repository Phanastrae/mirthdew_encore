package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.dreamtwirl.DreamtwirlStageManager;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignGenerator;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.destroy.StageNuker;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceableRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceableRoomStorage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaType;
import phanastrae.mirthdew_encore.dreamtwirl.stage.play.DreamtwirlBorder;
import phanastrae.mirthdew_encore.network.packet.DreamtwirlDebugPayload;
import phanastrae.mirthdew_encore.registry.MirthdewEncoreRegistries;
import phanastrae.mirthdew_encore.services.XPlatInterface;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class DreamtwirlStage extends SavedData {
    public static final String KEY_PLACEABLE_ROOM_DATA = "placeable_room_data";
    public static final String KEY_STAGE_DESIGN_GENERATOR = "stage_design_generator";
    public static final String KEY_ACHERUNE_DATA = "acherune_data";
    public static final String KEY_DELETING_SELF = "is_deleting_self";
    public static final String KEY_CHUNK_DELETION_PROGRESS = "chunk_deletion_progress";

    public static boolean SEND_DEBUG_INFO = true;

    private final Level level;

    private final BasicStageData basicStageData;
    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;
    private final StageAreaData stageAreaData;

    private final PlaceableRoomStorage placeableRoomStorage;
    private final StageAcherunes stageAcherunes;

    @Nullable
    private StageDesignGenerator stageDesignGenerator;

    private final DreamtwirlBorder dreamtwirlBorder;

    private boolean isDeletingSelf = false;
    private int chunkDeletionProgress = 0;
    private boolean isRemoved = false;

    public DreamtwirlStage(Level level, BasicStageData basicStageData) {
        this.level = level;

        this.basicStageData = basicStageData;
        this.id = basicStageData.getId();
        this.regionPos = basicStageData.getRegionPos();
        this.timestamp = basicStageData.getTimestamp();
        this.stageAreaData = new StageAreaData(
                this.regionPos,
                this.level.getMinBuildHeight(),
                this.level.getHeight(),
                this.level.getMaxBuildHeight()
        );

        this.placeableRoomStorage = new PlaceableRoomStorage();
        this.stageAcherunes = new StageAcherunes(this);

        this.dreamtwirlBorder = new DreamtwirlBorder(this.regionPos);
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // TODO make sure everything is properly setDirty()-ed!

        if(this.level instanceof ServerLevel serverLevel) {
            StructurePieceSerializationContext spsContext = StructurePieceSerializationContext.fromLevel(serverLevel);

            tag.put(KEY_PLACEABLE_ROOM_DATA, this.placeableRoomStorage.writeNbt(new CompoundTag(), registries, spsContext));
        }

        if(this.stageDesignGenerator != null) {
            tag.put(KEY_STAGE_DESIGN_GENERATOR, this.stageDesignGenerator.writeNbt(new CompoundTag(), registries));
        }

        tag.put(KEY_ACHERUNE_DATA, this.stageAcherunes.writeNbt(new CompoundTag(), registries));

        tag.putBoolean(KEY_DELETING_SELF, this.isDeletingSelf);

        if(this.isDeletingSelf) {
            tag.putInt(KEY_CHUNK_DELETION_PROGRESS, this.chunkDeletionProgress);
        }

        return tag;
    }

    public static DreamtwirlStage fromNbt(Level level, BasicStageData bsd, CompoundTag tag, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        DreamtwirlStage stage = new DreamtwirlStage(level, bsd);

        if(tag.contains(KEY_PLACEABLE_ROOM_DATA, Tag.TAG_COMPOUND)) {
            stage.getRoomStorage().readNbt(tag.getCompound(KEY_PLACEABLE_ROOM_DATA), registries, spsContext, level);
        }

        if(tag.contains(KEY_STAGE_DESIGN_GENERATOR, Tag.TAG_COMPOUND) && level instanceof ServerLevel serverLevel) {
            CompoundTag sdgTag = tag.getCompound(KEY_STAGE_DESIGN_GENERATOR);
            stage.stageDesignGenerator = StageDesignGenerator.fromNbt(sdgTag, registries, stage.stageAreaData, serverLevel);
        } else {
            stage.stageDesignGenerator = null;
        }

        if(tag.contains(KEY_ACHERUNE_DATA, Tag.TAG_COMPOUND)) {
            stage.getStageAcherunes().readNbt(tag.getCompound(KEY_ACHERUNE_DATA), registries);
        }

        if(tag.contains(KEY_DELETING_SELF, Tag.TAG_BYTE)) {
            stage.setDeletingSelf(tag.getBoolean(KEY_DELETING_SELF));
        } else {
            stage.setDeletingSelf(false);
        }

        if(tag.contains(KEY_CHUNK_DELETION_PROGRESS, Tag.TAG_INT)) {
            stage.chunkDeletionProgress = tag.getInt(KEY_CHUNK_DELETION_PROGRESS);
        } else {
            stage.chunkDeletionProgress = 0;
        }

        return stage;
    }

    public static boolean isIdAllowed(long id) {
        return isIdAllowed(new RegionPos(id));
    }

    public static boolean isIdAllowed(RegionPos rp) {
        return ((rp.regionX & 0x1) == 0) && ((rp.regionZ & 0x1) == 0);
    }

    public boolean isReady() {
        return !this.stageAcherunes.isEmpty();
    }

    @Nullable
    public Acherune getEntranceAcherune(RandomSource random) {
        return this.stageAcherunes.getRandomEmptyEntranceAcherune(random);
    }

    public void generate(long stageSeed, ServerLevel serverLevel) {
        Optional<Registry<VistaType>> vistaTypeRegistryOptional = serverLevel.registryAccess().registry(MirthdewEncoreRegistries.VISTA_TYPE_KEY);
        if(vistaTypeRegistryOptional.isEmpty()) return;
        Registry<VistaType> vistaTypeRegistry = vistaTypeRegistryOptional.get();

        VistaType vistaType = vistaTypeRegistry.get(MirthdewEncore.id("decidrheum_forest"));
        if(vistaType == null) return;

        RoomSourceCollection roomSources = RoomSourceCollection.create(vistaType);
        this.stageDesignGenerator = new StageDesignGenerator(this.getStageAreaData(), serverLevel, stageSeed, roomSources);

        this.setDirty();
    }

    public void openLychseal(int roomId, String lychsealName) {
        Optional<PlaceableRoom> roomOptional = this.placeableRoomStorage.getRoom(roomId);
        if(roomOptional.isPresent()) {
            PlaceableRoom room = roomOptional.get();

            if(room.openLychseal(this.placeableRoomStorage, lychsealName)) {
                this.setDirty();
            }
        }
    }

    public void tick(ServerLevel level, boolean runsNormally) {
        if(this.isDeletingSelf) {
            this.tickSelfDeletion(level, runsNormally);
        }

        if(!this.isDeletingSelf) {
            // TODO tweak, optimise, etc.
            if (runsNormally) {
                if (this.stageDesignGenerator != null) {
                    boolean done = this.stageDesignGenerator.tick();
                    if (done) {
                        sendRoomsToStorage(this.getRoomStorage(), stageDesignGenerator.getDesignData());

                        if (SEND_DEBUG_INFO) { // TODO do debug info properly
                            List<ServerPlayer> players = level.getPlayers(p -> true);
                            if (!players.isEmpty()) {
                                DreamtwirlDebug.DebugInfo debugInfo = DreamtwirlDebugPayload.createDebugInfo(this.stageDesignGenerator.getDesignData(), this.id);
                                DreamtwirlDebugPayload payload = new DreamtwirlDebugPayload(debugInfo);
                                for (ServerPlayer player : players) {
                                    XPlatInterface.INSTANCE.sendPayload(player, payload);
                                }
                            }
                        }
                        this.stageDesignGenerator = null;
                    }

                    this.setDirty(); // TODO review setDirty()'s
                }

                for (PlaceableRoom room : this.placeableRoomStorage.getRooms()) {
                    if (!room.shouldTick()) continue;

                    room.tick(level, this.placeableRoomStorage, this.stageAreaData.getInBoundsBoundingBox(), this);
                }
            }
        }
    }

    public void tickSelfDeletion(ServerLevel level, boolean runsNormally) {
        if(this.stageDesignGenerator != null || !this.placeableRoomStorage.getRooms().isEmpty()) {
            this.abortDestruction();
        }

        if(runsNormally) {
            DreamtwirlStageManager dsm = DreamtwirlStageManager.getDreamtwirlStageManager(level);
            if (dsm != null) {
                int CLEAR_COUNT = 1;
                int PRELOAD_COUNT = 2; // i have no idea if this value makes sense

                // preload chunks
                for(int i = 0; i < PRELOAD_COUNT; i++) {
                    int target = this.chunkDeletionProgress + i;
                    if(0 <= target && target < 900) {
                        ChunkPos targetChunkPos = StageNuker.getChunkPosForProgress(this.regionPos, target);
                        StageNuker.tryPreLoadChunk(level, targetChunkPos);
                    }
                }
                // try to clear chunks
                for (int i = 0; i < CLEAR_COUNT; i++) {
                    if (0 <= this.chunkDeletionProgress && this.chunkDeletionProgress < 900) {
                        ChunkPos targetChunkPos = StageNuker.getChunkPosForProgress(this.regionPos, this.chunkDeletionProgress);

                        if(StageNuker.tryClearChunk(level, targetChunkPos)) {
                            this.setChunkDeletionProgress(this.chunkDeletionProgress + 1);
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                }

                if(this.chunkDeletionProgress >= 900) { // if all chunks are cleared
                    dsm.deleteDreamtwirlStage(this.regionPos);
                    this.setDeletingSelf(false);
                    this.setDirty();
                }
            }
        }
    }

    public boolean initateSelfDestruct() {
        // initiates self destruct
        // this self destructs the dreamtwirl
        // dreamtwirl go bye bye

        // i sure do hope that this never ever gets triggered by accident :)

        if(this.isDeletingSelf) {
            return false;
        } else {
            this.clearDesignGenerator();
            this.clearRoomStorage();
            this.clearAcherunes();
            this.setDeletingSelf(true);

            this.setDirty();
            return true;
        }
    }

    public void abortDestruction() {
        this.setDeletingSelf(false);
        this.setDirty();
    }

    public void beginPlacingAllRooms() {
        AtomicBoolean changed = new AtomicBoolean(false);
        this.getRoomStorage().getRooms().forEach(room -> {
            if(!room.isRoomPlaced()) {
                room.beginPlacementFromCenter(false);
                changed.set(true);
            }
        });

        if(changed.get()) {
            this.setDirty();
        }
    }

    public boolean clearDesignGenerator() {
        if(this.stageDesignGenerator != null) {
            this.stageDesignGenerator = null;
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public boolean clearRoomStorage() {
        if(this.getRoomStorage().reset()) {
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public boolean clearAcherunes() {
        if(this.stageAcherunes.reset()) {
            this.setDirty();
            return true;
        } else {
            return false;
        }
    }

    public void setRemoved(boolean removed) {
        this.isRemoved = removed;
    }

    public boolean isRemoved() {
        return this.isRemoved;
    }

    public void setDeletingSelf(boolean deletingSelf) {
        if(this.isDeletingSelf != deletingSelf) {
            this.isDeletingSelf = deletingSelf;
            this.setDirty();

            this.setChunkDeletionProgress(0);

            DreamtwirlStageManager dsm = DreamtwirlStageManager.getDreamtwirlStageManager(this.level);
            if(dsm != null) {
                BasicStageData bsd = dsm.getBasicStageDataIfPresent(this.id);

                if(bsd != null && bsd.getTimestamp() == this.timestamp) {
                    bsd.setDeletingSelf(deletingSelf);
                    dsm.setDirty();
                }
            }
        }
    }

    public boolean isDeletingSelf() {
        return this.isDeletingSelf;
    }

    public void setChunkDeletionProgress(int chunkDeletionProgress) {
        this.chunkDeletionProgress = chunkDeletionProgress;
        this.setDirty();
    }

    public int getChunkDeletionProgress() {
        return chunkDeletionProgress;
    }

    public long getId() {
        return this.id;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public RegionPos getRegionPos() {
        return this.regionPos;
    }

    public Level getLevel() {
        return this.level;
    }

    public StageAreaData getStageAreaData() {
        return stageAreaData;
    }

    public PlaceableRoomStorage getRoomStorage() {
        return this.placeableRoomStorage;
    }

    public @Nullable StageDesignGenerator getStageDesignGenerator() {
        return stageDesignGenerator;
    }

    public StageAcherunes getStageAcherunes() {
        return stageAcherunes;
    }

    public DreamtwirlBorder getDreamtwirlBorder() {
        return dreamtwirlBorder;
    }

    public long getAgeInTicks() {
        return this.basicStageData.getAgeInTicks(this.level.getGameTime());
    }

    public Component getAgeTextComponent() {
        return BasicStageData.getAgeTextComponent(this.getAgeInTicks());
    }

    public static SavedData.Factory<DreamtwirlStage> getPersistentStateType(ServerLevel level, BasicStageData bsd) {
        return new SavedData.Factory<>(
                () -> new DreamtwirlStage(level, bsd),
                (nbt, registryLookup) -> fromNbt(level, bsd, nbt, registryLookup, StructurePieceSerializationContext.fromLevel(level)),
                null
        );
    }

    public static String nameFor(RegionPos regionPos) {
        return "mirthdew_dreamtwirl_stage." + regionPos.regionX + "." + regionPos.regionZ;
    }

    public static void sendRoomsToStorage(PlaceableRoomStorage prrs, StageDesignData designData) {
        prrs.addRooms(designData.getIdToRoomMap().values().stream().toList());
        prrs.addConnections(designData, designData.getRoomGraph());
        prrs.beginEntrancePlacement();
    }
}
