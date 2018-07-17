package de.bioforscher.singa.simulation.model.modules.macroscopic.membranes;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MacroscopicMembraneSegment {

    AutomatonNode node;
    List<LineSegment> segments;

    public MacroscopicMembraneSegment(AutomatonNode node) {
        this.node = node;
        segments = new ArrayList<>();
        node.addMembraneSegment(this);
    }

    public AutomatonNode getNode() {
        return node;
    }

    public List<LineSegment> getLineSegments() {
        return segments;
    }

    public void addSegment(LineSegment segment) {
        segments.add(segment);
    }

}
