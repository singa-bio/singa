package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.agents.volumelike.ActinCortex;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.List;

import static bio.singa.simulation.features.DefaultFeatureSources.LANG2000;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;


/**
 * @author cl
 */
public class ActinCortexAttachment extends QualitativeModule {

    public static final Diffusivity DEFAULT_CORTEX_DIFFUSIVITY = new Diffusivity(Quantities.getQuantity(2.45E-4, MICRO(METRE).pow(2).divide(SECOND)).asType(Diffusivity.class), LANG2000);

    private List<Vesicle> tetheringVesicles;

    public ActinCortexAttachment() {
        tetheringVesicles = new ArrayList<>();
    }

    @Override
    public void calculateUpdates() {
        ActinCortex cortex = simulation.getVolumeLayer().getCortex();
        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            if (!vesicle.getVesicleState().equals(VesicleStateRegistry.ACTIN_ATTACHED)) {
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
            vesicle.setVesicleState(VesicleStateRegistry.ACTIN_ATTACHED);
            DEFAULT_CORTEX_DIFFUSIVITY.scale();
            vesicle.setFeature(DEFAULT_CORTEX_DIFFUSIVITY);
        });
    }
}
