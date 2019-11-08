package bio.singa.simulation.model.concentrations;

import bio.singa.simulation.model.sections.CellRegion;
import bio.singa.simulation.model.simulation.Updatable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class RegionCondition extends AbstractConcentrationCondition {

    public static RegionCondition forRegions(List<CellRegion> identifiers) {
        return new RegionCondition(identifiers);
    }

    public static RegionCondition forRegion(CellRegion identifier) {
        return forRegions(Collections.singletonList(identifier));
    }

    public static RegionCondition forRegions(CellRegion... identifiers) {
        return forRegions(Arrays.asList(identifiers));
    }

    private List<CellRegion> regions;

    public RegionCondition(List<CellRegion> regions) {
        super(20);
        this.regions = regions;
    }

    public List<CellRegion> getRegions() {
        return regions;
    }

    @Override
    public boolean test(Updatable updatable) {
        return regions.contains(updatable.getCellRegion());
    }

    @Override
    public String toString() {
        return "updatable is one of the regions: " + regions;
    }


}
