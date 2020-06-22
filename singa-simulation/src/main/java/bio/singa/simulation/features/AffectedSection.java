package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.model.sections.CellSubsection;

/**
 * @author cl
 */
public class AffectedSection extends QualitativeFeature<CellSubsection> {

    public AffectedSection(CellSubsection subsection) {
        super(subsection);
    }

    public static Builder of(CellSubsection quantity) {
        return new Builder(quantity);
    }

    public static class Builder extends AbstractFeature.Builder<CellSubsection, AffectedSection, Builder> {

        public Builder(CellSubsection quantity) {
            super(quantity);
        }

        @Override
        protected AffectedSection createObject(CellSubsection quantity) {
            return new AffectedSection(quantity);
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }
    }


}
