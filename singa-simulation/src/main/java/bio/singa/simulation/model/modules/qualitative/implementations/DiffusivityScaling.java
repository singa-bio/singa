package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.ModifiedDiffusivity;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;

import javax.measure.Quantity;
import java.util.ArrayList;
import java.util.List;

import static bio.singa.simulation.model.modules.concentration.ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;

/**
 * @author cl
 */
public class DiffusivityScaling extends QualitativeModule {

    List<Vesicle> storedVesicles;

    public DiffusivityScaling() {
        storedVesicles = new ArrayList<>();
        // features
        getRequiredFeatures().add(ModifiedDiffusivity.class);
        getRequiredFeatures().add(AppliedVesicleState.class);
    }

    @Override
    public void calculateUpdates() {
        String vesicleState = getFeature(AppliedVesicleState.class).getContent();
        double scaledDiffusivity = getScaledFeature(ModifiedDiffusivity.class);
        for (Vesicle vesicle : getSimulation().getVesicleLayer().getVesicles()) {
            if (vesicle.getState().equals(vesicleState)) {
                if (scaledDiffusivity != vesicle.getFeature(Diffusivity.class).getScaledQuantity()) {
                    storedVesicles.add(vesicle);
                }
            }
        }
        setState(SUCCEEDED_WITH_PENDING_CHANGES);
    }

    @Override
    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        storedVesicles.clear();
    }

    @Override
    public void onCompletion() {
        Quantity<Diffusivity> diffusivity = getFeature(ModifiedDiffusivity.class).getContent();
        for (Vesicle storedVesicle : storedVesicles) {
            storedVesicle.getFeature(Diffusivity.class).setContent(diffusivity);
        }
    }
}
