package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
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

public class DreamtwirlStage {
    public static final String KEY_ID = "Id";
    public static final String KEY_TIMESTAMP = "Timestamp";
    public static final String KEY_ACHERUNE_DATA = "acherune_data";

    public static boolean SEND_DEBUG_INFO = true;

    private final Level level;

    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;

    private final StageAreaData stageAreaData;
    private final PlaceReadyRoomStorage roomStorage;
    private final StageAcherunes stageAcherunes;

    @Nullable
    private StageDesignGenerator stageDesignGenerator;

    private boolean markDirty = false;

    public DreamtwirlStage(Level level, long id, long timestamp) {
        this.level = level;

        this.id = id;
        this.regionPos = new RegionPos(id);
        this.timestamp = timestamp;

        this.stageAreaData = new StageAreaData(
                this.regionPos,
                this.level.getMinBuildHeight(),
                this.level.getHeight(),
                this.level.getMaxBuildHeight()
        );
        this.roomStorage = new PlaceReadyRoomStorage();
        this.stageAcherunes = new StageAcherunes();
    }

    public static boolean isIdAllowed(long id) {
        return isIdAllowed(new RegionPos(id));
    }

    public static boolean isIdAllowed(RegionPos rp) {
        return ((rp.regionX & 0x1) == 0) && ((rp.regionZ & 0x1) == 0);
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong(KEY_ID, this.getId());
        nbt.putLong(KEY_TIMESTAMP, this.getTimestamp());

        nbt.put(KEY_ACHERUNE_DATA, this.stageAcherunes.writeNbt(new CompoundTag()));
        return nbt;
    }

    public static DreamtwirlStage fromNbt(Level level, CompoundTag nbt) {
        long id = nbt.getLong(KEY_ID);
        long timestamp = nbt.getLong(KEY_TIMESTAMP);

        DreamtwirlStage stage = new DreamtwirlStage(level, id, timestamp);

        if(nbt.contains(KEY_ACHERUNE_DATA, Tag.TAG_COMPOUND)) {
            stage.getStageAcherunes().readNbt(nbt.getCompound(KEY_ACHERUNE_DATA));
        }
        return stage;
    }

    public boolean isReady() {
        return !this.stageAcherunes.isEmpty();
    }

    @Nullable
    public Vec3 getEntrancePos(RandomSource random) {
        Acherune acherune = this.stageAcherunes.getRandomEmptyEntranceAcherune(random);
        if(acherune == null) {
            return null;
        } else {
            return acherune.getPos().above().getBottomCenter();
        }
    }

    public void generate(long stageSeed, ServerLevel serverLevel) {
        RoomSourceCollection roomSources = RoomSourceCollection.create(serverLevel, VistaTypes.DECIDRHEUM_FOREST);
        this.stageDesignGenerator = new StageDesignGenerator(this, serverLevel, stageSeed, roomSources);
    }

    public static void sendRoomsToStorage(PlaceReadyRoomStorage prrs, StageDesignData designData) {
        prrs.addRooms(designData.getRoomList());
        prrs.addConnections(designData.getRoomGraph());
        prrs.enableEntranceSpawning();
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
            this.markDirty();
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
                if(room.isEntrance()) {
                    // TODO add some sort of entrance block/marker
                    //this.entrancePos = room.getPrefab().getBoundingBox().getCenter().getCenter();
                }
                placedRoom = true;

                RoomPlacer.spawnParticles(level, room.getPrefab());
                room.setNeighborsCanSpawn();

                // TODO serialization
                this.markDirty();
            }
        }

        if(placedRoom) {
            this.roomStorage.getRooms().removeIf(PlaceReadyRoom::isPlaced);
        }
    }

    public void markDirty() {
        this.markDirty(true);
    }

    public void markDirty(boolean value) {
        this.markDirty = value;
    }

    public boolean isDirty() {
        return this.markDirty;
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
}
