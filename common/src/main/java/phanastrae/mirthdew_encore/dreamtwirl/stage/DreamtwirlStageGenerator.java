package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DreamtwirlStageGenerator {
    private static final ResourceLocation ENTRANCE = MirthdewEncore.id("test/entrance");
    private static final ResourceLocation FOURWAY = MirthdewEncore.id("test/fourway");
    private static final ResourceLocation FOURWAY_MINI = MirthdewEncore.id("test/fourway/mini");
    private static final ResourceLocation FOURWAY_CROSSROAD = MirthdewEncore.id("test/fourway_crossroad");
    private static final ResourceLocation TOWER = MirthdewEncore.id("test/tower");
    private static final ResourceLocation BRIDGE = MirthdewEncore.id("test/bridge");
    private static final ResourceLocation LARGE_PATH = MirthdewEncore.id("test/large_path");
    private static final ResourceLocation TUFF_SPIRAL = MirthdewEncore.id("test/tuff_spiral");

    private static final ResourceLocation DECIDRHEUM_RING = MirthdewEncore.id("test/decidrheum_ring");
    private static final ResourceLocation DECIDRHEUM_FOURWAY = MirthdewEncore.id("test/decidrheum_fourway");
    private static final ResourceLocation DECIDRHEUM_CORNER = MirthdewEncore.id("test/decidrheum_corner");
    private static final ResourceLocation DECIDRHEUM_TWISTING_PATH = MirthdewEncore.id("test/decidrheum_twisting_path");
    private static final ResourceLocation CLINKERA_TWIRL_PATH = MirthdewEncore.id("test/clinkera_twirl_path");
    private static final ResourceLocation CLINKERA_VESPERBILE_FOUNTAIN = MirthdewEncore.id("test/clinkera_vesperbile_fountain");

    private final RoomStorage roomStorage;
    private final RegionPos regionPos;
    private final ServerLevel serverWorld;
    private final ChunkPos stageChunkCenter;
    private final StructureChunkMap chunkMap;
    private final BoundingBox areaBox;
    private final RandomSource random;

    private final List<DreamtwirlRoomGroup> roomGroups = new ArrayList<>();
    private final List<Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup>> edges = new ObjectArrayList<>();

    public DreamtwirlStageGenerator(RoomStorage roomStorage, ServerLevel serverWorld) {
        this.roomStorage = roomStorage;
        this.regionPos = roomStorage.getRegionPos();
        this.serverWorld = serverWorld;

        RegionPos regionPos = roomStorage.getRegionPos();
        BlockPos stageBlockCenter = new BlockPos(regionPos.getCenterX(), 128, regionPos.getCenterZ());
        this.stageChunkCenter = new ChunkPos(stageBlockCenter);

        this.chunkMap = this.createChunkMap(regionPos, serverWorld.getMinBuildHeight(), serverWorld.getHeight());

        this.areaBox = this.createAreaBox(regionPos);

        // TODO decide how seeds should work
        //long seed = serverWorld.getSeed() ^ regionPos.id;
        long seed = serverWorld.getRandom().nextLong();
        this.random = RandomSource.create(seed);
    }

    public StructureChunkMap createChunkMap(RegionPos regionPos, int minBuildHeight, int height) {
        ChunkPos minCornerPos = regionPos.getChunkPos(1, 1);
        return new StructureChunkMap(new Vec3i(minCornerPos.x, minBuildHeight >> 4, minCornerPos.z), 30, height >> 4, 30);
    }

    public BoundingBox createAreaBox(RegionPos regionPos) {
        BlockPos minPos = new BlockPos(regionPos.worldX + 16, serverWorld.getMinBuildHeight(), regionPos.worldZ + 16);
        BlockPos maxPos = new BlockPos(regionPos.worldX + 16 * 31 - 1, serverWorld.getMaxBuildHeight() - 1, regionPos.worldZ + 16 * 31 - 1);
        return new BoundingBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
    }

    public void addRoomGroup(DreamtwirlRoomGroup dreamtwirlRoomGroup) {
        this.roomGroups.add(dreamtwirlRoomGroup);
    }

    public static BlockPos blockPosFromV3(Vec3 v) {
        return new BlockPos(Mth.floor(v.x), Mth.floor(v.y), Mth.floor(v.z));
    }

    public void generate() {
        BlockPos regionMin = new BlockPos(this.regionPos.worldX, 0, this.regionPos.worldZ);

        BlockPos entranceBlockPos = regionMin.offset(
                128 + this.random.nextInt(25) - 12,
                24 + this.random.nextInt(15) - 7,
                256 + this.random.nextInt(129) - 64
        );
        BlockPos exitBlockPos = regionMin.offset(
                384 + this.random.nextInt(25) - 12,
                232 + this.random.nextInt(15) - 7,
                256 + this.random.nextInt(129) - 64
        );

        // add entrance and exit
        this.addCoreRoomOfType(ENTRANCE, entranceBlockPos).ifPresent(DreamtwirlRoom::setCanSpawn);
        this.addCoreRoomOfType(ENTRANCE, exitBlockPos);

        // connect room groups
        for(int i = 0; i + 1 < this.roomGroups.size(); i++) {
            this.edges.add(new Tuple<>(this.roomGroups.get(i), this.roomGroups.get(i + 1)));
        }

        // create connecting room groups
        int SPLIT_LAYERS = 4;
        for(int i = 0; i < SPLIT_LAYERS; i++) {
            for (Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup> edge : new ArrayList<>(this.edges)) {
                this.joinEdge(edge);
            }
        }

        // place rooms
        for(int i = 0; i < 25000; i++) {
            if (!this.roomGroups.isEmpty()) {
                DreamtwirlRoomGroup roomGroup = this.roomGroups.get(random.nextInt(this.roomGroups.size()));
                this.sprawl(roomGroup, this.random);
            }
        }

        /*
        for(Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup> edge : this.edges) {
            BlockPos posA = edge.getA().start;
            BlockPos posB = edge.getB().start;

            Vec3 difference = posB.getCenter().subtract(posA.getCenter());
            int N = 20;
            for(int i = 1; i < N; i++) {
                Vec3 target = posA.getCenter().add(difference.scale(i / (float)N));
                BlockPos targetBP = new BlockPos((int)Math.floor(target.x), (int)Math.floor(target.y), (int)Math.floor(target.z));

                Optional<DreamtwirlRoom> roomAoptional = edge.getA().getClosestRoomToPos(targetBP);
                Optional<DreamtwirlRoom> roomBoptional = edge.getB().getClosestRoomToPos(targetBP);
                if(roomAoptional.isPresent() && roomBoptional.isPresent()) {
                    DreamtwirlRoom roomA = roomAoptional.get();
                    DreamtwirlRoom roomB = roomBoptional.get();

                    Optional<DreamtwirlRoom> roomOptional = getRoomOfType(FOURWAY_MINI);
                    if(roomOptional.isPresent()) {
                        DreamtwirlRoom room = roomOptional.get();
                        room.centerAt(targetBP);
                        if(isLocationValid(room)) {
                            roomA.connectTo(room);
                            room.connectTo(roomA);
                            roomB.connectTo(room);
                            room.connectTo(roomB);
                            if (i <= 10) {
                                edge.getA().addRoom(room, this.chunkMap);
                            } else {
                                edge.getB().addRoom(room, this.chunkMap);
                            }
                        }
                    }
                }
            }
        }

         */
    }

    public Optional<DreamtwirlRoom> getRoomOfType(ResourceLocation identifier) {
        Optional<StructureData> structureDataOptional = StructureData.makeStructureData(identifier, serverWorld, random, stageChunkCenter);
        return structureDataOptional.map(structureData -> DreamtwirlRoom.createRoom(structureData, this.serverWorld.getStructureManager(), this.random));
    }

    public Optional<DreamtwirlRoom> addCoreRoomOfType(ResourceLocation roomId, BlockPos roomCenter) {
        Optional<DreamtwirlRoom> roomOptional = getRoomOfType(roomId);
        if(roomOptional.isPresent()) {
            DreamtwirlRoom room = roomOptional.get();

            room.centerAt(roomCenter);
            this.adjustPosition(room, null);

            if(this.isLocationValid(room)) {
                DreamtwirlRoomGroup roomGroup = new DreamtwirlRoomGroup(roomCenter);
                roomGroup.addRoom(room, this.chunkMap);

                this.addRoomGroup(roomGroup);

                return Optional.of(room);
            }
        }

        return Optional.empty();
    }

    public void joinEdge(Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup> pair) {
        Optional<BlockPos> targetOptional = getTargetPosition(pair.getA(), pair.getB());
        if(targetOptional.isEmpty()) {
            return;
        }
        BlockPos target = targetOptional.get();

        Optional<DreamtwirlRoom> newRoomOptional = getBigRoom();
        if (newRoomOptional.isPresent()) {
            DreamtwirlRoom room = newRoomOptional.get();
            room.centerAt(target);
            adjustPosition(room, null); // TODO try to adjust position to be between the two target points instead?

            if (this.isLocationValid(room)) {
                DreamtwirlRoomGroup roomGroup = new DreamtwirlRoomGroup(room.getBoundingBox().getCenter());
                roomGroup.addRoom(room, this.chunkMap);
                this.addRoomGroup(roomGroup);

                this.edges.remove(pair);
                this.edges.add(new Tuple<>(pair.getA(), roomGroup));
                this.edges.add(new Tuple<>(pair.getB(), roomGroup));
            }
        }
    }

    public void sprawl(DreamtwirlRoomGroup roomGroup, RandomSource random) {
        // get random room from group
        List<DreamtwirlRoom> rgRooms = roomGroup.getRooms();
        if(rgRooms.isEmpty()) return;
        DreamtwirlRoom room = rgRooms.get(random.nextInt(rgRooms.size()));

        // get random gate from room
        Optional<RoomGate> gateOptional = room.getRandomEmptyGate(random);
        if(gateOptional.isEmpty()) return;
        RoomGate gate = gateOptional.get();

        // create new room
        //Optional<DreamtwirlRoom> fourwayOptional = this.getRoomOfType(FOURWAY_MINI);
        Optional<DreamtwirlRoom> fourwayOptional = random.nextInt(3) != 0 ? this.getBigRoom() : this.getRoomOfType(DECIDRHEUM_CORNER);
        if(fourwayOptional.isEmpty()) return;
        DreamtwirlRoom fourway = fourwayOptional.get();

        // get corresponding gate from new room
        Optional<RoomGate> fourwayGateOptional = fourway.getRandomEmptyGateMatching(random, gate.getOrientation());
        if(fourwayGateOptional.isEmpty()) return;
        RoomGate fourwayGate = fourwayGateOptional.get();

        // match gates
        fourway.translateToMatchGate(fourwayGate, gate);

        Direction direction = gate.getOrientation().front();
        Vec3i vector = direction.getNormal();
        fourway.translate(vector.multiply(2).offset(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1));
        //this.adjustPosition(fourway, direction);

        // add room if valid
        if(this.isLocationValid(fourway)) {
            DreamtwirlRoom.connectRooms(fourway, room, fourwayGate, gate);
            roomGroup.addRoom(fourway, this.chunkMap);
        }
    }

    public Optional<DreamtwirlRoom> getBigRoom() {
        ResourceLocation[] paths = new ResourceLocation[]{
                DECIDRHEUM_CORNER,
                DECIDRHEUM_TWISTING_PATH,
                DECIDRHEUM_FOURWAY,
                CLINKERA_TWIRL_PATH
        };

        ResourceLocation[] rooms = new ResourceLocation[]{
                DECIDRHEUM_RING,
                CLINKERA_VESPERBILE_FOUNTAIN
        };

        ResourceLocation id = random.nextInt(4) == 0
                ? rooms[random.nextInt(2)]
                : paths[random.nextInt(4)];

        return getRoomOfType(id);
    }

    public Optional<BlockPos> getTargetPosition(DreamtwirlRoomGroup groupA, DreamtwirlRoomGroup groupB) {
        Optional<DreamtwirlRoom> closestAtoB = groupA.getClosestRoomToPos(groupB.start);
        Optional<DreamtwirlRoom> closestBtoA = groupA.getClosestRoomToPos(groupA.start);

        if (closestAtoB.isEmpty() || closestBtoA.isEmpty()) {
            return Optional.empty();
        }

        BlockPos targetA = closestAtoB.get().getBoundingBox().getCenter();
        BlockPos targetB = closestBtoA.get().getBoundingBox().getCenter();
        Vec3i difference = targetB.subtract(targetA);

        int bx = Mth.abs(difference.getX()) / 4;
        int by = Mth.abs(difference.getY()) / 4;
        int bz = Mth.abs(difference.getZ()) / 4;

        int rx = random.nextInt(bx * 2 + 1) - bx;
        int ry = random.nextInt(by * 2 + 1) - by;
        int rz = random.nextInt(bz * 2 + 1) - bz;

        targetA = targetA.offset(rx, ry, rz);
        targetB = targetB.offset(rx, ry, rz);

        Optional<DreamtwirlRoom> closestAtoBclosest = groupA.getClosestRoomToPos(targetB);
        Optional<DreamtwirlRoom> closestBtoAclosest = groupB.getClosestRoomToPos(targetA);

        if (closestAtoBclosest.isEmpty() || closestBtoAclosest.isEmpty()) {
            return Optional.empty();
        }

        BlockPos p1 = closestAtoBclosest.get().getBoundingBox().getCenter();
        BlockPos p2 = closestBtoAclosest.get().getBoundingBox().getCenter();

        Vec3i sum = p1.offset(p2);
        BlockPos center = new BlockPos(sum.getX() / 2, sum.getY() / 2, sum.getZ() / 2);

        // want the cross product between center -> endpoint vector and a unit up vector
        Vec3i centerToEndpoint = p2.subtract(center);
        Vec3i unitUp = new Vec3i(0, 1, 0);
        Vec3i cross = centerToEndpoint.cross(unitUp);
        if(random.nextBoolean()) { // randomly flip direction
            cross = cross.multiply(-1);
        }

        return Optional.of(center.offset(cross));
    }

    public void transferRoomGroups() {
        // connect room groups together
        for(Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup> edge : edges) {
            DreamtwirlRoomGroup groupA = edge.getA();
            DreamtwirlRoomGroup groupB = edge.getB();

            Optional<DreamtwirlRoom> closestAtoB = groupA.getClosestRoomToPos(groupB.start);
            if(closestAtoB.isPresent()) {
                Optional<DreamtwirlRoom> closestBtoClosestAtoB = groupB.getClosestRoomToPos(closestAtoB.get().getBoundingBox().getCenter());
                if(closestBtoClosestAtoB.isPresent()) {
                    DreamtwirlRoom room1 = closestAtoB.get();
                    DreamtwirlRoom room2 = closestBtoClosestAtoB.get();

                    room1.connectTo(room2);
                    room2.connectTo(room1);
                }
            }
        }
        // send room groups to the room storage
        this.roomStorage.addRooms(this.roomGroups);
        this.roomGroups.clear();
    }

    public boolean isLocationValid(DreamtwirlRoom room) {
        List<DreamtwirlRoom> collisionRooms = this.chunkMap.getIntersections(room.getBoundingBox(), room);
        BoundingBox boundingBox = room.getBoundingBox();

        if(boundingBox.minX() < this.areaBox.minX()
                || boundingBox.minY() < this.areaBox.minY()
                || boundingBox.minZ() < this.areaBox.minZ()
                || boundingBox.maxX() > this.areaBox.maxX()
                || boundingBox.maxY() > this.areaBox.maxY()
                || boundingBox.maxZ() > this.areaBox.maxZ()) {
            return false;
        }

        for(DreamtwirlRoom collisionRoom : collisionRooms) {
            BoundingBox collisionBox = collisionRoom.getBoundingBox();
            if (boundingBox.intersects(collisionBox)) {
                return false;
            }
        }

        return true;
    }

    public void adjustPosition(DreamtwirlRoom room, @Nullable Direction preferredOffsetDirection) {
        Vec3i vector = preferredOffsetDirection == null ? null : preferredOffsetDirection.getNormal();
        for(int n = 0; n < 8; n++) {
            List<DreamtwirlRoom> collisionRooms = this.chunkMap.getIntersections(room.getBoundingBox(), room);

            boolean hadCollision = false;
            for(DreamtwirlRoom collisionRoom : collisionRooms) {
                BoundingBox collisionBox = collisionRoom.getBoundingBox();

                for(int k = 0; k < 10; k++) {
                    if (room.getBoundingBox().intersects(collisionBox)) {
                        hadCollision = true;

                        int d = k * k;
                        BlockPos moveBy = new BlockPos(
                                random.nextInt(2 * d + 1) - d,
                                random.nextInt(2 * k + 1) - k,
                                random.nextInt(2 * d + 1) - d
                        );
                        if(vector != null) {
                            moveBy = moveBy.offset(vector.multiply(d));
                        }

                        room.translate(moveBy);
                    } else {
                        break;
                    }
                }
            }
            if(!hadCollision) {
                break;
            }
        }
    }
}
