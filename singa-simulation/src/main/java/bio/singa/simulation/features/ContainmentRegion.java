package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.List;

/**
 * @author cl
 */
public class ContainmentRegion extends RegionFeature {

    public ContainmentRegion(CellRegion region, List<Evidence> evidence) {
        super(region, evidence);
    }

    public ContainmentRegion(CellRegion region, Evidence evidence) {
        super(region, evidence);
    }

    public ContainmentRegion(CellRegion region) {
        super(region);
    }

}
