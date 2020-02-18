package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class PrimaryCargoes extends MultiEntityFeature {

    public PrimaryCargoes(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public PrimaryCargoes(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public PrimaryCargoes(ChemicalEntity... entities) {
        super(Arrays.asList(entities));
    }
}
