package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Collections;
import java.util.List;

/**
 * @author cl
 */
public class MatchingRSnares extends MultiEntityFeature {

    public MatchingRSnares(List<ChemicalEntity> chemicalEntities, List<Evidence> evidence) {
        super(chemicalEntities, evidence);
    }

    public MatchingRSnares(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public MatchingRSnares(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public MatchingRSnares(ChemicalEntity chemicalEntity, Evidence evidence) {
        super(Collections.singletonList(chemicalEntity), evidence);
    }

    public MatchingRSnares(ChemicalEntity chemicalEntity) {
        super(Collections.singletonList(chemicalEntity));
    }

}
