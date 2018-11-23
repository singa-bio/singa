package bio.singa.simulation.model.agents.surfacelike;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.mathematics.geometry.edges.LineSegment;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.graphs.AutomatonNode;

import javax.measure.Quantity;
import javax.measure.quantity.Area;

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

    public Quantity<Area> getArea() {
        return Environment.convertSimulationToSystemScale(segment.getLength())
                .multiply(UnitRegistry.getSpace())
                .asType(Area.class);
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
