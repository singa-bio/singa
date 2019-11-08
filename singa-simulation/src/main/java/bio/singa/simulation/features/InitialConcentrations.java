package bio.singa.simulation.features;

import bio.singa.features.model.AbstractFeature;
import bio.singa.features.model.Evidence;
import bio.singa.simulation.model.concentrations.InitialConcentration;

import java.util.List;

/**
 * @author cl
 */
public class InitialConcentrations extends AbstractFeature<List<InitialConcentration>> {

    public InitialConcentrations(List<InitialConcentration> initialConcentrations, List<Evidence> evidence) {
        super(initialConcentrations, evidence);
    }

    public InitialConcentrations(List<InitialConcentration> initialConcentrations, Evidence evidence) {
        super(initialConcentrations, evidence);
    }

    public InitialConcentrations(List<InitialConcentration> initialConcentrations) {
        super(initialConcentrations);
    }

}
