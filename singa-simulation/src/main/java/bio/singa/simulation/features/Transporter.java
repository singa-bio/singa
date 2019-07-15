package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class Transporter extends EntityFeature {

    public Transporter(ChemicalEntity entity, List<Evidence> evidence) {
        super(entity, evidence);
    }

    public Transporter(ChemicalEntity entity, Evidence evidence) {
        super(entity, evidence);
    }

    public Transporter(ChemicalEntity entity) {
        super(entity);
    }

}
