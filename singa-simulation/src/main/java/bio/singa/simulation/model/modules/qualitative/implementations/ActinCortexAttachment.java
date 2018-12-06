package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.volumelike.ActinCortex;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;

import java.util.ArrayList;
import java.util.List;


/**
 * @author cl
 */
public class ActinCortexAttachment extends QualitativeModule {

    private List<Vesicle> tetheringVesicles;

    public ActinCortexAttachment() {
        tetheringVesicles = new ArrayList<>();
    }

    @Override
    public void calculateUpdates() {
        ActinCortex cortex = simulation.getVolumeLayer().getCortex();
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            if (!(vesicle.getVesicleState().equals(VesicleStateRegistry.ACTIN_TETHERED)
                    || vesicle.getVesicleState().equals(VesicleStateRegistry.ACTIN_PROPELLED)
                    || vesicle.getVesicleState().equals(VesicleStateRegistry.MEMBRANE_TETHERED))) {
                if (cortex.getArea().isInside(vesicle.getCurrentPosition())) {
                    tetheringVesicles.add(vesicle);
                }
            }
        }
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    @Override
    public void optimizeTimeStep() {
        // nothing to do
    }

    @Override
    public void onReset() {
        tetheringVesicles.clear();
    }

    @Override
    public void onCompletion() {
        tetheringVesicles.forEach(vesicle -> {
            vesicle.setVesicleState(VesicleStateRegistry.ACTIN_TETHERED);
        });
    }
}
