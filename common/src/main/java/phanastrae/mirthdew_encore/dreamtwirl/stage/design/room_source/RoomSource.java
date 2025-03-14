package phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.pools.*;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
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
    public static final String KEY_ROOM_TYPE = "room_type";
    public static final String KEY_ATTEMPT_ROOMS = "attempt_rooms";
    public static final String KEY_FAILED_ROOMS = "failed_rooms";

    private final RoomType roomType;
    private final List<Room> attemptRooms;
    private final List<Room> failedRooms;

    public RoomSource(RoomType roomType) {
        this.roomType = roomType;
        this.attemptRooms = new ArrayList<>();
        this.failedRooms = new ArrayList<>();
    }

    public CompoundTag writeNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        RoomType.CODEC
                .encodeStart(registryops, this.roomType)
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to encode room type for Room: '{}'", st))
                .ifPresent(bpdTag -> nbt.put(KEY_ROOM_TYPE, bpdTag));

        ListTag attemptRoomList = new ListTag();
        for(Room room : this.attemptRooms) {
            attemptRoomList.add(room.writeNbt(new CompoundTag(), registries, spsContext));
        }
        nbt.put(KEY_ATTEMPT_ROOMS, attemptRoomList);

        ListTag failedRoomList = new ListTag();
        for(Room room : this.failedRooms) {
            failedRoomList.add(room.writeNbt(new CompoundTag(), registries, spsContext));
        }
        nbt.put(KEY_FAILED_ROOMS, failedRoomList);

        return nbt;
    }

    public static @Nullable RoomSource fromNbt(CompoundTag nbt, HolderLookup.Provider registries, StructurePieceSerializationContext spsContext) {
        RegistryOps<Tag> registryops = registries.createSerializationContext(NbtOps.INSTANCE);

        if(!nbt.contains(KEY_ROOM_TYPE, Tag.TAG_COMPOUND)) {
            return null;
        }
        Optional<RoomType> roomTypeOptional = RoomType.CODEC
                .parse(registryops, nbt.get(KEY_ROOM_TYPE))
                .resultOrPartial(st -> MirthdewEncore.LOGGER.error("Failed to parse room type for Room: '{}'", st));
        if(roomTypeOptional.isEmpty()) {
            return null;
        }
        RoomType roomType = roomTypeOptional.get();

        RoomSource source = new RoomSource(roomType);

        if(nbt.contains(KEY_ATTEMPT_ROOMS, Tag.TAG_LIST)) {
            ListTag attemptRoomList = nbt.getList(KEY_ATTEMPT_ROOMS, Tag.TAG_COMPOUND);

            for(int i = 0; i < attemptRoomList.size(); i++) {
                CompoundTag tag = attemptRoomList.getCompound(i);

                Room room = Room.fromNbt(tag, registries, spsContext);
                if(room != null) {
                    source.attemptRooms.add(room);
                }
            }
        }

        if(nbt.contains(KEY_FAILED_ROOMS, Tag.TAG_LIST)) {
            ListTag failedRoomList = nbt.getList(KEY_FAILED_ROOMS, Tag.TAG_COMPOUND);

            for(int i = 0; i < failedRoomList.size(); i++) {
                CompoundTag tag = failedRoomList.getCompound(i);

                Room room = Room.fromNbt(tag, registries, spsContext);
                if(room != null) {
                    source.failedRooms.add(room);
                }
            }
        }

        return source;
    }

    public Optional<SourcedRoom> tryGetRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel, StageDesignData designData) {
        if(!this.attemptRooms.isEmpty()) {
            Room room = this.attemptRooms.removeFirst();
            return Optional.of(new SourcedRoom(room, this));
        } else {
            if(!this.failedRooms.isEmpty()) {
                this.attemptRooms.addAll(this.failedRooms);
                this.failedRooms.clear();
            }

            return this.tryGenerateRoom(stageSeed, stageCenterPos, random, serverLevel, designData);
        }
    }

    public Optional<SourcedRoom> tryGenerateRoom(long stageSeed, ChunkPos stageCenterPos, RandomSource random, ServerLevel serverLevel, StageDesignData designData) {
        // try get pieces container
        Optional<PiecesContainer> piecesContainerOptional = tryMakePiecesContainer(
                serverLevel,
                random,
                stageSeed,
                stageCenterPos.getWorldPosition(),
                this.roomType
        );

        // generate room if present
        return piecesContainerOptional
                .map(piecesContainer -> {
                    long roomId = designData.getNextRoomId();
                    Room.RoomObjects objects = collectRoomObjects(piecesContainer, serverLevel.getStructureManager(), random, roomId);
                    Room room = new Room(roomId, this.roomType, piecesContainer, objects);
                    return new SourcedRoom(room, this);
                });
    }

    public void acceptDiscardedRoom(Room discardedRoom) {
        this.failedRooms.add(discardedRoom);
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public static Optional<PiecesContainer> tryMakePiecesContainer(ServerLevel serverLevel, RandomSource random, long stageSeed, BlockPos pos, RoomType roomType) {
        return tryMakePiecesContainer(
                serverLevel,
                stageSeed ^ random.nextLong(),
                pos,
                roomType.maxDepth(),
                roomType.templatePool()
        );
    }

    public static Optional<PiecesContainer> tryMakePiecesContainer(ServerLevel serverLevel, long seed, BlockPos pos, int maxDepth, ResourceLocation templatePool) {
        // get registry access
        RegistryAccess registryAccess = serverLevel.registryAccess();
        Optional<Registry<StructureTemplatePool>> structureTemplateRegistryOptional = registryAccess.registry(Registries.TEMPLATE_POOL);
        if(structureTemplateRegistryOptional.isEmpty()) {
            return Optional.empty();
        }
        Registry<StructureTemplatePool> structureTemplateRegistry = structureTemplateRegistryOptional.get();

        // get template pool holder
        Optional<Holder.Reference<StructureTemplatePool>> holderOptional = structureTemplateRegistry.getHolder(templatePool);
        if(holderOptional.isEmpty()) {
            return Optional.empty();
        }
        Holder<StructureTemplatePool> startPool = holderOptional.get();

        // get generation context
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        Structure.GenerationContext generationContext = new Structure.GenerationContext(
                registryAccess,
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverLevel.getChunkSource().randomState(),
                serverLevel.getStructureManager(),
                seed,
                new ChunkPos(pos),
                serverLevel,
                biome -> true
        );

        // gen generation stub
        Optional<Structure.GenerationStub> genStubOptional = JigsawPlacement.addPieces(
                generationContext,
                startPool,
                Optional.empty(), // this determines structure spawn position, but the structure gets repositioned anyway, so this is not needed and can be left empty
                maxDepth,
                pos,
                false,
                Optional.empty(),
                128,
                PoolAliasLookup.EMPTY,
                JigsawStructure.DEFAULT_DIMENSION_PADDING,
                JigsawStructure.DEFAULT_LIQUID_SETTINGS
        );

        // return pieces
        return genStubOptional.map(generationStub -> generationStub.getPiecesBuilder().build());
    }

    public static Room.RoomObjects collectRoomObjects(PiecesContainer piecesContainer, StructureTemplateManager structureTemplateManager, RandomSource random, long roomId) {
        List<RoomDoor> doors = new ObjectArrayList<>();
        int doorId = 0;
        List<RoomLychseal> seals = new ObjectArrayList<>();

        for(StructurePiece piece : piecesContainer.pieces()) {
            if(piece instanceof PoolElementStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getElement();

                List<StructureTemplate.StructureBlockInfo> doorList = getDoorMarkerInfos(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);

                for(StructureTemplate.StructureBlockInfo info : doorList) {
                    Optional<RoomDoor> doorOptional = getDoorFromInfo(info, doorId, roomId);
                    if(doorOptional.isPresent()) {
                        doors.add(doorOptional.get());
                        doorId++;
                    }
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

    private static Optional<RoomDoor> getDoorFromInfo(StructureTemplate.StructureBlockInfo info, int id, long roomId) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(state.hasProperty(property)) {
            FrontAndTop orientation = state.getValue(property);

            CompoundTag nbt = info.nbt();
            if(nbt == null) {
                return Optional.empty();
            } else {
                RoomDoor door = RoomDoor.fromNbt(id, roomId, nbt, info.pos(), orientation, false);
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
