package phanastrae.mirthdew_encore.dreamtwirl.stage;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
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
        private final List<DebugNode> nodes;
        private final List<DebugEdge> edges;
        private Map<Integer, DebugNode> nodeIdMap = new Object2ObjectOpenHashMap<>();

        public DebugInfo(long dreamtwirlId, List<DebugNode> nodes, List<DebugEdge> edges) {
            this.dreamtwirlId = dreamtwirlId;
            this.nodes = nodes;
            this.edges = edges;
            this.nodes.forEach(node -> this.nodeIdMap.put(node.id, node));
        }

        @Nullable
        public DebugNode getNodeOfId(int id) {
            return this.nodeIdMap.getOrDefault(id, null);
        }

        public static DebugInfo read(FriendlyByteBuf buf) {
            long dreamtwirlId = buf.readLong();

            int nodeCount = buf.readInt();
            List<DebugNode> nodes = new ArrayList<>(nodeCount);
            for(int i = 0; i < nodeCount; i++) {
                nodes.add(DebugNode.read(buf));
            }

            int edgeCount = buf.readInt();
            List<DebugEdge> edges = new ArrayList<>(edgeCount);
            for(int i = 0; i < edgeCount; i++) {
                edges.add(DebugEdge.read(buf));
            }

            return new DebugInfo(dreamtwirlId, nodes, edges);
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeLong(this.dreamtwirlId);

            buf.writeInt(this.nodes.size());
            for(DebugNode node : this.nodes) {
                node.write(buf);
            }
            buf.writeInt(this.edges.size());
            for(DebugEdge edge : this.edges) {
                edge.write(buf);
            }
        }

        public long getDreamtwirlId() {
            return dreamtwirlId;
        }

        public List<DebugNode> getNodes() {
            return nodes;
        }

        public List<DebugEdge> getEdges() {
            return edges;
        }
    }

    public record DebugNode(int id, BlockPos pos) {

        public static DebugNode read(FriendlyByteBuf buf) {
            return new DebugNode(buf.readInt(), buf.readBlockPos());
        }

        public void write(FriendlyByteBuf buf) {
            buf.writeInt(this.id);
            buf.writeBlockPos(this.pos);
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
