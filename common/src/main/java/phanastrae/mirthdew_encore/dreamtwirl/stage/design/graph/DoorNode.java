package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room_source.RoomSource;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;

public class DoorNode {

    private final RoomDoor door;
    private final List<DirectedEdge> edgesOut = new ObjectArrayList<>();
    private final List<DirectedEdge> edgesIn = new ObjectArrayList<>();
    private final DistanceInfo distanceInfo = new DistanceInfo(this);
    private boolean removed = false;

    public DoorNode(RoomDoor door) {
        this.door = door;
    }

    public RoomDoor getDoor() {
        return door;
    }

    public @Nullable Room getParentRoom() {
        return this.door.getParentRoom();
    }

    public RoomDoor.DoorType getDoorType() {
        return this.door.getDoorType();
    }

    public List<DirectedEdge> getEdgesOut() {
        return edgesOut;
    }

    public List<DirectedEdge> getEdgesIn() {
        return edgesIn;
    }

    public void addEdgeOut(DirectedEdge edge) {
        this.edgesOut.add(edge);
    }

    public void addEdgeIn(DirectedEdge edge) {
        this.edgesIn.add(edge);
    }

    public void removeEdgeOut(DirectedEdge edge) {
        this.edgesOut.remove(edge);
    }

    public void removeEdgeIn(DirectedEdge edge) {
        this.edgesIn.remove(edge);
    }

    public DistanceInfo getDistanceInfo() {
        return distanceInfo;
    }

    public void update() {
        this.distanceInfo.update();
    }

    public void forEachNeighbour(Consumer<DoorNode> consumer) {
        this.getEdgesIn().forEach(edge -> consumer.accept(edge.getStart()));
        this.getEdgesOut().forEach(edge -> consumer.accept(edge.getEnd()));
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
        this.update();
    }

    public boolean isRemoved() {
        return removed;
    }

    public static class DistanceInfo {

        private final DoorNode doorNode;
        private int distanceFromEntrance = -1;

        public DistanceInfo(DoorNode doorNode) {
            this.doorNode = doorNode;
        }

        public void update() {
            boolean updated = false;

            boolean removed = this.doorNode.isRemoved();

            Room room = this.getParentRoom();
            RoomSource source = room == null ? null : room.getRoomSource();

            boolean isDreamtwirlEntrance = source != null && source.getRoomType().isEntrance();
            int distanceFromEntrance = removed ? -1 : calcDistanceFromEntrance(isDreamtwirlEntrance);

            if(this.distanceFromEntrance != distanceFromEntrance) {
                this.distanceFromEntrance = distanceFromEntrance;
                updated = true;
            }

            if(updated) {
                this.updateNeighbours();
            }
        }

        public int calcDistanceFromEntrance(boolean isDreamtwirlEntrance) {
            if(isDreamtwirlEntrance) {
                return 1;
            } else {
                return this.calcDistance(DistanceInfo::getDistanceFromEntrance);
            }
        }

        public int calcDistance(Function<DistanceInfo, Integer> getDistanceFunction) {
            AtomicInteger minDist = new AtomicInteger(-1);
            this.doorNode.edgesIn.forEach(edge -> {
                DoorNode start = edge.getStart();
                if(start.isRemoved()) return;
                int min = minDist.get();
                int dist = getDistanceFunction.apply(start.getDistanceInfo());
                if(dist != -1 && (min == -1 || dist < min)) {
                    minDist.set(dist);
                }
            });
            int min = minDist.get();
            if(min == -1) {
                // invalid
                return -1;
            } else if(min > 64) {
                // really far // TODO consider tweaking this
                return 64;
            } else {
                return min + 1;
            }
        }

        public @Nullable Room getParentRoom() {
            return this.doorNode.getDoor().getParentRoom();
        }

        public void updateNeighbours() {
            this.doorNode.forEachNeighbour(DoorNode::update);
        }

        public int getDistanceFromEntrance() {
            return distanceFromEntrance;
        }
    }
}
