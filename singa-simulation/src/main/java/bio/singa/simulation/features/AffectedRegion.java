package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.List;

/**
 * @author cl
 */
public class AffectedRegion extends RegionFeature {

    public AffectedRegion(CellRegion region, List<Evidence> evidence) {
        super(region, evidence);
    }

    public AffectedRegion(CellRegion region, Evidence evidence) {
        super(region, evidence);
    }

    public AffectedRegion(CellRegion region) {
        super(region);
    }
}
