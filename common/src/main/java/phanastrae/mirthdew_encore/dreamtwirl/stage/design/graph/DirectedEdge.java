package phanastrae.mirthdew_encore.dreamtwirl.stage.design.graph;

public class DirectedEdge {

    private final DoorNode start;
    private final DoorNode end;

    private boolean attached = false;

    public DirectedEdge(DoorNode start, DoorNode end) {
        this.start = start;
        this.end = end;
    }

    public void attachToNodes() {
        if(!this.attached) {
            this.start.addEdgeOut(this);
            this.end.addEdgeIn(this);
            this.attached = true;
        }
    }

    public void detachFromNodes() {
        if(this.attached) {
            this.start.removeEdgeOut(this);
            this.end.removeEdgeIn(this);
            this.attached = false;
        }
    }

    public DoorNode getStart() {
        return start;
    }

    public DoorNode getEnd() {
        return end;
    }
}
