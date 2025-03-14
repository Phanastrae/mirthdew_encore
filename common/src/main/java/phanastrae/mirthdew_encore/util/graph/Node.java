package phanastrae.mirthdew_encore.util.graph;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Node {

    private final long id;
    private @Nullable List<Long> outgoingEdges;
    private @Nullable List<Long> incomingEdges;

    public Node(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void addOutgoingEdge(long id) {
        if(this.outgoingEdges == null) {
            this.outgoingEdges = new ObjectArrayList<>();
        }
        if(!this.outgoingEdges.contains(id)) {
            this.outgoingEdges.add(id);
        }
    }

    public void addIncomingEdge(long id) {
        if(this.incomingEdges == null) {
            this.incomingEdges = new ObjectArrayList<>();
        }
        if(!this.incomingEdges.contains(id)) {
            this.incomingEdges.add(id);
        }
    }

    public void removeOutgoingEdge(long id) {
        if(this.outgoingEdges != null) {
            this.outgoingEdges.remove(id);
        }
    }

    public void removeIncomingEdge(long id) {
        if(this.incomingEdges != null) {
            this.incomingEdges.remove(id);
        }
    }

    public @Nullable List<Long> getOutgoingEdges() {
        return outgoingEdges;
    }

    public @Nullable List<Long> getIncomingEdges() {
        return incomingEdges;
    }

    public boolean isEmpty() {
        return (this.incomingEdges == null || this.incomingEdges.isEmpty()) && (this.outgoingEdges == null || this.outgoingEdges.isEmpty());
    }
}
