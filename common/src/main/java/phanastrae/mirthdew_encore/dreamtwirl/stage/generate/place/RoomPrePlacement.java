package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import phanastrae.mirthdew_encore.block.MirthdewEncoreBlocks;
import phanastrae.mirthdew_encore.block.entity.LychsealBlockEntity;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomLychseal;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;

import java.util.List;
import java.util.Optional;

public class RoomPrePlacement {

    public static boolean placeStructure(Room room, ServerLevel serverLevel, WorldGenLevel worldGenLevel, BoundingBox areaBox, boolean forceLoadChunks, int roomId) {
        Structure structure = room.getRoomSource().getStructure();
        PiecesContainer piecesContainer = room.getPiecesContainer();
        List<StructurePiece> list = piecesContainer.pieces();
        BoundingBox blockBox = room.getBoundingBox();

        if(list.isEmpty()) {
            return true;
        }

        ChunkPos chunkPosStart = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.minX()), SectionPos.blockToSectionCoord(blockBox.minZ()));
        ChunkPos chunkPosEnd = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.maxX()), SectionPos.blockToSectionCoord(blockBox.maxZ()));
        if(forceLoadChunks) {
            ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd).forEach(p -> serverLevel.getChunk(p.x, p.z));
        }
        // check the entire structure is loaded
        if (ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd).anyMatch(p -> !serverLevel.isLoaded(p.getWorldPosition()))) {
            return false;
        }

        BoundingBox firstPieceBB = list.getFirst().getBoundingBox();

        BlockPos firstPieceCenter = firstPieceBB.getCenter();
        BlockPos firstPieceBasePosition = new BlockPos(firstPieceCenter.getX(), firstPieceBB.minY(), firstPieceCenter.getZ());
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();
        StructureManager structureAccessor = serverLevel.structureManager();
        RandomSource random = serverLevel.getRandom();


        ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd)
                .forEach(
                        chunkPos -> {
                            if(!areaBox.isInside(chunkPos.getBlockAt(0, 0, 0))) {
                                return;
                            }

                            BoundingBox chunkBox = new BoundingBox(
                                    chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(),
                                    chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight(), chunkPos.getMaxBlockZ()
                            );
                            for(StructurePiece structurePiece : list) {
                                if (structurePiece.getBoundingBox().intersects(chunkBox)) {
                                    if(structurePiece instanceof PoolElementStructurePiece poolStructurePiece) {
                                        generate(
                                                poolStructurePiece,
                                                serverLevel.getStructureManager(),
                                                worldGenLevel,
                                                structureAccessor,
                                                chunkGenerator,
                                                random,
                                                chunkBox,
                                                firstPieceBasePosition
                                        );
                                    } else {
                                        structurePiece.postProcess(
                                                worldGenLevel,
                                                structureAccessor,
                                                chunkGenerator,
                                                random,
                                                chunkBox,
                                                chunkPos,
                                                firstPieceBasePosition
                                        );
                                    }
                                }
                            }

                            structure.afterPlace(
                                    worldGenLevel,
                                    structureAccessor,
                                    chunkGenerator,
                                    random,
                                    chunkBox,
                                    chunkPos,
                                    piecesContainer
                            );
                        }
                );
        for(StructurePiece structurePiece : list) {
            if (structurePiece instanceof PoolElementStructurePiece poolStructurePiece) {
                placeRoomObjects(poolStructurePiece, serverLevel.getStructureManager(), worldGenLevel, random, room, roomId);
            }
        }
        return true;
    }

    public static void generate(PoolElementStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, WorldGenLevel worldGenLevel, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, BlockPos pivot) {
        StructurePoolElement poolElement = structurePiece.getElement();
        poolElement.place(structureTemplateManager, worldGenLevel, structureAccessor, chunkGenerator, structurePiece.getPosition(), pivot, structurePiece.getRotation(), chunkBox, random, LiquidSettings.IGNORE_WATERLOGGING, false);
    }

    public static void placeRoomObjects(PoolElementStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, WorldGenLevel level, RandomSource random, Room room, int roomId) {
        StructurePoolElement poolElement = structurePiece.getElement();
        BlockPos pos = structurePiece.getPosition();
        Rotation rotation = structurePiece.getRotation();

        convertDoorMarkers(level, poolElement, structureTemplateManager, pos, rotation, random);
        convertAcheruneMarkers(level, poolElement, structureTemplateManager, pos, rotation, random);
        convertLychsealMarkers(level, poolElement, structureTemplateManager, pos, rotation, random, room, roomId);
    }

    public static void convertDoorMarkers(WorldGenLevel level, StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos piecePos, Rotation pieceRotation, RandomSource random) {
        // replace all door markers with normal blocks
        // we should already have this info available, but get it again to ensure the blocks are always set even if the structure piece has since been modified in the datapack
        List<StructureTemplate.StructureBlockInfo> doorInfos = RoomSource.getDoorMarkerInfos(poolElement, structureTemplateManager, piecePos, pieceRotation, random);
        for(StructureTemplate.StructureBlockInfo doorInfo : doorInfos) {
            BlockPos pos = doorInfo.pos();

            // TODO add custom replace state
            if(level.getBlockState(pos).is(MirthdewEncoreBlocks.DOOR_MARKER)) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
    }

    public static void convertAcheruneMarkers(WorldGenLevel level, StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos piecePos, Rotation pieceRotation, RandomSource random) {
        // replace all greater acherune markers with actual acherunes
        List<StructureTemplate.StructureBlockInfo> greaterAcheruneInfos = RoomSource.getGreaterAcheruneMarkerInfos(poolElement, structureTemplateManager, piecePos, pieceRotation, random);
        for(StructureTemplate.StructureBlockInfo runeInfo : greaterAcheruneInfos) {
            BlockPos pos = runeInfo.pos();

            if(level.getBlockState(pos).is(MirthdewEncoreBlocks.GREATER_ACHERUNE_MARKER)) {
                level.setBlock(pos, MirthdewEncoreBlocks.GREATER_ACHERUNE.defaultBlockState(), 3);
            }
        }
    }

    public static void convertLychsealMarkers(WorldGenLevel level, StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos piecePos, Rotation pieceRotation, RandomSource random, Room room, int roomId) {
        // replace all lychseal markers with actual lychseals
        List<StructureTemplate.StructureBlockInfo> lychsealInfos = RoomSource.getLychsealMarkerInfos(poolElement, structureTemplateManager, piecePos, pieceRotation, random);
        for(StructureTemplate.StructureBlockInfo sealInfo : lychsealInfos) {
            BlockPos pos = sealInfo.pos();

            BlockState oldState = sealInfo.state();
            BlockState newState = MirthdewEncoreBlocks.LYCHSEAL.defaultBlockState();

            if(oldState.hasProperty(BlockStateProperties.ORIENTATION)) {
                newState = newState.setValue(BlockStateProperties.ORIENTATION, oldState.getValue(BlockStateProperties.ORIENTATION));
            }

            if(level.getBlockState(pos).is(MirthdewEncoreBlocks.LYCHSEAL_MARKER)) {
                level.setBlock(pos, newState, 3);

                Optional<RoomLychseal> lychsealOptional = room.getUnplacedLychseal(pos);

                if(lychsealOptional.isPresent()) {
                    RoomLychseal lychseal = lychsealOptional.get();
                    if (level.getBlockEntity(pos) instanceof LychsealBlockEntity lychsealBlockEntity) {
                        lychsealBlockEntity.setRoomId(roomId);
                        lychsealBlockEntity.setLychsealName(lychseal.getLychsealName());
                    }
                } else {
                    level.destroyBlock(pos, false);
                }
            }
        }
    }
}
