package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import phanastrae.mirthdew_encore.mixin.ListPoolElementAccessor;
import phanastrae.mirthdew_encore.mixin.SinglePoolElementAccesor;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class StructureData {
    private static final Block GATE_BLOCK = MirthdewEncoreBlocks.DOOR_MARKER;

    public final Structure structure;
    public final PiecesContainer piecesContainer;
    public StructureData(Structure structure, PiecesContainer piecesContainer) {
        this.structure = structure;
        this.piecesContainer = piecesContainer;
    }

    public void translate(int x, int y, int z) {
        this.piecesContainer.pieces().forEach(structurePiece -> {
            structurePiece.move(x, y, z);
        });
    }

    public List<RoomGate> collectGates(StructureTemplateManager structureTemplateManager, RandomSource random) {
        List<RoomGate> gates = new ObjectArrayList<>();
        for(StructurePiece piece : this.piecesContainer.pieces()) {
            if(piece instanceof PoolElementStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getElement();
                List<StructureTemplate.StructureBlockInfo> list = getGates(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);
                for(StructureTemplate.StructureBlockInfo info : list) {
                    Optional<RoomGate> gateOptional = getGateFromInfo(info);
                    gateOptional.ifPresent(gates::add);
                }
            }
        }
        return gates;
    }

    public static List<StructureTemplate.StructureBlockInfo> getGates(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
        if(poolElement instanceof SinglePoolElement singlePoolElement) {
            StructureTemplate structureTemplate = ((SinglePoolElementAccesor)singlePoolElement).invokeGetTemplate(structureTemplateManager);
            ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = structureTemplate.filterBlocks(
                    pos, new StructurePlaceSettings().setRotation(rotation), GATE_BLOCK, true
            );
            Util.shuffle(objectArrayList, random);
            objectArrayList.sort(Comparator.<StructureTemplate.StructureBlockInfo>comparingInt(block -> Optionull.mapOrDefault(block.nbt(), nbt -> nbt.getInt("selection_priority"), 0)).reversed());
            return objectArrayList;
        } else if(poolElement instanceof ListPoolElement listPoolElement) {
            List<StructurePoolElement> elements = ((ListPoolElementAccessor)listPoolElement).getElements();
            return getGates(elements.getFirst(), structureTemplateManager, pos, rotation, random);
        } else {
            return new ObjectArrayList<>();
        }
    }

    private static Optional<RoomGate> getGateFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(!state.hasProperty(property)) {
            return Optional.empty();
        }
        FrontAndTop orientation = state.getValue(property);
        BlockPos pos = info.pos();
        CompoundTag nbt = info.nbt();
        RoomGate gate = new RoomGate(pos, orientation);
        return Optional.of(gate);
    }

    public static Optional<StructureData> makeStructureData(ResourceLocation identifier, ServerLevel serverLevel, RandomSource random, ChunkPos stageChunkCenter) {
        Optional<Structure> structureOptional = getStructure(identifier, serverLevel.registryAccess());
        if(structureOptional.isPresent()) {
            Structure structure = structureOptional.get();

            Optional<Structure.GenerationStub> generationStubOptional = getStructurePosition(structure, stageChunkCenter, random, serverLevel);
            if(generationStubOptional.isPresent()) {
                Structure.GenerationStub generationStub = generationStubOptional.get();

                PiecesContainer piecesContainer = generationStub.getPiecesBuilder().build();
                return Optional.of(new StructureData(structure, piecesContainer));
            }
        }
        return Optional.empty();
    }

    public static Optional<Structure.GenerationStub> getStructurePosition(Structure structure, ChunkPos chunkPos, RandomSource random, ServerLevel serverLevel) {
        long seed = random.nextLong();
        ChunkGenerator chunkGenerator = serverLevel.getChunkSource().getGenerator();

        Structure.GenerationContext context = new Structure.GenerationContext(
                serverLevel.registryAccess(),
                chunkGenerator,
                chunkGenerator.getBiomeSource(),
                serverLevel.getChunkSource().randomState(),
                serverLevel.getStructureManager(),
                seed,
                chunkPos,
                serverLevel,
                biome -> true
        );
        return structure.findValidGenerationPoint(context);
    }

    public static Optional<Structure> getStructure(ResourceLocation identifier, RegistryAccess registryAccess) {
        Registry<Structure> structureRegistry = registryAccess.registryOrThrow(Registries.STRUCTURE);
        ResourceKey<Structure> registryKey = ResourceKey.create(Registries.STRUCTURE, identifier);
        return structureRegistry.getOptional(registryKey);
    }
}
