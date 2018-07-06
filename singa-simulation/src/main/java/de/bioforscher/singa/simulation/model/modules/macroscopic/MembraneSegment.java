package de.bioforscher.singa.simulation.model.modules.macroscopic;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class MembraneSegment {

    AutomatonNode node;
    List<LineSegment> segments;

    public MembraneSegment(AutomatonNode node) {
        this.node = node;
        segments = new ArrayList<>();
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
