package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

import phanastrae.mirthdew_encore.dreamtwirl.stage.design.StageDesignData;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.ParentedRoomDoor;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.Room;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class DoorNode {

    private final ParentedRoomDoor door;
    private final DistanceInfo distanceInfo = new DistanceInfo(this);

    public DoorNode(ParentedRoomDoor door) {
        this.door = door;
    }

    public ParentedRoomDoor getParentedDoor() {
        return door;
    }

    public RoomDoor.DoorType getDoorType() {
        return this.door.getDoor().getDoorType();
    }

    public DistanceInfo getDistanceInfo() {
        return distanceInfo;
    }

    public void update(StageDesignData designData, RoomGraph roomGraph) {
        this.distanceInfo.update(designData, roomGraph);
    }

    public static class DistanceInfo {
        private final DoorNode doorNode;
        private int distanceFromEntrance = -1;

        public DistanceInfo(DoorNode doorNode) {
            this.doorNode = doorNode;
        }

        public void update(StageDesignData designData, RoomGraph roomGraph) {
            boolean updated = false;

            Room room = this.doorNode.getParentedDoor().getRoom();

            boolean isDreamtwirlEntrance = room.getRoomType().isEntrance();
            int distanceFromEntrance = calcDistanceFromEntrance(roomGraph, isDreamtwirlEntrance);

            if(this.distanceFromEntrance != distanceFromEntrance) {
                this.distanceFromEntrance = distanceFromEntrance;
                updated = true;
            }

            if(updated) {
                this.updateNeighbours(designData, roomGraph);
            }
        }

        public int calcDistanceFromEntrance(RoomGraph roomGraph, boolean isDreamtwirlEntrance) {
            if(isDreamtwirlEntrance) {
                return 1;
            } else {
                return this.calcDistance(roomGraph, DistanceInfo::getDistanceFromEntrance);
            }
        }

        public int calcDistance(RoomGraph roomGraph, Function<DistanceInfo, Integer> getDistanceFunction) {
            AtomicInteger minDist = new AtomicInteger(-1);
            roomGraph.forEachConnectedStartpoint(this.getRoomDoor().getRoomDoorId(), startNode -> {
                //if(startNode.isRemoved()) return;
                int min = minDist.get();
                //int dist = getDistanceFunction.apply(startNode.getDistanceInfo());
                int dist = 0; // TODO work out what is happening here
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

        public void updateNeighbours(StageDesignData designData, RoomGraph roomGraph) {
            // TODO work this out
            //roomGraph.forEachConnectedStartpoint(this.getRoomDoor(), startNode -> startNode.update(roomGraph));
            //roomGraph.forEachConnectedEndpoint(this.getRoomDoor(), endNode -> endNode.update(roomGraph));
        }

        public RoomDoor getRoomDoor() {
            return this.doorNode.door.getDoor();
        }

        public int getDistanceFromEntrance() {
            return distanceFromEntrance;
        }
    }
}
