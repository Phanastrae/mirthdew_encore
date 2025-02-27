package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignGenerator;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceReadyRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceReadyRoomStorage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.RoomPlacer;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaTypes;
import phanastrae.mirthdew_encore.network.packet.DreamtwirlDebugPayload;
import phanastrae.mirthdew_encore.services.XPlatInterface;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;
import java.util.Optional;

public class DreamtwirlStage extends SavedData {
    public static final String KEY_ACHERUNE_DATA = "acherune_data";

    public static boolean SEND_DEBUG_INFO = true;

    private final Level level;

    private final BasicStageData basicStageData;
    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;

    private final StageAreaData stageAreaData;
    private final PlaceReadyRoomStorage roomStorage;
    private final StageAcherunes stageAcherunes;

    @Nullable
    private StageDesignGenerator stageDesignGenerator;

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
        this.roomStorage = new PlaceReadyRoomStorage();
        this.stageAcherunes = new StageAcherunes(this);

        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put(KEY_ACHERUNE_DATA, this.stageAcherunes.writeNbt(new CompoundTag(), registries));

        return tag;
    }

    public static DreamtwirlStage fromNbt(Level level, BasicStageData bsd, CompoundTag tag, HolderLookup.Provider registries) {
        DreamtwirlStage stage = new DreamtwirlStage(level, bsd);

        if(tag.contains(KEY_ACHERUNE_DATA, Tag.TAG_COMPOUND)) {
            stage.getStageAcherunes().readNbt(tag.getCompound(KEY_ACHERUNE_DATA), registries);
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
        RoomSourceCollection roomSources = RoomSourceCollection.create(serverLevel, VistaTypes.DECIDRHEUM_FOREST);
        this.stageDesignGenerator = new StageDesignGenerator(this, serverLevel, stageSeed, roomSources);
    }

    public void openLychseal(int roomId, String lychsealName) {
        Optional<PlaceReadyRoom> roomOptional = this.roomStorage.getRoom(roomId);
        if(roomOptional.isPresent()) {
            PlaceReadyRoom room = roomOptional.get();

            room.openLychseal(lychsealName);
        }
    }

    public void tick(ServerLevel level) {
        // TODO tweak, optimise, etc.
        RandomSource random = level.getRandom();

        if(this.stageDesignGenerator != null) {
            boolean done = this.stageDesignGenerator.tick();
            if(done) {
                sendRoomsToStorage(this.getRoomStorage(), stageDesignGenerator.getDesignData());

                if(SEND_DEBUG_INFO) {
                    List<ServerPlayer> players = level.getPlayers(p -> true);
                    if(!players.isEmpty()) {
                        DreamtwirlDebug.DebugInfo debugInfo = DreamtwirlDebugPayload.createDebugInfo(stageDesignGenerator.getDesignData(), this.id);
                        DreamtwirlDebugPayload payload = new DreamtwirlDebugPayload(debugInfo);
                        for(ServerPlayer player : players) {
                            XPlatInterface.INSTANCE.sendPayload(player, payload);
                        }
                    }
                }
                this.stageDesignGenerator = null;
            }

            // TODO serialization
            this.setDirty();
        }

        boolean placedRoom = false;
        for(PlaceReadyRoom room : this.roomStorage.getRooms()) {
            if(room.isPlaced() || !room.canPlace()) {
                continue;
            }

            if(random.nextInt(45) != 0) {
                continue;
            }

            if(room.place(level, this.stageAreaData.getInBoundsBoundingBox())) {
                placedRoom = true;

                RoomPlacer.spawnParticles(level, room.getRoom());
                room.openLychseal("");

                // TODO serialization
                this.setDirty();
            }
        }
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

    public PlaceReadyRoomStorage getRoomStorage() {
        return this.roomStorage;
    }

    public StageAcherunes getStageAcherunes() {
        return stageAcherunes;
    }

    public static SavedData.Factory<DreamtwirlStage> getPersistentStateType(Level level, BasicStageData bsd) {
        return new SavedData.Factory<>(
                () -> new DreamtwirlStage(level, bsd),
                (nbt, registryLookup) -> fromNbt(level, bsd, nbt, registryLookup),
                null
        );
    }

    public static String nameFor(RegionPos regionPos) {
        return "mirthdew_dreamtwirl_stage." + regionPos.regionX + "." + regionPos.regionZ;
    }

    public static void sendRoomsToStorage(PlaceReadyRoomStorage prrs, StageDesignData designData) {
        prrs.addRooms(designData.getRoomList());
        prrs.addConnections(designData.getRoomGraph());
        prrs.enableEntranceSpawning();
    }
}
