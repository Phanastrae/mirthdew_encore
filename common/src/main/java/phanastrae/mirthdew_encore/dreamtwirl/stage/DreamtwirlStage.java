package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
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
    public static boolean SEND_DEBUG_INFO = true;

    private final Level level;

    private final long id;
    private final RegionPos regionPos;
    private final long timestamp;

    private final StageAreaData stageAreaData;
    private final PlaceReadyRoomStorage roomStorage;

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
    }

    public static boolean isIdAllowed(long id) {
        return isIdAllowed(new RegionPos(id));
    }

    public static boolean isIdAllowed(RegionPos rp) {
        return ((rp.regionX & 0x1) == 0) && ((rp.regionZ & 0x1) == 0);
    }

    public CompoundTag writeNbt(CompoundTag nbt) {
        nbt.putLong("Id", this.getId());
        nbt.putLong("Timestamp", this.getTimestamp());
        return nbt;
    }

    public static DreamtwirlStage fromNbt(Level level, CompoundTag nbt) {
        long id = nbt.getLong("Id");
        long timestamp = nbt.getLong("Timestamp");
        return new DreamtwirlStage(level, id, timestamp);
    }

    public void generate(long stageSeed, ServerLevel serverLevel) {
        RoomSourceCollection roomSources = RoomSourceCollection.create(serverLevel, VistaTypes.DECIDRHEUM_FOREST);

        StageDesignGenerator stageDesignGenerator = new StageDesignGenerator(this, serverLevel, stageSeed, roomSources);
        stageDesignGenerator.generate();

        sendRoomsToStorage(this.getRoomStorage(), stageDesignGenerator.getDesignData());
        this.markDirty();

        if(SEND_DEBUG_INFO) {
            List<ServerPlayer> players = serverLevel.getPlayers(p -> true);
            if(!players.isEmpty()) {
                DreamtwirlDebug.DebugInfo debugInfo = DreamtwirlDebugPayload.createDebugInfo(stageDesignGenerator.getDesignData(), this.id);
                DreamtwirlDebugPayload payload = new DreamtwirlDebugPayload(debugInfo);
                for(ServerPlayer player : players) {
                    XPlatInterface.INSTANCE.sendPayload(player, payload);
                }
            }
        }
    }

    public static void sendRoomsToStorage(PlaceReadyRoomStorage prrs, StageDesignData designData) {
        prrs.addRooms(designData.getRoomList());
        prrs.addConnections(designData.getRoomGraph());
        prrs.enableEntranceSpawning();
    }

    public void tick(ServerLevel level) {
        // TODO tweak, optimise, etc.
        RandomSource random = level.getRandom();

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

                RoomPlacer.spawnParticles(level, room.getPrefab());
                room.setNeighborsCanSpawn();
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
}
