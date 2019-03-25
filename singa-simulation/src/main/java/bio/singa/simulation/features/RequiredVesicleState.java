package bio.singa.simulation.features;

import bio.singa.features.model.Evidence;
import bio.singa.features.model.StringFeature;

import java.util.List;

/**
 * @author cl
 */
public class RequiredVesicleState extends StringFeature{

        public RequiredVesicleState(String vesicleState, List<Evidence> evidence) {
            super(vesicleState, evidence);
        }

        public RequiredVesicleState(String vesicleState, Evidence evidence) {
            super(vesicleState, evidence);
        }

        public RequiredVesicleState(String vesicleState) {
            super(vesicleState);
        }

}
