package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class Cargoes extends MultiEntityFeature {

    private static final String SYMBOL = "es_Cargoes";

    public Cargoes(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public Cargoes(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public Cargoes(ChemicalEntity... entities) {
        super(Arrays.asList(entities));
    }

    @Override
    public String getDescriptor() {
        return SYMBOL;
    }
}
