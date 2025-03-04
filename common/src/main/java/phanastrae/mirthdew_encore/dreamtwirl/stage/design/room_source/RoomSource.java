package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.nbt.CompoundTag;
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
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomLychseal;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.SourcedRoom;
import phanastrae.mirthdew_encore.dreamtwirl.stage.plan.room.RoomType;
import phanastrae.mirthdew_encore.mixin.ListPoolElementAccessor;
import phanastrae.mirthdew_encore.mixin.SinglePoolElementAccesor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class RoomSource {
    private final RoomType roomType;
    private final Structure structure;
    private final List<Room> goodRooms;
    private final List<Room> failedRooms;

    public RoomSource(RoomType roomType, Structure structure) {
        this.roomType = roomType;
        this.structure = structure;
        this.goodRooms = new ArrayList<>();
        this.failedRooms = new ArrayList<>();
    }

    public Optional<SourcedRoom> tryGetRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel) {
        if(!this.goodRooms.isEmpty()) {
            Room room = this.goodRooms.removeFirst();
            return Optional.of(new SourcedRoom(room, this));
        } else {
            if(!this.failedRooms.isEmpty()) {
                this.goodRooms.addAll(this.failedRooms);
                this.failedRooms.clear();
            }

            return this.tryGenerateRoom(stageSeed, stageCenterPos, random, serverLevel);
        }
    }

    public Optional<SourcedRoom> tryGenerateRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel) {
        Optional<PiecesContainer> piecesContainerOptional = tryMakePiecesContainer(this.structure, stageSeed, random, stageCenterPos, serverLevel);
        return piecesContainerOptional
                .map(piecesContainer -> {
                    Room.RoomObjects objects = collectRoomObjects(piecesContainer, serverLevel.getStructureManager(), random);
                    Room room = new Room(this.structure, this.roomType, piecesContainer, objects);
                    return new SourcedRoom(room, this);
                });
    }

    public void acceptDiscardedRoom(Room discardedRoom) {
        this.failedRooms.add(discardedRoom);
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

    public static Room.RoomObjects collectRoomObjects(PiecesContainer piecesContainer, StructureTemplateManager structureTemplateManager, RandomSource random) {
        List<RoomDoor> doors = new ObjectArrayList<>();
        List<RoomLychseal> seals = new ObjectArrayList<>();

        for(StructurePiece piece : piecesContainer.pieces()) {
            if(piece instanceof PoolElementStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getElement();

                List<StructureTemplate.StructureBlockInfo> doorList = getDoorMarkerInfos(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);

                for(StructureTemplate.StructureBlockInfo info : doorList) {
                    getDoorFromInfo(info).ifPresent(doors::add);
                }

                List<StructureTemplate.StructureBlockInfo> sealList = getLychsealMarkerInfos(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);
                for(StructureTemplate.StructureBlockInfo info : sealList) {
                    getLychsealFromInfo(info).ifPresent(seals::add);
                }
            }
        }

        return new Room.RoomObjects(doors, seals);
    }

    public static List<StructureTemplate.StructureBlockInfo> getDoorMarkerInfos(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        return getMarkerInfos(MirthdewEncoreBlocks.DOOR_MARKER, poolElement, structureTemplateManager, pos, rotation, random);
    }

    public static List<StructureTemplate.StructureBlockInfo> getGreaterAcheruneMarkerInfos(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        return getMarkerInfos(MirthdewEncoreBlocks.GREATER_ACHERUNE_MARKER, poolElement, structureTemplateManager, pos, rotation, random);
    }

    public static List<StructureTemplate.StructureBlockInfo> getLychsealMarkerInfos(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        return getMarkerInfos(MirthdewEncoreBlocks.LYCHSEAL_MARKER, poolElement, structureTemplateManager, pos, rotation, random);
    }

    public static List<StructureTemplate.StructureBlockInfo> getMarkerInfos(Block targetBlock, StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        if(poolElement instanceof SinglePoolElement singlePoolElement) {
            StructureTemplate structureTemplate = ((SinglePoolElementAccesor)singlePoolElement).invokeGetTemplate(structureTemplateManager);
            ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = structureTemplate.filterBlocks(
                    pos, new StructurePlaceSettings().setRotation(rotation), targetBlock, true
            );
            Util.shuffle(objectArrayList, random);
            objectArrayList.sort(Comparator.<StructureTemplate.StructureBlockInfo>comparingInt(block -> Optionull.mapOrDefault(block.nbt(), nbt -> nbt.getInt("selection_priority"), 0)).reversed());
            return objectArrayList;
        } else if(poolElement instanceof ListPoolElement listPoolElement) {
            List<StructurePoolElement> elements = ((ListPoolElementAccessor)listPoolElement).getElements();
            return getMarkerInfos(targetBlock, elements.getFirst(), structureTemplateManager, pos, rotation, random);
        } else {
            return new ObjectArrayList<>();
        }
    }

    private static Optional<RoomDoor> getDoorFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(state.hasProperty(property)) {
            FrontAndTop orientation = state.getValue(property);

            CompoundTag nbt = info.nbt();
            if(nbt == null) {
                return Optional.empty();
            } else {
                RoomDoor door = RoomDoor.fromNbt(nbt, info.pos(), orientation, false);
                return Optional.of(door);
            }
        } else {
            return Optional.empty();
        }
    }

    private static Optional<RoomLychseal> getLychsealFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(state.hasProperty(property)) {
            FrontAndTop orientation = state.getValue(property);

            CompoundTag nbt = info.nbt();
            if(nbt == null) {
                return Optional.empty();
            } else {
                RoomLychseal seal = RoomLychseal.fromNbt(nbt, info.pos(), orientation, false);
                return Optional.of(seal);
            }
        } else {
            return Optional.empty();
        }
    }
}
