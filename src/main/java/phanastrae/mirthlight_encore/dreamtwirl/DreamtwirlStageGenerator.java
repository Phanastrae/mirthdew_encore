package phanastrae.mirthlight_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.structure.Structure;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthlight_encore.MirthlightEncore;
import phanastrae.mirthlight_encore.util.RegionPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DreamtwirlStageGenerator {

    private final DreamtwirlStage dreamtwirlStage;
    private final ServerWorld serverWorld;
    private final BlockPos stageBlockCenter;
    private final ChunkPos stageChunkCenter;
    private final ChunkMap chunkMap;
    private final BlockBox areaBox;

    //private final List<DreamtwirlRoom> rooms = new ArrayList<>();
    private final List<DreamtwirlRoomGroup> roomGroups = new ArrayList<>();

    public DreamtwirlStageGenerator(DreamtwirlStage dreamtwirlStage, ServerWorld serverWorld) {
        this.dreamtwirlStage = dreamtwirlStage;
        this.serverWorld = serverWorld;

        RegionPos regionPos = this.dreamtwirlStage.getRegionPos();
        this.stageBlockCenter = new BlockPos(regionPos.getCenterX(), 128, regionPos.getCenterZ());
        this.stageChunkCenter = new ChunkPos(stageBlockCenter);

        ChunkPos minCornerPos = regionPos.getChunkPos(1, 1);
        this.chunkMap = new ChunkMap(new Vec3i(minCornerPos.x, serverWorld.getBottomY() >> 4, minCornerPos.z), 30, serverWorld.getHeight() >> 4, 30);

        BlockPos minPos = new BlockPos(regionPos.worldX + 16, serverWorld.getBottomY(), regionPos.worldZ + 16);
        BlockPos maxPos = new BlockPos(regionPos.worldX + 16 * 31 - 1, serverWorld.getTopY() - 1, regionPos.worldX + 16 * 31 - 1);
        this.areaBox = new BlockBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
    }

    public ChunkMap getChunkMap() {
        return this.chunkMap;
    }

    public void generate() {
        RegionPos region = this.dreamtwirlStage.getRegionPos();
        BlockPos regionMin = new BlockPos(region.worldX, 0, region.worldZ);
        Random random = serverWorld.getRandom();

        BlockPos entrancePos = regionMin.add(128, 32, 256).add(random.nextInt(25) - 12, random.nextInt(15) - 7, random.nextInt(129) - 64);
        BlockPos exitPos = regionMin.add(384, 192, 256).add(random.nextInt(25) - 12, random.nextInt(15) - 7, random.nextInt(129) - 64);

        Identifier entranceId = MirthlightEncore.id("test/entrance");
        Identifier fourwayId = MirthlightEncore.id("test/fourway");
        Identifier towerId = MirthlightEncore.id("test/tower");

        Optional<DreamtwirlRoom> entranceOptional = getRoomOfType(entranceId);
        if(entranceOptional.isPresent()) {
            DreamtwirlRoom entrance = entranceOptional.get();

            entrance.centerAt(entrancePos);

            DreamtwirlRoomGroup entranceRoomGroup = new DreamtwirlRoomGroup(entrancePos);
            entranceRoomGroup.addRoom(entrance, this.chunkMap);
            this.addRoomGroup(entranceRoomGroup);
        }
        Optional<DreamtwirlRoom> exitOptional = getRoomOfType(entranceId);
        if(exitOptional.isPresent()) {
            DreamtwirlRoom exit = exitOptional.get();

            exit.centerAt(exitPos);

            DreamtwirlRoomGroup exitRoomGroup = new DreamtwirlRoomGroup(exitPos);
            exitRoomGroup.addRoom(exit, this.chunkMap);
            this.addRoomGroup(exitRoomGroup);
        }

        List<Pair<DreamtwirlRoomGroup, DreamtwirlRoomGroup>> edges = new ObjectArrayList<>();
        if(this.roomGroups.size() == 2) {
            edges.add(new Pair<>(this.roomGroups.get(0), this.roomGroups.get(1)));
        }

        for(int n = 0; n < 8; n++) {
            List<Pair<DreamtwirlRoomGroup, DreamtwirlRoomGroup>> edgesCopy = new ObjectArrayList<>();
            edgesCopy.addAll(edges);

            for (Pair<DreamtwirlRoomGroup, DreamtwirlRoomGroup> pair : edgesCopy) {
                DreamtwirlRoomGroup group1 = pair.getLeft();
                DreamtwirlRoomGroup group2 = pair.getRight();

                Optional<DreamtwirlRoom> closest1To2 = group1.getClosestRoomToPos(group2.center);
                Optional<DreamtwirlRoom> closest2To1 = group1.getClosestRoomToPos(group1.center);

                if (closest1To2.isEmpty() || closest2To1.isEmpty()) {
                    continue;
                }

                BlockPos target1 = closest1To2.get().getBoundingBox().getCenter();
                BlockPos target2 = closest2To1.get().getBoundingBox().getCenter();
                Vec3i difference = target2.subtract(target1);
                int bx = MathHelper.abs(difference.getX()) / 4;
                int by = MathHelper.abs(difference.getY()) / 4;
                int bz = MathHelper.abs(difference.getZ()) / 4;

                int rx = random.nextInt(bx * 2 + 1) - bx;
                int ry = random.nextInt(by * 2 + 1) - by;
                int rz = random.nextInt(bz * 2 + 1) - bz;

                target1 = target1.add(rx, ry, rz);
                target2 = target2.add(rx, ry, rz);

                Optional<DreamtwirlRoom> closest1To2closest = group1.getClosestRoomToPos(target2);
                Optional<DreamtwirlRoom> closest2To1closest = group2.getClosestRoomToPos(target1);

                if (closest1To2closest.isEmpty() || closest2To1closest.isEmpty()) {
                    continue;
                }

                BlockPos p1 = closest1To2closest.get().getBoundingBox().getCenter();
                BlockPos p2 = closest2To1closest.get().getBoundingBox().getCenter();

                Vec3i sum = p1.add(p2);
                BlockPos center = new BlockPos(sum.getX() / 2, sum.getY() / 2, sum.getZ() / 2);

                // want the cross product between center -> endpoint vector and a unit up vector
                Vec3i centerToEndpoint = p2.subtract(center);
                Vec3i unitUp = new Vec3i(0, 1, 0);
                Vec3i cross = centerToEndpoint.crossProduct(unitUp);
                if(random.nextBoolean()) { // randomly flip direction
                    cross = cross.multiply(-1);
                }
                BlockPos target = center.add(cross);

                Identifier id = random.nextInt(8) == 0 ? towerId : fourwayId;
                Optional<DreamtwirlRoom> newRoomOptional = getRoomOfType(id);
                if (newRoomOptional.isPresent()) {
                    DreamtwirlRoom room = newRoomOptional.get();
                    room.centerAt(target);
                    adjustPosition(room, random, null);

                    if (this.isLocationValid(room)) {
                        DreamtwirlRoomGroup roomGroup = new DreamtwirlRoomGroup(room.getBoundingBox().getCenter());
                        roomGroup.addRoom(room, this.chunkMap);
                        this.addRoomGroup(roomGroup);

                        edges.remove(pair);
                        edges.add(new Pair<>(group1, roomGroup));
                        edges.add(new Pair<>(group2, roomGroup));
                    }
                }
            }

            for (DreamtwirlRoomGroup roomGroup : this.roomGroups) {
                roomGroup.sprawl(this, random);
            }
        }

        this.placeRooms();
    }

    public boolean isLocationValid(DreamtwirlRoom room) {
        List<DreamtwirlRoom> collisionRooms = this.chunkMap.getIntersections(room.getBoundingBox(), room);
        BlockBox boundingBox = room.getBoundingBox();

        if(boundingBox.getMinX() < this.areaBox.getMinX()
                || boundingBox.getMinY() < this.areaBox.getMinY()
                || boundingBox.getMinZ() < this.areaBox.getMinZ()
                || boundingBox.getMaxX() > this.areaBox.getMaxX()
                || boundingBox.getMaxY() > this.areaBox.getMaxY()
                || boundingBox.getMaxZ() > this.areaBox.getMaxZ()) {
            return false;
        }

        for(DreamtwirlRoom collisionRoom : collisionRooms) {
            BlockBox collisionBox = collisionRoom.getBoundingBox();
            if (boundingBox.intersects(collisionBox)) {
                return false;
            }
        }

        return true;
    }

    public void adjustPosition(DreamtwirlRoom room, Random random, @Nullable Direction preferredOffsetDirection) {
        Vec3i vector = preferredOffsetDirection == null ? null : preferredOffsetDirection.getVector();
        for(int n = 0; n < 8; n++) {
            List<DreamtwirlRoom> collisionRooms = this.chunkMap.getIntersections(room.getBoundingBox(), room);

            boolean hadCollision = false;
            for(DreamtwirlRoom collisionRoom : collisionRooms) {
                BlockBox collisionBox = collisionRoom.getBoundingBox();

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
                            moveBy = moveBy.add(vector.multiply(d));
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

    public void addRoomGroup(DreamtwirlRoomGroup dreamtwirlRoomGroup) {
        this.roomGroups.add(dreamtwirlRoomGroup);
    }

    public Optional<DreamtwirlRoom> getRoomOfType(Identifier identifier) {
        Optional<StructureData> structureDataOptional = makeStructureData(identifier);
        if (structureDataOptional.isEmpty()) {
            return Optional.empty();
        } else {
            DreamtwirlRoom dreamtwirlRoom = new DreamtwirlRoom(structureDataOptional.get());
            dreamtwirlRoom.collectGates(this.serverWorld);
            return Optional.of(dreamtwirlRoom);
        }
    }

    public Optional<StructureData> makeStructureData(Identifier identifier) {
        Optional<Structure> structureOptional = getStructure(identifier);
        if(structureOptional.isEmpty()) {
            return Optional.empty();
        }
        Structure structure = structureOptional.get();

        Optional<Structure.StructurePosition> optional = getStructurePosition(stageChunkCenter, structure);
        if(optional.isEmpty()) {
            return Optional.empty();
        }
        StructurePiecesCollector structurePiecesCollector = optional.get().generate();
        StructurePiecesList structurePiecesList = structurePiecesCollector.toList();

        return Optional.of(new StructureData(structure, structurePiecesList, this.stageBlockCenter));
    }

    public void placeRooms() {
        for(DreamtwirlRoomGroup roomGroup : this.roomGroups) {
            this.placeRoomGroup(roomGroup);
        }
    }

    public void placeRoomGroup(DreamtwirlRoomGroup roomGroup) {
        for(DreamtwirlRoom room : roomGroup.getRooms()) {
            this.placeRoom(room);
        }
    }

    public void placeRoom(DreamtwirlRoom room) {
        this.placeStructure(room.structureData);
    }

    public void placeStructure(StructureData structureData) {
        Structure structure = structureData.structure;
        StructurePiecesList structurePiecesList = structureData.structurePiecesList;

        StructureStart structureStart = new StructureStart(structure, this.stageChunkCenter, 0, structurePiecesList);
        if(!structureStart.hasChildren()) {
            return;
        }

        BlockBox blockBox = structureStart.getBoundingBox();
        ChunkPos chunkPosStart = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMinX()), ChunkSectionPos.getSectionCoord(blockBox.getMinZ()));
        ChunkPos chunkPosEnd = new ChunkPos(ChunkSectionPos.getSectionCoord(blockBox.getMaxX()), ChunkSectionPos.getSectionCoord(blockBox.getMaxZ()));
        if (ChunkPos.stream(chunkPosStart, chunkPosEnd).anyMatch(p -> !serverWorld.canSetBlock(p.getStartPos()))) {
            // return; // TODO handle this
        }

        List<StructurePiece> list = structurePiecesList.pieces();
        if(list.isEmpty()) {
            return;
        }

        BlockBox firstPieceBB = list.getFirst().getBoundingBox();

        BlockPos firstPieceCenter = firstPieceBB.getCenter();
        BlockPos firstPieceBasePosition = new BlockPos(firstPieceCenter.getX(), firstPieceBB.getMinY(), firstPieceCenter.getZ());
        ChunkGenerator chunkGenerator = this.serverWorld.getChunkManager().getChunkGenerator();
        StructureAccessor structureAccessor = this.serverWorld.getStructureAccessor();
        Random random = this.serverWorld.getRandom();


        ChunkPos.stream(chunkPosStart, chunkPosEnd)
                .forEach(
                        chunkPosx -> {
                            if(!this.areaBox.contains(chunkPosx.getBlockPos(0, 0, 0))) {
                                return;
                            }
                            if(!serverWorld.canSetBlock(chunkPosx.getStartPos())) return; // TODO handle this

                            BlockBox chunkBox = new BlockBox(chunkPosx.getStartX(), serverWorld.getBottomY(), chunkPosx.getStartZ(), chunkPosx.getEndX(), serverWorld.getTopY(), chunkPosx.getEndZ());
                            for(StructurePiece structurePiece : list) {
                                if (structurePiece.getBoundingBox().intersects(chunkBox)) {
                                    if(structurePiece instanceof PoolStructurePiece poolStructurePiece) {
                                        this.generate(poolStructurePiece, serverWorld.getStructureTemplateManager(), serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, firstPieceBasePosition);
                                    } else {
                                        structurePiece.generate(serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, firstPieceBasePosition);
                                    }
                                }
                            }
                            structure.postPlace(serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, structurePiecesList);
                        }
                );
    }

    public void generate(PoolStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, ServerWorld world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        StructurePoolElement poolElement = structurePiece.getPoolElement();
        if(poolElement.generate(structureTemplateManager, world, structureAccessor, chunkGenerator, structurePiece.getPos(), pivot, structurePiece.getRotation(), chunkBox, random, StructureLiquidSettings.IGNORE_WATERLOGGING, false)) {
            poolElement.getStructureBlockInfos(structureTemplateManager, structurePiece.getPos(), structurePiece.getRotation(), random);
            List<StructureTemplate.StructureBlockInfo> gateInfos =  DreamtwirlRoom.getGates(poolElement, structureTemplateManager, structurePiece.getPos(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo gateInfo : gateInfos) {
                // TODO handle properly
                // TODO get gates from the room directly?
                world.setBlockState(gateInfo.pos(), Blocks.AIR.getDefaultState());
            }
        }
    }

    public Optional<Structure> getStructure(Identifier identifier) {
        RegistryKey<Structure> registryKey = RegistryKey.of(RegistryKeys.STRUCTURE, identifier);

        Registry<Structure> structureRegistry = serverWorld.getRegistryManager().get(RegistryKeys.STRUCTURE);
        return structureRegistry.getOrEmpty(registryKey);
    }

    public Optional<Structure.StructurePosition> getStructurePosition(ChunkPos chunkPos, Structure structure) {
        long seed = serverWorld.getSeed() ^ serverWorld.getRandom().nextLong(); // TODO make this more consistent?

        ChunkGenerator chunkGenerator = serverWorld.getChunkManager().getChunkGenerator();
        Structure.Context context = new Structure.Context(
                serverWorld.getRegistryManager(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverWorld.getChunkManager().getNoiseConfig(),
                serverWorld.getStructureTemplateManager(),
                seed,
                chunkPos,
                serverWorld,
                biome -> true
        );
        return structure.getValidStructurePosition(context);
    }

    public static class StructureData {
        public final Structure structure;
        public final StructurePiecesList structurePiecesList;
        public BlockPos startPos;
        public StructureData(Structure structure, StructurePiecesList structurePiecesList, BlockPos startPos) {
            this.structure = structure;
            this.structurePiecesList = structurePiecesList;
            this.startPos = startPos;
        }

        public void translate(int x, int y, int z) {
            this.startPos = this.startPos.add(x, y, z);
            this.structurePiecesList.pieces().forEach(structurePiece -> {
                structurePiece.translate(x, y, z);
            });
        }
    }
}
