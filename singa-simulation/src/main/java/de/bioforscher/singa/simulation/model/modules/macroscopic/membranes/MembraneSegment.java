package de.bioforscher.singa.simulation.model.modules.macroscopic.membranes;

import de.bioforscher.singa.mathematics.geometry.edges.LineSegment;
import de.bioforscher.singa.mathematics.vectors.Vector2D;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;

/**
 * @author cl
 */
public class MembraneSegment implements LineSegment {

    private AutomatonNode node;
    private LineSegment segment;

    public MembraneSegment(AutomatonNode node, LineSegment segment) {
        this.node = node;
        this.segment = segment;
        node.addMembraneSegment(this);
    }

    public AutomatonNode getNode() {
        return node;
    }

    public LineSegment getSegment() {
        return segment;
    }

    @Override
    public Vector2D getStartingPoint() {
        return segment.getStartingPoint();
    }

    @Override
    public Vector2D getEndingPoint() {
        return segment.getEndingPoint();
    }

    @Override
    public void setStartingPoint(Vector2D startingPoint) {
        segment.setStartingPoint(startingPoint);
    }

    @Override
    public void setEndingPoint(Vector2D endingPoint) {
        segment.setEndingPoint(endingPoint);
    }
}
