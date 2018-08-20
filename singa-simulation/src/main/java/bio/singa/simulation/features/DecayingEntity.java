package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.EntityFeature;
import bio.singa.features.model.FeatureOrigin;

/**
 * @author cl
 */
public class DecayingEntity extends EntityFeature {

    private static final String SYMBOL = "e_Decay";

    public DecayingEntity(ChemicalEntity entity, FeatureOrigin featureOrigin) {
        super(entity, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }


}
