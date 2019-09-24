package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;

import java.util.List;

/**
 * @author cl
 */
public class BlackListVesicleStates extends MultiStringFeature {

    public BlackListVesicleStates(List<String> vesicleStates, List<Evidence> evidence) {
        super(vesicleStates, evidence);
    }

    public BlackListVesicleStates(List<String> vesicleStates, Evidence evidence) {
        super(vesicleStates, evidence);
    }

    public BlackListVesicleStates(List<String> vesicleStates) {
        super(vesicleStates);
    }

}
