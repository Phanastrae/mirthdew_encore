package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.enums.Orientation;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.*;
import net.minecraft.structure.pool.ListPoolElement;
import net.minecraft.structure.pool.SinglePoolElement;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Nullables;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
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

    public void collectGates(ServerWorld serverWorld) {
        StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();
        Random random = serverWorld.getRandom();

        for(StructurePiece piece : this.structureData.structurePiecesList.pieces()) {
            if(piece instanceof PoolStructurePiece poolStructurePiece) {
                StructurePoolElement poolElement = poolStructurePiece.getPoolElement();
                ObjectArrayList<StructureTemplate.StructureBlockInfo> list = getGates(poolElement, structureTemplateManager, poolStructurePiece.getPos(), piece.getRotation(), random);
                for(StructureTemplate.StructureBlockInfo info : list) {
                    Optional<Gate> gateOptional = getGateFromInfo(info);
                    gateOptional.ifPresent(this::addGate);
                }
            }
        }
    }

    public Optional<Gate> getRandomEmptyGate(Random random) {
        return getRandomGateFrom(this.emptyGates, random);
    }

    public Optional<Gate> getRandomEmptyGateMatching(Random random, Orientation orientation) {
        Direction targetOrientation = orientation.getFacing().getOpposite(); // TODO account for the multiple up and down orientations?

        List<Gate> valid = new ArrayList<>();
        for(Gate gate : this.emptyGates) {
            if(gate.orientation.getFacing().equals(targetOrientation)) {
                valid.add(gate);
            }
        }

        return getRandomGateFrom(valid, random);
    }

    public static Optional<Gate> getRandomGateFrom(List<Gate> gateList, Random random) {
        if(gateList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(gateList.get(random.nextInt(gateList.size())));
        }
    }

    public void matchGates(Gate thisGate, Gate targetGate, DreamtwirlRoom target) {
        BlockPos targetPos = targetGate.pos.offset(targetGate.orientation.getFacing());
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

    public BlockBox getBoundingBox() {
        return this.structureData.structurePiecesList.getBoundingBox();
    }

    private static Optional<Gate> getGateFromInfo(StructureTemplate.StructureBlockInfo info) {
        BlockState state = info.state();
        EnumProperty<Orientation> property = Properties.ORIENTATION;
        if(!state.contains(property)) {
            return Optional.empty();
        }
        Orientation orientation = state.get(property);
        BlockPos pos = info.pos();
        NbtCompound nbt = info.nbt();
        Gate gate = new Gate(pos, orientation);
        return Optional.of(gate);
    }

    public static ObjectArrayList<StructureTemplate.StructureBlockInfo> getGates(StructurePoolElement poolElement, StructureTemplateManager structureTemplateManager, BlockPos pos, BlockRotation rotation, Random random) {
        if(poolElement instanceof SinglePoolElement singlePoolElement) {
            StructureTemplate structureTemplate = ((SinglePoolElementAccesor)singlePoolElement).invokeGetStructure(structureTemplateManager);
            ObjectArrayList<StructureTemplate.StructureBlockInfo> objectArrayList = structureTemplate.getInfosForBlock(
                    pos, new StructurePlacementData().setRotation(rotation), GATE_BLOCK, true
            );
            Util.shuffle(objectArrayList, random);
            objectArrayList.sort(Comparator.<StructureTemplate.StructureBlockInfo>comparingInt(block -> Nullables.mapOrElse(block.nbt(), nbt -> nbt.getInt("selection_priority"), 0)).reversed());
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
        Orientation orientation;

        public Gate(BlockPos pos, Orientation orientation) {
            this.pos = pos;
            this.orientation = orientation;
        }

        public void translate(int x, int y, int z) {
            this.pos = this.pos.add(x, y, z);
        }
    }
}
