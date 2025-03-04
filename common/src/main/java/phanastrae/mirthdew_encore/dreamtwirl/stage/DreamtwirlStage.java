package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.Acherune;
import phanastrae.mirthdew_encore.dreamtwirl.stage.acherune.StageAcherunes;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignGenerator;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSourceCollection;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceableRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place.PlaceableRoomStorage;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.vista.VistaTypes;
import phanastrae.mirthdew_encore.network.packet.DreamtwirlDebugPayload;
import phanastrae.mirthdew_encore.services.XPlatInterface;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.List;
import java.util.Optional;

public class DreamtwirlStage extends SavedData {
    public static final String KEY_PLACEABLE_ROOM_DATA = "placeable_room_data";
    public static final String KEY_ACHERUNE_DATA = "acherune_data";

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

        this.setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // TODO make sure everything is properly setDirty()-ed!

        if(this.level instanceof ServerLevel serverLevel) {
            StructurePieceSerializationContext spsContext = StructurePieceSerializationContext.fromLevel(serverLevel);

            tag.put(KEY_PLACEABLE_ROOM_DATA, this.placeableRoomStorage.writeNbt(new CompoundTag(), registries, spsContext));
        }

        tag.put(KEY_ACHERUNE_DATA, this.stageAcherunes.writeNbt(new CompoundTag(), registries));

        // TODO save stageDesignGenerator

        return tag;
    }

    public static DreamtwirlStage fromNbt(Level level, BasicStageData bsd, CompoundTag tag, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        DreamtwirlStage stage = new DreamtwirlStage(level, bsd);

        if(tag.contains(KEY_PLACEABLE_ROOM_DATA, Tag.TAG_COMPOUND)) {
            stage.getRoomStorage().readNbt(tag.getCompound(KEY_PLACEABLE_ROOM_DATA), registries, spsContext);
        }

        if(tag.contains(KEY_ACHERUNE_DATA, Tag.TAG_COMPOUND)) {
            stage.getStageAcherunes().readNbt(tag.getCompound(KEY_ACHERUNE_DATA), registries);
        }

        // TODO load stageDesignGenerator

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
        Optional<PlaceableRoom> roomOptional = this.placeableRoomStorage.getRoom(roomId);
        if(roomOptional.isPresent()) {
            PlaceableRoom room = roomOptional.get();

            room.openLychseal(this.placeableRoomStorage, lychsealName);
        }
    }

    public void tick(ServerLevel level) {
        // TODO tweak, optimise, etc.
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

        for(PlaceableRoom room : this.placeableRoomStorage.getRooms()) {
            if(!room.shouldTick()) continue;

            room.tick(level, this.placeableRoomStorage, this.stageAreaData.getInBoundsBoundingBox(), this);
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

    public PlaceableRoomStorage getRoomStorage() {
        return this.placeableRoomStorage;
    }

    public StageAcherunes getStageAcherunes() {
        return stageAcherunes;
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
        prrs.addRooms(designData.getRoomList());
        prrs.addConnections(designData.getRoomGraph());
        prrs.beginEntrancePlacement();
    }
}
