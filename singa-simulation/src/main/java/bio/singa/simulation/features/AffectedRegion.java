package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.simulation.features.model.RegionFeature;
import bio.singa.simulation.model.sections.CellRegion;

/**
 * @author cl
 */
public class AffectedRegion extends RegionFeature {

    public AffectedRegion(CellRegion region) {
        super(region);
    }

    public static Builder of(CellRegion quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<CellRegion, AffectedRegion, Builder> {

        public Builder(CellRegion quantity) {
            super(quantity);
        }

        @Override
        protected AffectedRegion createObject(CellRegion quantity) {
            return new AffectedRegion(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }

}
