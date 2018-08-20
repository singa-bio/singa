package bio.singa.simulation.features.endocytosis;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.FeatureOrigin;

import java.util.Set;

/**
 * @author cl
 */
public class MatchingQSnares extends AbstractFeature<Set<ChemicalEntity>> {

    private static final String SYMBOL = "es_QSnares";

    public MatchingQSnares(Set<ChemicalEntity> chemicalEntities, FeatureOrigin featureOrigin) {
        super(chemicalEntities, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
}
