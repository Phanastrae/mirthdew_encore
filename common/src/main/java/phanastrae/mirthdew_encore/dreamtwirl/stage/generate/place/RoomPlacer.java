package phanastrae.mirthdew_encore.dreamtwirl.stage.generate.place;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.Blocks;
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
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;

import java.util.List;

public class RoomPlacer {

    public static void spawnParticles(ServerLevel level, Room room) {
        BoundingBox box = room.getBoundingBox();
        level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                0.5 * (box.minX() + box.maxX()),
                0.5 * (box.minY() + box.maxY()),
                0.5 * (box.minZ() + box.maxZ()),
                600,
                0.5 * (box.maxX() - box.minX()),
                0.5 * (box.maxY() - box.minY()),
                0.5 * (box.maxZ() - box.minZ()),
                0.15);
    }

    public static boolean placeStructure(Room room, ServerLevel serverLevel, BoundingBox areaBox, boolean forceLoadChunks) {
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

                            BoundingBox chunkBox = new BoundingBox(chunkPos.getMinBlockX(), serverLevel.getMinBuildHeight(), chunkPos.getMinBlockZ(), chunkPos.getMaxBlockX(), serverLevel.getMaxBuildHeight(), chunkPos.getMaxBlockZ());
                            for(StructurePiece structurePiece : list) {
                                if (structurePiece.getBoundingBox().intersects(chunkBox)) {
                                    if(structurePiece instanceof PoolElementStructurePiece poolStructurePiece) {
                                        generate(poolStructurePiece, serverLevel.getStructureManager(), serverLevel, structureAccessor, chunkGenerator, random, chunkBox, firstPieceBasePosition);
                                    } else {
                                        structurePiece.postProcess(serverLevel, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, firstPieceBasePosition);
                                    }
                                }
                            }

                            structure.afterPlace(serverLevel, structureAccessor, chunkGenerator, random, chunkBox, chunkPos, piecesContainer);
                        }
                );
        return true;
    }

    public static void generate(PoolElementStructurePiece structurePiece, StructureTemplateManager structureTemplateManager, ServerLevel world, StructureManager structureAccessor, ChunkGenerator chunkGenerator, RandomSource random, BoundingBox chunkBox, BlockPos pivot) {
        StructurePoolElement poolElement = structurePiece.getElement();
        if(poolElement.place(structureTemplateManager, world, structureAccessor, chunkGenerator, structurePiece.getPosition(), pivot, structurePiece.getRotation(), chunkBox, random, LiquidSettings.IGNORE_WATERLOGGING, false)) {
            poolElement.getShuffledJigsawBlocks(structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            // TODO get gates from the room directly?
            // TODO is calling getGates in two places needed?
            // i think this is here because in the event the room changes (ie datapack changes after world reload once i add serialization), the door positions could change
            // and thus it would make sense to do the block state setting separately to avoid having any new gates not getting set

            List<StructureTemplate.StructureBlockInfo> doorInfos = RoomSource.getDoorMarkerInfos(poolElement, structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo doorInfo : doorInfos) {
                // TODO handle properly (ie add a custom replace state etc)
                world.setBlockAndUpdate(doorInfo.pos(), Blocks.AIR.defaultBlockState());
            }

            List<StructureTemplate.StructureBlockInfo> greaterAcheruneInfos = RoomSource.getGreaterAcheruneMarkerInfos(poolElement, structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo runeInfo : greaterAcheruneInfos) {
                world.setBlockAndUpdate(runeInfo.pos(), MirthdewEncoreBlocks.GREATER_ACHERUNE.defaultBlockState());
            }

            List<StructureTemplate.StructureBlockInfo> lychsealInfos = RoomSource.getLychsealMarkerInfos(poolElement, structureTemplateManager, structurePiece.getPosition(), structurePiece.getRotation(), random);
            for(StructureTemplate.StructureBlockInfo sealInfo : lychsealInfos) {
                BlockState oldState = sealInfo.state();
                BlockState newState = MirthdewEncoreBlocks.LYCHSEAL.defaultBlockState();

                if(oldState.hasProperty(BlockStateProperties.ORIENTATION)) {
                    newState = newState.setValue(BlockStateProperties.ORIENTATION, oldState.getValue(BlockStateProperties.ORIENTATION));
                }

                // TODO do nbt linking stuff
                world.setBlockAndUpdate(sealInfo.pos(), newState);
            }
        }
    }
}
