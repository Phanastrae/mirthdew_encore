package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;

import java.util.List;
import java.util.Optional;

public class DreamtwirlRoomGroup {

    private final List<DreamtwirlRoom> rooms;
    public final BlockPos start;

    public DreamtwirlRoomGroup(BlockPos start) {
        this.rooms = new ObjectArrayList<>();
        this.start = start;
    }

    public void addRoom(DreamtwirlRoom dreamtwirlRoom, StructureChunkMap chunkMap) {
        this.rooms.add(dreamtwirlRoom);
        dreamtwirlRoom.setChunkMap(chunkMap);
    }

    public List<DreamtwirlRoom> getRooms() {
        return this.rooms;
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
