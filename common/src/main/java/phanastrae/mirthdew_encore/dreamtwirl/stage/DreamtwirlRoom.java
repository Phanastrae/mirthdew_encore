package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class DreamtwirlRoom {
    public final StructureData structureData;
    private final List<RoomGate> gates = new ObjectArrayList<>();
    private final List<DreamtwirlRoom> connectedRooms = new ObjectArrayList<>();

    private boolean canSpawn = false;

    @Nullable
    private StructureChunkMap chunkMap;

    public DreamtwirlRoom(StructureData structureData) {
        this.structureData = structureData;
    }

    public void collectGates(StructureTemplateManager structureTemplateManager, RandomSource random) {
        this.structureData.collectGates(structureTemplateManager, random).forEach(this::addGate);
    }

    public static DreamtwirlRoom createRoom(StructureData structureData, StructureTemplateManager structureTemplateManager, RandomSource random) {
        DreamtwirlRoom room = new DreamtwirlRoom(structureData);
        room.collectGates(structureTemplateManager, random);
        return room;
    }

    public void setChunkMap(@Nullable StructureChunkMap chunkMap) {
        if(this.chunkMap != null) {
            this.chunkMap.remove(this);
        }
        this.chunkMap = chunkMap;
        if(this.chunkMap != null) {
            this.chunkMap.add(this);
        }
    }

    public Optional<RoomGate> getRandomEmptyGate(RandomSource random) {
        return getRandomGateMatching(random, gate -> !gate.isFilled());
    }

    public Optional<RoomGate> getRandomEmptyGateMatching(RandomSource random, FrontAndTop orientation) {
        // TODO account for the multiple up and down orientations?
        Direction targetOrientation = orientation.front().getOpposite();

        return getRandomGateMatching(random, gate -> !gate.isFilled() && gate.getOrientation().front().equals(targetOrientation));
    }

    public Optional<RoomGate> getRandomGateMatching(RandomSource random, Function<RoomGate, Boolean> condition) {
        List<RoomGate> valid = new ArrayList<>();
        for(RoomGate gate : this.gates) {
            if(condition.apply(gate)) {
                valid.add(gate);
            }
        }

        return getRandomGateFrom(valid, random);
    }

    public static Optional<RoomGate> getRandomGateFrom(List<RoomGate> gateList, RandomSource random) {
        if(gateList.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(gateList.get(random.nextInt(gateList.size())));
        }
    }

    public void translateToMatchGate(RoomGate thisGate, RoomGate targetGate) {
        BlockPos targetPos = targetGate.getPos().relative(targetGate.getOrientation().front());
        BlockPos currentPos = thisGate.getPos();
        this.translate(targetPos.subtract(currentPos));
    }

    public static void connectRooms(DreamtwirlRoom roomA, DreamtwirlRoom roomB, RoomGate gateA, RoomGate gateB) {
        gateA.setFilled(true);
        gateB.setFilled(true);

        roomA.connectTo(roomB);
        roomB.connectTo(roomA);
    }

    public void addGate(RoomGate gate) {
        this.gates.add(gate);
    }

    public void connectTo(DreamtwirlRoom room) {
        this.connectedRooms.add(room);
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

    public void setCanSpawn() {
        this.canSpawn = true;
    }

    public void setNeighborsCanSpawn() {
        for(DreamtwirlRoom room : this.connectedRooms) {
            room.setCanSpawn();
        }
    }

    public boolean canSpawn() {
        return this.canSpawn;
    }

    public BoundingBox getBoundingBox() {
        return this.structureData.piecesContainer.calculateBoundingBox();
    }

    public void forEachGate(Consumer<? super RoomGate> action) {
        this.gates.forEach(action);
    }
}
