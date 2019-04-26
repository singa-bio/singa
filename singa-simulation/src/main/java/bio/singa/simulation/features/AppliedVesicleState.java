package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.List;

/**
 * @author cl
 */
public class AppliedVesicleState extends StringFeature {

    public AppliedVesicleState(String vesicleState, List<Evidence> evidence) {
        super(vesicleState, evidence);
    }

    public AppliedVesicleState(String vesicleState, Evidence evidence) {
        super(vesicleState, evidence);
    }

    public AppliedVesicleState(String vesicleState) {
        super(vesicleState);
    }

}
