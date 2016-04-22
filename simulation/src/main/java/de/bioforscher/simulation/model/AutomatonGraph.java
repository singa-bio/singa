package de.bioforscher.simulation.model;

import de.bioforscher.mathematics.graphs.model.AbstractGraph;

public class AutomatonGraph extends AbstractGraph<BioNode, BioEdge> {

    public AutomatonGraph() {

    }

    public AutomatonGraph(int nodeCapacity, int edgeCapacity) {
        super(nodeCapacity, edgeCapacity);
    }

    public void connect(int identifier, BioNode source, BioNode target) {
        super.connect(identifier, source, target, BioEdge.class);
    }

}
