package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.ContainmentRegion;
import bio.singa.simulation.features.InitialConcentrations;
import bio.singa.simulation.features.WhiteListVesicleStates;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.concentrations.InitialConcentration;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author cl
 */
public class ConcentrationApplier extends QualitativeModule {

    private static final Logger logger = LoggerFactory.getLogger(ConcentrationApplier.class);

    private List<Vesicle> containedVesicles;

    public ConcentrationApplier() {
        containedVesicles = new ArrayList<>();
        // features
        getRequiredFeatures().add(ContainmentRegion.class);
        getRequiredFeatures().add(WhiteListVesicleStates.class);
        getRequiredFeatures().add(AppliedVesicleState.class);
        getRequiredFeatures().add(InitialConcentrations.class);
    }

    @Override
    public void calculateUpdates() {
        // determine region
        VolumeLikeAgent agent = getFeature(ContainmentRegion.class).retrieveAreaAgent(simulation);
        // get black listed states
        List<String> whiteLitsStates = getFeature(WhiteListVesicleStates.class).getContent();
        // set containment
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            // ignore vesicles which are not on white list
            if (!whiteLitsStates.contains(vesicle.getState())) {
                continue;
            }
            if (agent.getArea().containsVector(vesicle.getPosition())) {
                containedVesicles.add(vesicle);
            }
        }
        // don't forget to set state
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    @Override
    public void optimizeTimeStep() {
        // nothing to do
    }

    @Override
    public void onReset() {
        containedVesicles.clear();
    }

    @Override
    public void onCompletion() {
        String state = getFeature(AppliedVesicleState.class).getContent();
        List<InitialConcentration> concentrations = getFeature(InitialConcentrations.class).getContent();

        for (Vesicle vesicle : containedVesicles) {
            vesicle.setState(state);
            // FIXME this could be mor elegant
            vesicle.getConcentrationManager().setOriginalConcentrations(vesicle.getConcentrationManager().getOriginalConcentrations().emptyCopy());
            vesicle.getConcentrationManager().setConcentrationContainer(vesicle.getConcentrationManager().getOriginalConcentrations().emptyCopy());
            vesicle.getConcentrationManager().clearPotentialDeltas();
            for (InitialConcentration initialConcentration : concentrations) {
                initialConcentration.apply(vesicle);
            }
        }

    }
}
