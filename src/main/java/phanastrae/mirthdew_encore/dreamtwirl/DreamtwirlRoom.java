package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.Optionull;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.ListPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.mixin.ListPoolElementAccessor;
import phanastrae.mirthdew_encore.mixin.SinglePoolElementAccesor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DreamtwirlRoom {
    private static final Block GATE_BLOCK = Blocks.CRAFTER;

    public final DreamtwirlStageGenerator.StructureData structureData;
    private final List<Gate> gates = new ArrayList<>();
    private final List<Gate> emptyGates = new ArrayList<>();
    @Nullable
    private ChunkMap chunkMap;

    public DreamtwirlRoom(DreamtwirlStageGenerator.StructureData structureData) {
        this.structureData = structureData;
    }

    public void setChunkMap(@Nullable ChunkMap chunkMap) {
        if(this.chunkMap != null) {
            this.chunkMap.remove(this);
        }
        this.chunkMap = chunkMap;
        if(this.chunkMap != null) {
            this.chunkMap.add(this);
        }
    }

    public void collectGates(ServerLevel serverWorld) {
        StructureTemplateManager structureTemplateManager = serverWorld.getStructureManager();
        RandomSource random = serverWorld.getRandom();

        for(StructurePiece piece : this.structureData.structurePiecesList.pieces()) {
            if(piece instanceof PoolElementStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getElement();
                ObjectArrayList<StructureTemplate.StructureBlockInfo> list = getGates(poolElement, structureTemplateManager, poolStructurePiece.getPosition(), piece.getRotation(), random);
                for(StructureTemplate.StructureBlockInfo info : list) {
                    Optional<Gate> gateOptional = getGateFromInfo(info);
                    gateOptional.ifPresent(this::addGate);
                }
            }
        }
    }

    public Optional<Gate> getRandomEmptyGate(RandomSource random) {
        return getRandomGateFrom(this.emptyGates, random);
    }

    public Optional<Gate> getRandomEmptyGateMatching(RandomSource random, FrontAndTop orientation) {
        Direction targetOrientation = orientation.front().getOpposite(); // TODO account for the multiple up and down orientations?

        List<Gate> valid = new ArrayList<>();
        for(Gate gate : this.emptyGates) {
            if(gate.orientation.front().equals(targetOrientation)) {
                valid.add(gate);
            }
        }

        return getRandomGateFrom(valid, random);
    }

    public static Optional<Gate> getRandomGateFrom(List<Gate> gateList, RandomSource random) {
        if(gateList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(gateList.get(random.nextInt(gateList.size())));
        }
    }

    public void matchGates(Gate thisGate, Gate targetGate, DreamtwirlRoom target) {
        BlockPos targetPos = targetGate.pos.relative(targetGate.orientation.front());
        BlockPos currentPos = thisGate.pos;
        this.translate(targetPos.subtract(currentPos));

        this.markGateFull(thisGate);
        target.markGateFull(targetGate);
    }

    public void markGateFull(Gate gate) {
        this.emptyGates.remove(gate);
    }

    public void addGate(Gate gate) {
        this.gates.add(gate);
        this.emptyGates.add(gate);
    }

    public void centerAt(Vec3i vec3i) {
        this.translate(vec3i.subtract(this.getBoundingBox().getCenter()));
    }

    public void translate(Vec3i vec3i) {
        this.translate(vec3i.getX(), vec3i.getY(), vec3i.getZ());
    }

    public void translate(int x, int y, int z) {
        if(this.chunkMap != null) {
            this.chunkMap.remove(this);
        }
        this.structureData.translate(x, y, z);
        this.gates.forEach(gate -> gate.translate(x, y, z));
        if(this.chunkMap != null) {
            this.chunkMap.add(this);
        }
    }

    public BoundingBox getBoundingBox() {
        return this.structureData.structurePiecesList.calculateBoundingBox();
    }

    private static Optional<Gate> getGateFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<FrontAndTop> property = BlockStateProperties.ORIENTATION;
        if(!state.hasProperty(property)) {
            return Optional.empty();
        }
        FrontAndTop orientation = state.getValue(property);
        BlockPos pos = info.pos();
        CompoundTag nbt = info.nbt();
        Gate gate = new Gate(pos, orientation);
        return Optional.of(gate);
    }

    public static ObjectArrayList<StructureTemplate.StructureBlockInfo> getGates(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, Rotation rotation, RandomSource random) {
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

    public static class Gate {
        BlockPos pos;
        FrontAndTop orientation;

        public Gate(BlockPos pos, FrontAndTop orientation) {
            this.pos = pos;
            this.orientation = orientation;
        }

        public void translate(int x, int y, int z) {
            this.pos = this.pos.offset(x, y, z);
        }
    }
}
