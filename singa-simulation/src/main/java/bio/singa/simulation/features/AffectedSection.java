package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.model.sections.CellSubsection;

import java.util.List;

/**
 * @author cl
 */
public class AffectedSection extends QualitativeFeature<CellSubsection> {

    public AffectedSection(CellSubsection subsection, List<Evidence> evidence) {
        super(subsection, evidence);
    }

    public AffectedSection(CellSubsection subsection, Evidence evidence) {
        super(subsection, evidence);
    }

    public AffectedSection(CellSubsection subsection) {
        super(subsection);
    }
}
