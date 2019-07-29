package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.simulation.features.AppliedVesicleState;
import bio.singa.simulation.features.BlackListVesicleStates;
import bio.singa.simulation.features.ContainmentRegion;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.volumelike.VolumeLikeAgent;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import bio.singa.simulation.model.sections.CellRegion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * For vesicles state changes upon entering certain regions represented by volume like agents
 *
 * @author cl
 */
public class VolumeLikeAgentContainment extends QualitativeModule {

    private static final Logger logger = LoggerFactory.getLogger(VolumeLikeAgentContainment.class);

    private List<Vesicle> containedVesicles;

    public VolumeLikeAgentContainment() {
        containedVesicles = new ArrayList<>();
        // features
        getRequiredFeatures().add(ContainmentRegion.class);
        getRequiredFeatures().add(BlackListVesicleStates.class);
        getRequiredFeatures().add(AppliedVesicleState.class);
    }

    @Override
    public void calculateUpdates() {
        // determine region
        CellRegion containmentRegion = getFeature(ContainmentRegion.class).getContent();
        VolumeLikeAgent requiredAgent = null;
        for (VolumeLikeAgent agent : simulation.getVolumeLayer().getAgents()) {
            if (agent.getCellRegion().equals(containmentRegion)) {
                requiredAgent = agent;
            }
        }
        if (requiredAgent == null) {
            logger.warn("There exists no region in the simulation matching the given region: " + containmentRegion);
            state = ModuleState.SUCCEEDED;
            return;
        }
        // get black listed states
        List<String> blacklistStates = getFeature(BlackListVesicleStates.class).getContent();
        // set containment
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            // ignore vesicles with blacklisted states
            if (blacklistStates.contains(vesicle.getState())) {
                continue;
            }
            if (requiredAgent.getArea().containsVector(vesicle.getPosition())) {
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
        // set new state
        String state = getFeature(AppliedVesicleState.class).getContent();
        for (Vesicle vesicle : containedVesicles) {
            vesicle.setState(state);
        }
    }
}
