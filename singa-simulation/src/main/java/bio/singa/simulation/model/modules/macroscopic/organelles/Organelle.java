package bio.singa.simulation.model.modules.macroscopic.organelles;

import bio.singa.mathematics.geometry.faces.LineSegmentPolygon;
import bio.singa.simulation.model.sections.CellRegion;

/**
 * @author cl
 */
public class Organelle {

    private final CellRegion correspondingRegion;
    private final LineSegmentPolygon polygon;
    private final double scale;

    public Organelle(CellRegion correspondingRegion, LineSegmentPolygon polygon, double scale) {
        this.correspondingRegion = correspondingRegion;
        this.polygon = polygon;
        this.scale = scale;
    }

}
