package bio.singa.simulation.model.agents.volumelike;

import bio.singa.mathematics.geometry.model.Polygon;

/**
 * @author cl
 */
public abstract class VolumeLikeAgent {

    protected Polygon area;

    public VolumeLikeAgent(Polygon area) {
        this.area = area;
    }

    public Polygon getArea() {
        return area;
    }

    public void setArea(Polygon area) {
        this.area = area;
    }



}
