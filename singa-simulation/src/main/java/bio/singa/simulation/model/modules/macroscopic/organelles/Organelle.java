package bio.singa.simulation.model.modules.macroscopic.organelles;

import bio.singa.features.parameters.Environment;
import bio.singa.mathematics.geometry.faces.LineSegmentPolygon;
import bio.singa.simulation.model.sections.CellRegion;

import javax.measure.Quantity;
import javax.measure.quantity.Length;

/**
 * @author cl
 */
public class Organelle {

    private final CellRegion internalRegion;
    private final CellRegion membraneRegion;
    private final Quantity<Length> scale;
    private LineSegmentPolygon polygon;

    public Organelle(CellRegion internalRegion, CellRegion membraneRegion, LineSegmentPolygon polygon, Quantity<Length> scale) {
        this.internalRegion = internalRegion;
        this.membraneRegion = membraneRegion;
        this.scale = scale;
        this.polygon = polygon;
        mapToSystemExtend();
    }

    private void mapToSystemExtend() {
        Quantity<Length> systemScale = Environment.convertSimulationToSystemScale(1).to(scale.getUnit());
        polygon.scale(scale.getValue().doubleValue()/systemScale.getValue().doubleValue());
    }

    public CellRegion getInternalRegion() {
        return internalRegion;
    }

    public CellRegion getMembraneRegion() {
        return membraneRegion;
    }

    public LineSegmentPolygon getPolygon() {
        return polygon;
    }
}
