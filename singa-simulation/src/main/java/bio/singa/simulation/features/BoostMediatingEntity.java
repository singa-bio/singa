package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class BoostMediatingEntity extends EntityFeature {

    public BoostMediatingEntity(ChemicalEntity entity, List<Evidence> evidence) {
        super(entity, evidence);
    }

    public BoostMediatingEntity(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    public BoostMediatingEntity(ChemicalEntity entity) {
        super(entity);
    }

}
