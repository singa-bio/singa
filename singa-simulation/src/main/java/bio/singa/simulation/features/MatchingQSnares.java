package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class MatchingQSnares extends MultiEntityFeature {

    public MatchingQSnares(List<ChemicalEntity> chemicalEntities, List<Evidence> evidence) {
        super(chemicalEntities, evidence);
    }

    public MatchingQSnares(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public MatchingQSnares(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public MatchingQSnares(ChemicalEntity chemicalEntity, Evidence evidence) {
        super(Collections.singletonList(chemicalEntity), evidence);
    }

    public MatchingQSnares(ChemicalEntity chemicalEntity) {
        super(Collections.singletonList(chemicalEntity));
    }

}
