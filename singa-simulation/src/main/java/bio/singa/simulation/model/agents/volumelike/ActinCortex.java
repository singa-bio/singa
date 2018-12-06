package bio.singa.simulation.model.agents.volumelike;

import bio.singa.mathematics.geometry.model.Polygon;
import bio.singa.mathematics.vectors.Vector2D;

/**
 * @author cl
 */
public class ActinCortex extends VolumeLikeAgent {

    public ActinCortex(Polygon area) {
        super(area);
    }

    public double getDesity(Vector2D vector) {
        if (area.isInside(vector)) {
            return 0.0;
        }
        return 1.0;
    }

}
