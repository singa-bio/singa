package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.DetachmentProbability;
import bio.singa.simulation.features.RequiredVesicleState;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author cl
 */
public class LineLikeAgentDetachment extends QualitativeModule {

    private List<Vesicle> detachingVesicles;

    public LineLikeAgentDetachment() {
        detachingVesicles = new ArrayList<>();
        // features
        getRequiredFeatures().add(DetachmentProbability.class);
        getRequiredFeatures().add(RequiredVesicleState.class);
        getRequiredFeatures().add(AppliedVesicleState.class);
    }

    @Override
    public void calculateUpdates() {
        String vesicleState = getFeature(RequiredVesicleState.class).getContent();
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            // continue if state does not match
            if (!vesicle.getState().equals(vesicleState)) {
                continue;
            }
            if (detachmentEventHappened()) {
                detachingVesicles.add(vesicle);
            }
        }
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    private boolean detachmentEventHappened() {
        return ThreadLocalRandom.current().nextDouble() < getFeature(DetachmentProbability.class).getScaledQuantity();
    }

    @Override
    public void optimizeTimeStep() {
        // FIXME if probability get too large slow time
    }

    @Override
    public void onReset() {
        detachingVesicles.clear();
    }

    @Override
    public void onCompletion() {
        String vesicleState = getFeature(AppliedVesicleState.class).getContent();
        for (Vesicle detachingVesicle : detachingVesicles) {
            detachingVesicle.clearAttachmentInformation();
            detachingVesicle.setState(vesicleState);
        }
    }

}
