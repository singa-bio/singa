package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;

import java.util.List;

import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.VesicleState;

/**
 * @author cl
 */
public class AppliedVesicleState extends QualitativeFeature<VesicleState> {

    public AppliedVesicleState(VesicleState vesicleState, List<Evidence> evidence) {
        super(vesicleState, evidence);
    }

    public AppliedVesicleState(VesicleState vesicleState, Evidence evidence) {
        super(vesicleState, evidence);
    }

    public AppliedVesicleState(VesicleState vesicleState) {
        super(vesicleState);
    }

}
