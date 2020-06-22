package bio.singa.simulation.features.model;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.model.sections.CellRegion;

import java.util.List;

/**
 * @author cl
 */
public abstract class RegionFeature extends QualitativeFeature<CellRegion> {

    public RegionFeature(CellRegion region, List<Evidence> evidence) {
        super(region, evidence);
    }

    public RegionFeature(CellRegion region, Evidence evidence) {
        super(region, evidence);
    }

    public RegionFeature(CellRegion region) {
        super(region);
    }

}
