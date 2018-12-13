package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.chemistry.features.EntityFeature;
import bio.singa.features.model.Evidence;

/**
 * @author cl
 */
public class Cargo extends EntityFeature {

    private static final String SYMBOL = "e_Cargo";

    public Cargo(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }

}
