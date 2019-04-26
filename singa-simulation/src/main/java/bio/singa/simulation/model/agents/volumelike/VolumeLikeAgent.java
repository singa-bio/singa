package bio.singa.simulation.model.agents.volumelike;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.simulation.model.sections.CellRegion;

/**
 * @author cl
 */
public class VolumeLikeAgent {

    private Polygon area;
    private CellRegion cellRegion;

    public VolumeLikeAgent(Polygon area) {
        this.area = area;
    }

    public VolumeLikeAgent(Polygon area, CellRegion cellRegion) {
        this.area = area;
        this.cellRegion = cellRegion;
    }

    public Polygon getArea() {
        return area;
    }

    public void setArea(Polygon area) {
        this.area = area;
    }

    public CellRegion getCellRegion() {
        return cellRegion;
    }

    public void setCellRegion(CellRegion cellRegion) {
        this.cellRegion = cellRegion;
    }

}
