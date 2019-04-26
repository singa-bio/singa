package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class AttachedMotor extends EntityFeature {

    public AttachedMotor(ChemicalEntity entity, List<Evidence> evidence) {
        super(entity, evidence);
    }

    public AttachedMotor(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    public AttachedMotor(ChemicalEntity entity) {
        super(entity);
    }
}
