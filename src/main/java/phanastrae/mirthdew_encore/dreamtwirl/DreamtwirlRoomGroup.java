package phanastrae.mirthdew_encore.dreamtwirl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.List;
import java.util.Optional;

public class DreamtwirlRoomGroup {

    private final List<DreamtwirlRoom> rooms;
    public final BlockPos center;

    public DreamtwirlRoomGroup(BlockPos center) {
        this.rooms = new ObjectArrayList<>();
        this.center = center;
    }

    public void addRoom(DreamtwirlRoom dreamtwirlRoom, ChunkMap chunkMap) {
        this.rooms.add(dreamtwirlRoom);
        dreamtwirlRoom.setChunkMap(chunkMap);
    }

    public List<DreamtwirlRoom> getRooms() {
        return this.rooms;
    }

    public void sprawl(DreamtwirlStageGenerator stageGenerator, Random random) {
        Identifier fourwayMiniId = MirthdewEncore.id("test/fourway/mini");

        int MAX_ROOMS = 3;
        int MAX_OPERATIONS = 20;
        int rooms = 0;
        int operations = 0;
        for(int i = 0; i < 6; i++) {
            int size = this.rooms.size();
            for(int j = 0; j < size; j++) {
                if(rooms >= MAX_ROOMS) break;
                if(operations >= MAX_OPERATIONS) break;
                operations++;

                DreamtwirlRoom room = this.rooms.get(j);

                Optional<DreamtwirlRoom.Gate> gateOptional = room.getRandomEmptyGate(random);

                if(gateOptional.isPresent()) {
                    DreamtwirlRoom.Gate gate = gateOptional.get();

                    Optional<DreamtwirlRoom> fourwayOptional = stageGenerator.getRoomOfType(fourwayMiniId);
                    if(fourwayOptional.isPresent()) {
                        DreamtwirlRoom fourway = fourwayOptional.get();

                        Optional<DreamtwirlRoom.Gate> fourwayGateOptional = fourway.getRandomEmptyGateMatching(random, gate.orientation);
                        if(fourwayGateOptional.isPresent()) {
                            DreamtwirlRoom.Gate fourwayGate = fourwayGateOptional.get();

                            fourway.matchGates(fourwayGate, gate, room);
                            Direction direction = gate.orientation.getFacing();
                            Vec3i vector = direction.getVector();
                            fourway.translate(vector.multiply(1).add(random.nextInt(3) - 1, random.nextInt(3) - 1, random.nextInt(3) - 1));

                            stageGenerator.adjustPosition(fourway, random, direction);
                            if(stageGenerator.isLocationValid(fourway)) {
                                this.addRoom(fourway, stageGenerator.getChunkMap());
                                rooms++;
                            }
                        }
                    }
                }
            }
        }
    }

    public Optional<DreamtwirlRoom> getClosestRoomToPos(BlockPos blockPos) {
        if(this.rooms.isEmpty()) {
            return Optional.empty();
        } else {
            DreamtwirlRoom minRoom = this.rooms.getFirst();
            int minLengthSquared = Integer.MAX_VALUE;
            for(DreamtwirlRoom room : this.rooms) {
                BlockPos pos = room.getBoundingBox().getCenter();
                Vec3i offset = pos.subtract(blockPos);
                int lengthSquared = offset.getX()*offset.getX() + offset.getY()*offset.getY() * 8 + offset.getZ()*offset.getZ(); // TODO use custom distance measurement
                if(lengthSquared < minLengthSquared) {
                    minLengthSquared = lengthSquared;
                    minRoom = room;
                }
            }

            return Optional.of(minRoom);
        }
    }
}
