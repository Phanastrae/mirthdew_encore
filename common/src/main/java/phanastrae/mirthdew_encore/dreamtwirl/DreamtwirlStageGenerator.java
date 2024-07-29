package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.util.RegionPos;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DreamtwirlStageGenerator {

    private final DreamtwirlStage dreamtwirlStage;
    private final ServerLevel serverWorld;
    private final BlockPos stageBlockCenter;
    private final ChunkPos stageChunkCenter;
    private final ChunkMap chunkMap;
    private final BoundingBox areaBox;

    //private final List<DreamtwirlRoom> rooms = new ArrayList<>();
    private final List<DreamtwirlRoomGroup> roomGroups = new ArrayList<>();

    public DreamtwirlStageGenerator(DreamtwirlStage dreamtwirlStage, ServerLevel serverWorld) {
        this.dreamtwirlStage = dreamtwirlStage;
        this.serverWorld = serverWorld;

        RegionPos regionPos = this.dreamtwirlStage.getRegionPos();
        this.stageBlockCenter = new BlockPos(regionPos.getCenterX(), 128, regionPos.getCenterZ());
        this.stageChunkCenter = new ChunkPos(stageBlockCenter);

        ChunkPos minCornerPos = regionPos.getChunkPos(1, 1);
        this.chunkMap = new ChunkMap(new Vec3i(minCornerPos.x, serverWorld.getMinBuildHeight() >> 4, minCornerPos.z), 30, serverWorld.getHeight() >> 4, 30);

        BlockPos minPos = new BlockPos(regionPos.worldX + 16, serverWorld.getMinBuildHeight(), regionPos.worldZ + 16);
        BlockPos maxPos = new BlockPos(regionPos.worldX + 16 * 31 - 1, serverWorld.getMaxBuildHeight() - 1, regionPos.worldZ + 16 * 31 - 1);
        this.areaBox = new BoundingBox(minPos.getX(), minPos.getY(), minPos.getZ(), maxPos.getX(), maxPos.getY(), maxPos.getZ());
    }

    public ChunkMap getChunkMap() {
        return this.chunkMap;
    }

    public void generate() {
        RegionPos region = this.dreamtwirlStage.getRegionPos();
        BlockPos regionMin = new BlockPos(region.worldX, 0, region.worldZ);
        RandomSource random = serverWorld.getRandom();

        BlockPos entrancePos = regionMin.offset(128, 32, 256).offset(random.nextInt(25) - 12, random.nextInt(15) - 7, random.nextInt(129) - 64);
        BlockPos exitPos = regionMin.offset(384, 192, 256).offset(random.nextInt(25) - 12, random.nextInt(15) - 7, random.nextInt(129) - 64);

        ResourceLocation entranceId = MirthdewEncore.id("test/entrance");
        ResourceLocation fourwayId = MirthdewEncore.id("test/fourway");
        ResourceLocation towerId = MirthdewEncore.id("test/tower");

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

        List<Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup>> edges = new ObjectArrayList<>();
        if(this.roomGroups.size() == 2) {
            edges.add(new Tuple<>(this.roomGroups.get(0), this.roomGroups.get(1)));
        }

        for(int n = 0; n < 8; n++) {
            List<Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup>> edgesCopy = new ObjectArrayList<>();
            edgesCopy.addAll(edges);

            for (Tuple<DreamtwirlRoomGroup, DreamtwirlRoomGroup> pair : edgesCopy) {
                DreamtwirlRoomGroup group1 = pair.getA();
                DreamtwirlRoomGroup group2 = pair.getB();

                Optional<DreamtwirlRoom> closest1To2 = group1.getClosestRoomToPos(group2.center);
                Optional<DreamtwirlRoom> closest2To1 = group1.getClosestRoomToPos(group1.center);

                if (closest1To2.isEmpty() || closest2To1.isEmpty()) {
                    continue;
                }

                BlockPos target1 = closest1To2.get().getBoundingBox().getCenter();
                BlockPos target2 = closest2To1.get().getBoundingBox().getCenter();
                Vec3i difference = target2.subtract(target1);
                int bx = Mth.abs(difference.getX()) / 4;
                int by = Mth.abs(difference.getY()) / 4;
                int bz = Mth.abs(difference.getZ()) / 4;

                int rx = random.nextInt(bx * 2 + 1) - bx;
                int ry = random.nextInt(by * 2 + 1) - by;
                int rz = random.nextInt(bz * 2 + 1) - bz;

                target1 = target1.offset(rx, ry, rz);
                target2 = target2.offset(rx, ry, rz);

                Optional<DreamtwirlRoom> closest1To2closest = group1.getClosestRoomToPos(target2);
                Optional<DreamtwirlRoom> closest2To1closest = group2.getClosestRoomToPos(target1);

                if (closest1To2closest.isEmpty() || closest2To1closest.isEmpty()) {
                    continue;
                }

                BlockPos p1 = closest1To2closest.get().getBoundingBox().getCenter();
                BlockPos p2 = closest2To1closest.get().getBoundingBox().getCenter();

                Vec3i sum = p1.offset(p2);
                BlockPos center = new BlockPos(sum.getX() / 2, sum.getY() / 2, sum.getZ() / 2);

                // want the cross product between center -> endpoint vector and a unit up vector
                Vec3i centerToEndpoint = p2.subtract(center);
                Vec3i unitUp = new Vec3i(0, 1, 0);
                Vec3i cross = centerToEndpoint.cross(unitUp);
                if(random.nextBoolean()) { // randomly flip direction
                    cross = cross.multiply(-1);
                }
                BlockPos target = center.offset(cross);

                ResourceLocation id = random.nextInt(8) == 0 ? towerId : fourwayId;
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
                        edges.add(new Tuple<>(group1, roomGroup));
                        edges.add(new Tuple<>(group2, roomGroup));
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

    public void adjustPosition(DreamtwirlRoom room, RandomSource random, @Nullable Direction preferredOffsetDirection) {
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

    public void addRoomGroup(DreamtwirlRoomGroup dreamtwirlRoomGroup) {
        this.roomGroups.add(dreamtwirlRoomGroup);
    }

    public Optional<DreamtwirlRoom> getRoomOfType(ResourceLocation identifier) {
        Optional<StructureData> structureDataOptional = makeStructureData(identifier);
        if (structureDataOptional.isEmpty()) {
            return Optional.empty();
        } else {
            DreamtwirlRoom dreamtwirlRoom = new DreamtwirlRoom(structureDataOptional.get());
            dreamtwirlRoom.collectGates(this.serverWorld);
            return Optional.of(dreamtwirlRoom);
        }
    }

    public Optional<StructureData> makeStructureData(ResourceLocation identifier) {
        Optional<Structure> structureOptional = getStructure(identifier);
        if(structureOptional.isEmpty()) {
            return Optional.empty();
        }
        Structure structure = structureOptional.get();

        Optional<Structure.GenerationStub> optional = getStructurePosition(stageChunkCenter, structure);
        if(optional.isEmpty()) {
            return Optional.empty();
        }
        StructurePiecesBuilder structurePiecesCollector = optional.get().getPiecesBuilder();
        PiecesContainer structurePiecesList = structurePiecesCollector.build();

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
        PiecesContainer structurePiecesList = structureData.structurePiecesList;

        StructureStart structureStart = new StructureStart(structure, this.stageChunkCenter, 0, structurePiecesList);
        if(!structureStart.isValid()) {
            return;
        }

        BoundingBox blockBox = structureStart.getBoundingBox();
        ChunkPos chunkPosStart = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.minX()), SectionPos.blockToSectionCoord(blockBox.minZ()));
        ChunkPos chunkPosEnd = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.maxX()), SectionPos.blockToSectionCoord(blockBox.maxZ()));
        if (ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd).anyMatch(p -> !serverWorld.isLoaded(p.getWorldPosition()))) {
            // return; // TODO handle this
        }

        List<StructurePiece> list = structurePiecesList.pieces();
        if(list.isEmpty()) {
            return;
        }

        BoundingBox firstPieceBB = list.getFirst().getBoundingBox();

        BlockPos firstPieceCenter = firstPieceBB.getCenter();
        BlockPos firstPieceBasePosition = new BlockPos(firstPieceCenter.getX(), firstPieceBB.minY(), firstPieceCenter.getZ());
        ChunkGenerator chunkGenerator = this.serverWorld.getChunkSource().getGenerator();
        StructureManager structureAccessor = this.serverWorld.structureManager();
        RandomSource random = this.serverWorld.getRandom();


        ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd)
                .forEach(
                        chunkPosx -> {
                            if(!this.areaBox.isInside(chunkPosx.getBlockAt(0, 0, 0))) {
                                return;
                            }
                            if(!serverWorld.isLoaded(chunkPosx.getWorldPosition())) return; // TODO handle this

                            BoundingBox chunkBox = new BoundingBox(chunkPosx.getMinBlockX(), serverWorld.getMinBuildHeight(), chunkPosx.getMinBlockZ(), chunkPosx.getMaxBlockX(), serverWorld.getMaxBuildHeight(), chunkPosx.getMaxBlockZ());
                            for(StructurePiece structurePiece : list) {
                                if (structurePiece.getBoundingBox().intersects(chunkBox)) {
                                    if(structurePiece instanceof PoolElementStructurePiece poolStructurePiece) {
                                        this.generate(poolStructurePiece, serverWorld.getStructureManager(), serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, firstPieceBasePosition);
                                    } else {
                                        structurePiece.postProcess(serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, firstPieceBasePosition);
                                    }
                                }
                            }
                            structure.afterPlace(serverWorld, structureAccessor, chunkGenerator, random, chunkBox, chunkPosx, structurePiecesList);
                        }
                );
    }

    public void generate(PoolElementStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, ServerLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        StructurePoolElement poolElement = structurePiece.getElement();
        if(poolElement.place(structureTemplateManager, world, structureAccessor, chunkGenerator, structurePiece.getPosition(), pivot, structurePiece.getRotation(), chunkBox, random, LiquidSettings.IGNORE_WATERLOGGING, false)) {
            poolElement.getShuffledJigsawBlocks(structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            List<StructureTemplate.StructureBlockInfo> gateInfos =  DreamtwirlRoom.getGates(poolElement, structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo gateInfo : gateInfos) {
                // TODO handle properly
                // TODO get gates from the room directly?
                world.setBlockAndUpdate(gateInfo.pos(), Blocks.AIR.defaultBlockState());
            }
        }
    }

    public Optional<Structure> getStructure(ResourceLocation identifier) {
        ResourceKey<Structure> registryKey = ResourceKey.create(Registries.STRUCTURE, identifier);

        Registry<Structure> structureRegistry = serverWorld.registryAccess().registryOrThrow(Registries.STRUCTURE);
        return structureRegistry.getOptional(registryKey);
    }

    public Optional<Structure.GenerationStub> getStructurePosition(ChunkPos chunkPos, Structure structure) {
        long seed = serverWorld.getSeed() ^ serverWorld.getRandom().nextLong(); // TODO make this more consistent?

        ChunkGenerator chunkGenerator = serverWorld.getChunkSource().getGenerator();
        Structure.GenerationContext context = new Structure.GenerationContext(
                serverWorld.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverWorld.getChunkSource().randomState(),
                serverWorld.getStructureManager(),
                seed,
                chunkPos,
                serverWorld,
                biome -> true
        );
        return structure.findValidGenerationPoint(context);
    }

    public static class StructureData {
        public final Structure structure;
        public final PiecesContainer structurePiecesList;
        public BlockPos startPos;
        public StructureData(Structure structure, PiecesContainer structurePiecesList, BlockPos startPos) {
            this.structure = structure;
            this.structurePiecesList = structurePiecesList;
            this.startPos = startPos;
        }

        public void translate(int x, int y, int z) {
            this.startPos = this.startPos.offset(x, y, z);
            this.structurePiecesList.pieces().forEach(structurePiece -> {
                structurePiece.move(x, y, z);
            });
        }
    }
}
