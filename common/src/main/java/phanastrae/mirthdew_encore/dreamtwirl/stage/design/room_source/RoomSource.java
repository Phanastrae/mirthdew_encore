package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.mixin.ListPoolElementAccessor;
import phanastrae.mirthdew_encore.mixin.SinglePoolElementAccesor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RoomSource {
    private static final Block DOOR_MARKER = MirthdewEncoreBlocks.DOOR_MARKER;

    private final RoomType roomType;
    private final Structure structure;
    private final List<Room> goodPrefabs;
    private final List<Room> failedPrefabs;

    public RoomSource(RoomType roomType, Structure structure) {
        this.roomType = roomType;
        this.structure = structure;
        this.goodPrefabs = new ArrayList<>();
        this.failedPrefabs = new ArrayList<>();
    }

    public Optional<Room> tryGetRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel) {
        if(!this.goodPrefabs.isEmpty()) {
            Room prefab = this.goodPrefabs.removeFirst();
            return Optional.of(prefab);
        } else {
            if(!this.failedPrefabs.isEmpty()) {
                this.goodPrefabs.addAll(this.failedPrefabs);
                this.failedPrefabs.clear();
            }

            return this.tryGenerateRoom(stageSeed, stageCenterPos, random, serverLevel);
        }
    }

    public Optional<Room> tryGenerateRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel) {
        Optional<PiecesContainer> piecesContainerOptional = tryMakePiecesContainer(this.structure, stageSeed, random, stageCenterPos, serverLevel);
        return piecesContainerOptional
                .map(piecesContainer -> new Room(this, piecesContainer, collectDoors(piecesContainer, serverLevel.getStructureManager(), random)));
    }

    public void acceptDiscardedRoom(Room discardedRoom) {
        this.failedPrefabs.add(discardedRoom);
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public Structure getStructure() {
        return structure;
    }

    public static Optional<PiecesContainer> tryMakePiecesContainer(Structure structure, long stageSeed, RandomSource random, ChunkPos stageCenterPos, ServerLevel serverLevel) {
        Optional<Structure.GenerationStub> generationStubOptional = tryGetStructureGenStub(structure, stageSeed, random, stageCenterPos, serverLevel);
        return generationStubOptional.map(generationStub -> generationStub.getPiecesBuilder().build());
    }

    public static Optional<Structure.GenerationStub> tryGetStructureGenStub(Structure structure, long stageSeed, RandomSource random, ChunkPos stageCenterPos, ServerLevel serverLevel) {
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        // need to give the rooms individual seeds to stop them from all having the same orientation/layout/etc.
        long roomSeed = random.nextLong() ^ stageSeed;

        Structure.GenerationContext context = new Structure.GenerationContext(
                serverLevel.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverLevel.getChunkSource().randomState(),
                serverLevel.getStructureManager(),
                roomSeed,
                stageCenterPos,
                serverLevel,
                biome -> true
        );
        return structure.findValidGenerationPoint(context);
    }

    public static List<RoomDoor> collectDoors(PiecesContainer piecesContainer, StructureTemplateManager structureTemplateManager, RandomSource random) {
        List<RoomDoor> doors = new ObjectArrayList<>();

        for(StructurePiece piece : piecesContainer.pieces()) {
            if(piece instanceof PoolElementStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getElement();

                List<StructureTemplate.StructureBlockInfo> list = getDoors(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);

                for(StructureTemplate.StructureBlockInfo info : list) {
                    getDoorFromInfo(info).ifPresent(doors::add);
                }
            }
        }

        return doors;
    }

    public static List<StructureTemplate.StructureBlockInfo> getDoors(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        if(poolElement instanceof SinglePoolElement singlePoolElement) {
            StructureTemplate structureTemplate = ((SinglePoolElementAccesor)singlePoolElement).invokeGetTemplate(structureTemplateManager);
            ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = structureTemplate.filterBlocks(
                    pos, new StructurePlaceSettings().setRotation(rotation), DOOR_MARKER, true
            );
            Util.shuffle(objectArrayList, random);
            objectArrayList.sort(Comparator.<StructureTemplate.StructureBlockInfo>comparingInt(block -> Optionull.mapOrDefault(block.nbt(), nbt -> nbt.getInt("selection_priority"), 0)).reversed());
            return objectArrayList;
        } else if(poolElement instanceof ListPoolElement listPoolElement) {
            List<StructurePoolElement> elements = ((ListPoolElementAccessor)listPoolElement).getElements();
            return getDoors(elements.getFirst(), structureTemplateManager, pos, rotation, random);
        } else {
            return new ObjectArrayList<>();
        }
    }

    private static Optional<RoomDoor> getDoorFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(state.hasProperty(property)) {
            FrontAndTop orientation = state.getValue(property);

            RoomDoor door = new RoomDoor(info.pos(), orientation, info.nbt());
            return Optional.of(door);
        } else {
            return Optional.empty();
        }
    }
}
