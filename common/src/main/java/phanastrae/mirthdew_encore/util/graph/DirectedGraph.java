package phanastrae.mirthdew_encore.util.graph;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import phanastrae.mirthdew_encore.MirthdewEncore;

import java.util.List;
import java.util.Map;

public class DirectedGraph {
    public static final Codec<DirectedGraph> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.LONG.listOf().fieldOf("nodes").forGetter(graph -> graph.getNodes().values().stream().filter(node -> !node.isEmpty()).map(Node::getId).toList()),
                            DirectedEdge.CODEC.listOf().fieldOf("edges").forGetter(graph -> graph.getEdges().values().stream().toList())
                    )
                    .apply(instance, DirectedGraph::new)
    );

    private Map<Long, Node> nodes = new Object2ObjectOpenHashMap<>();
    private Map<Long, DirectedEdge> edges = new Object2ObjectOpenHashMap<>();

    public DirectedGraph() {
    }

    public DirectedGraph(List<Long> nodes, List<DirectedEdge> edges) {
        for(Long l : nodes) {
            this.nodes.put(l, new Node(l));
        }
        for(DirectedEdge edge : edges) {
            long eid = edge.getId();

            Node start = this.getOrCreateNode(edge.getStartId());
            Node end = this.getOrCreateNode(edge.getEndId());

            start.addOutgoingEdge(eid);
            end.addIncomingEdge(eid);

            this.edges.put(eid, edge);
        }
    }

    public Node getOrCreateNode(long id) {
        if(this.nodes.containsKey(id)) {
            return this.nodes.get(id);
        } else {
            Node node = new Node(id);
            this.nodes.put(id, node);
            return node;
        }
    }

    public @Nullable Node getNode(long id) {
        return this.nodes.getOrDefault(id, null);
    }

    public @Nullable DirectedEdge getEdge(long id) {
        return this.edges.getOrDefault(id, null);
    }

    public void addEdge(long start, long end, RandomSource random) {
        long edgeId = this.getNextEdgeId(random);

        DirectedEdge edge = new DirectedEdge(edgeId, start, end);
        this.edges.put(edgeId, edge);

        Node s = this.getOrCreateNode(start);
        s.addOutgoingEdge(edgeId);
        Node e = this.getOrCreateNode(end);
        e.addIncomingEdge(edgeId);
    }

    public long getNextNodeId(RandomSource random) {
        for(int i = 0; i < 100; i++) {
            long id = random.nextLong();
            if (!this.edges.containsKey(id)) {
                return id;
            }
        }
        MirthdewEncore.LOGGER.error("Somehow failed to generate a new node id 100 times in a row?????? Assigning a random id");
        return random.nextLong();
    }

    public long getNextEdgeId(RandomSource random) {
        for(int i = 0; i < 100; i++) {
            long id = random.nextLong();
            if (!this.edges.containsKey(id)) {
                return id;
            }
        }
        MirthdewEncore.LOGGER.error("Somehow failed to generate a new edge id 100 times in a row?????? Assigning a random id");
        return random.nextLong();
    }

    public void removeNode(long id) {
        Node node = this.getNode(id);
        if(node != null) {
            List<Long> out = node.getOutgoingEdges();
            if(out != null) {
                for(long edgeId : out) {
                    this.removeEdge(edgeId, id);
                }
            }

            List<Long> in = node.getIncomingEdges();
            if(in != null) {
                for(long edgeId : in) {
                    this.removeEdge(edgeId, id);
                }
            }
        }

        this.nodes.remove(id);
    }

    public void removeEdge(long id, long exceptNode) {
        DirectedEdge edge = this.getEdge(id);
        if(edge != null) {
            if(edge.getStartId() != exceptNode) {
                Node start = this.getNode(edge.getStartId());
                if (start != null) {
                    start.removeOutgoingEdge(id);
                    if (start.isEmpty()) {
                        this.nodes.remove(start.getId());
                    }
                }
            }

            if(edge.getEndId() != exceptNode) {
                Node end = this.getNode(edge.getEndId());
                if (end != null) {
                    end.removeIncomingEdge(id);
                    if (end.isEmpty()) {
                        this.nodes.remove(end.getId());
                    }
                }
            }

            this.edges.remove(id);
        }
    }

    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public Map<Long, DirectedEdge> getEdges() {
        return edges;
    }
}
