package phanastrae.mirthdew_encore.dreamtwirl.stage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.*;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;

public class RoomPlacer {

    public static boolean placeRoom(DreamtwirlRoom room, ServerLevel serverLevel, ChunkPos stageChunkCenter, BoundingBox areaBox) {
        return placeStructure(room.structureData, serverLevel, stageChunkCenter, areaBox);
    }

    public static boolean placeStructure(StructureData structureData, ServerLevel serverLevel, ChunkPos stageChunkCenter, BoundingBox areaBox) {
        Structure structure = structureData.structure;
        PiecesContainer structurePiecesList = structureData.piecesContainer;

        StructureStart structureStart = new StructureStart(structure, stageChunkCenter, 0, structurePiecesList);
        if(!structureStart.isValid()) {
            return false;
        }

        BoundingBox blockBox = structureStart.getBoundingBox();
        ChunkPos chunkPosStart = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.minX()), SectionPos.blockToSectionCoord(blockBox.minZ()));
        ChunkPos chunkPosEnd = new ChunkPos(SectionPos.blockToSectionCoord(blockBox.maxX()), SectionPos.blockToSectionCoord(blockBox.maxZ()));
        // check the entire structure is loaded
        if (ChunkPos.rangeClosed(chunkPosStart, chunkPosEnd).anyMatch(p -> !serverLevel.isLoaded(p.getWorldPosition()))) {
            return false;
        }

        List<StructurePiece> list = structurePiecesList.pieces();
        if(list.isEmpty()) {
            return true;
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

                            BoundingBox chunkBox = new BoundingBox(chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight(), chunkPos.getMaxBlockZ());
                            for(StructurePiece structurePiece : list) {
                                if (structurePiece.getBoundingBox().intersects(chunkBox)) {
                                    if(structurePiece instanceof PoolElementStructurePiece poolStructurePiece) {
                                        generate(poolStructurePiece, serverLevel.getStructureManager(), serverLevel, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, firstPieceBasePosition);
                                    } else {
                                        structurePiece.postProcess(serverLevel, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, firstPieceBasePosition);
                                    }
                                }
                            }

                            structure.afterPlace(serverLevel, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, structurePiecesList);
                        }
                );
        return true;
    }

    public static void generate(PoolElementStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, ServerLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, ChunkPos chunkPos, BlockPos pivot) {
        // TODO wait why is chunkpos not used?
        StructurePoolElement poolElement = structurePiece.getElement();
        if(poolElement.place(structureTemplateManager, world, structureAccessor, chunkGenerator, structurePiece.getPosition(), pivot, structurePiece.getRotation(), chunkBox, random, LiquidSettings.IGNORE_WATERLOGGING, false)) {
            poolElement.getShuffledJigsawBlocks(structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            // TODO is calling getGates in two places needed?
            List<StructureTemplate.StructureBlockInfo> gateInfos =  StructureData.getGates(poolElement, structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo gateInfo : gateInfos) {
                // TODO handle properly
                // TODO get gates from the room directly?
                world.setBlockAndUpdate(gateInfo.pos(), Blocks.AIR.defaultBlockState());
            }
        }
    }
}
