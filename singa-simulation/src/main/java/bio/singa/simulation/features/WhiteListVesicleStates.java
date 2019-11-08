package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class WhiteListVesicleStates extends MultiStringFeature {

    public WhiteListVesicleStates(List<String> vesicleStates, List<Evidence> evidence) {
        super(vesicleStates, evidence);
    }

    public WhiteListVesicleStates(List<String> vesicleStates, Evidence evidence) {
        super(vesicleStates, evidence);
    }

    public WhiteListVesicleStates(List<String> vesicleStates) {
        super(vesicleStates);
    }

}
