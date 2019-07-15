package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class Cargo extends EntityFeature {

    public Cargo(ChemicalEntity entity, List<Evidence> evidence) {
        super(entity, evidence);
    }

    public Cargo(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    public Cargo(ChemicalEntity entity) {
        super(entity);
    }

}
