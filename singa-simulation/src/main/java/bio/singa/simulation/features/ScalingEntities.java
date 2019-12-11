package bio.singa.simulation.features;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Evidence;

import java.util.Arrays;
import java.util.List;

/**
 * @author cl
 */
public class ScalingEntities extends MultiEntityFeature {

    public ScalingEntities(List<ChemicalEntity> chemicalEntities, Evidence evidence) {
        super(chemicalEntities, evidence);
    }

    public ScalingEntities(List<ChemicalEntity> chemicalEntities) {
        super(chemicalEntities);
    }

    public ScalingEntities(ChemicalEntity... entities) {
        super(Arrays.asList(entities));
    }

}
