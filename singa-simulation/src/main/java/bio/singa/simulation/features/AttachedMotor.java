package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.EntityFeature;
import bio.singa.features.model.Evidence;

/**
 * @author cl
 */
public class AttachedMotor extends EntityFeature {

    private static final String SYMBOL = "e_Motor";

    public AttachedMotor(ChemicalEntity entity, Evidence featureOrigin) {
        super(entity, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
