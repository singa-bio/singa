package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.QualitativeFeature;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.VesicleState;

import java.util.List;

/**
 * @author cl
 */
public class BlackListVesicleStates  extends QualitativeFeature<List<VesicleState>> {

    public BlackListVesicleStates(List<VesicleState> vesicleStates, List<Evidence> evidence) {
        super(vesicleStates, evidence);
    }

    public BlackListVesicleStates(List<VesicleState> vesicleStates, Evidence evidence) {
        super(vesicleStates, evidence);
    }

    public BlackListVesicleStates(List<VesicleState> vesicleStates) {
        super(vesicleStates);
    }

}
