package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.dreamtwirl.stage.design.room.RoomDoor;

import java.util.Map;
import java.util.Optional;

public class DreamtwirlDebug {
    private static DreamtwirlDebug INSTANCE = new DreamtwirlDebug();
    private DreamtwirlDebug(){};
    public static DreamtwirlDebug getInstance() {
        return INSTANCE;
    }

    private final Map<Long, DebugInfo> debugInfoMap = new Object2ObjectOpenHashMap<>();

    public void addDebugInfo(DebugInfo debugInfo) {
        long id = debugInfo.dreamtwirlId;
        this.debugInfoMap.remove(id);
        this.debugInfoMap.put(id, debugInfo);
    }

    public Optional<DebugInfo> getDebugInfo(long id) {
        if(this.debugInfoMap.containsKey(id)) {
            return Optional.of(this.debugInfoMap.get(id));
        } else {
            return Optional.empty();
        }
    }

    public static class DebugInfo {

        private final long dreamtwirlId;
        private final DebugNode[] nodes;
        private final DebugEdge[] edges;

        public DebugInfo(long dreamtwirlId, DebugNode[] nodes, DebugEdge[] edges) {
            this.dreamtwirlId = dreamtwirlId;
            this.nodes = nodes;
            this.edges = edges;
        }

        @Nullable
        public DebugNode getNodeOfId(int id) {
            if(0 <= id && id < nodes.length) {
                return nodes[id];
            } else {
                return null;
            }
        }

        public static DebugInfo read(FriendlyByteBuf buf) {
            long dreamtwirlId = buf.readLong();

            int nodeCount = buf.readInt();
            DebugNode[] nodes = new DebugNode[nodeCount];
            for(int i = 0; i < nodeCount; i++) {
                DebugNode node = DebugNode.read(buf);
                if(0 <= node.id && node.id < nodeCount) {
                    nodes[node.id] = node;
                }
            }

            int edgeCount = buf.readInt();
            DebugEdge[] edges = new DebugEdge[edgeCount];
            for(int i = 0; i < edgeCount; i++) {
                DebugEdge edge = DebugEdge.read(buf);
                edges[i] = edge;
            }

            return new DebugInfo(dreamtwirlId, nodes, edges);
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeLong(this.dreamtwirlId);

            buf.writeInt(this.nodes.length);
            for(DebugNode node : this.nodes) {
                node.write(buf);
            }
            buf.writeInt(this.edges.length);
            for(DebugEdge edge : this.edges) {
                edge.write(buf);
            }
        }

        public long getDreamtwirlId() {
            return dreamtwirlId;
        }

        public DebugNode[] getNodes() {
            return nodes;
        }

        public DebugEdge[] getEdges() {
            return edges;
        }
    }

    public record DebugNode(int id, BlockPos pos, RoomDoor.DoorType doorType) {

        public static DebugNode read(FriendlyByteBuf buf) {
            return new DebugNode(buf.readInt(), buf.readBlockPos(), buf.readEnum(RoomDoor.DoorType.class));
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(this.id);
            buf.writeBlockPos(this.pos);
            buf.writeEnum(this.doorType);
        }
    }

    public record DebugEdge(int startId, int endId) {

        public static DebugEdge read(FriendlyByteBuf buf) {
            return new DebugEdge(buf.readInt(), buf.readInt());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(this.startId);
            buf.writeInt(this.endId);
        }
    }
}
