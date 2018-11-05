package bio.singa.simulation.features;

import bio.singa.chemistry.MultiEntityFeature;
import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Set;

/**
 * @author cl
 */
public class Solutes extends MultiEntityFeature {

    private static final String SYMBOL = "es_Solutes";

    public Solutes(Set<ChemicalEntity> chemicalEntities, Evidence featureOrigin) {
        super(chemicalEntities, featureOrigin);
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

}
