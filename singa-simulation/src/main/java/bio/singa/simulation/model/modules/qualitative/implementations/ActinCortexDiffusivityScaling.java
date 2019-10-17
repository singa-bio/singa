package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.simple.Protein;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.features.MaximalConcentration;
import bio.singa.simulation.features.ModifiedDiffusivity;
import bio.singa.simulation.features.OriginalDiffusivity;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import tech.units.indriya.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.simulation.features.DefaultFeatureSources.LANG2000;
import static bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry.TETHERED;
import static bio.singa.simulation.model.modules.concentration.ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static tech.units.indriya.unit.MetricPrefix.MICRO;
import static tech.units.indriya.unit.MetricPrefix.NANO;
import static tech.units.indriya.unit.Units.METRE;
import static tech.units.indriya.unit.Units.SECOND;

/**
 * @author cl
 */
public class ActinCortexDiffusivityScaling extends QualitativeModule {

    public static final Diffusivity DEFAULT_CORTEX_DIFFUSIVITY = new Diffusivity(Quantities.getQuantity(2.45E-4, MICRO(METRE).pow(2).divide(SECOND)).asType(Diffusivity.class), LANG2000);
    public static final Diffusivity DEFAULT_CYTOPLASM_DIFFUSIVITY = Diffusivity.calculate(Quantities.getQuantity(50.0, NANO(METRE)));

    private Protein tropomyosin = new Protein.Builder("TRP")
            .additionalIdentifier(new UniProtIdentifier("P09493"))
            .build();

    private Map<Vesicle, Quantity<Diffusivity>> diffusivity;
    private double cytoplasmDiffusivity;
    private double slope;


    public ActinCortexDiffusivityScaling() {
        diffusivity = new HashMap<>();
        // features
        getRequiredFeatures().add(MaximalConcentration.class);
        getRequiredFeatures().add(ModifiedDiffusivity.class);
        getRequiredFeatures().add(OriginalDiffusivity.class);
    }

    @Override
    public void calculateUpdates() {
        double cortexDiffusivity = getFeature(ModifiedDiffusivity.class).getScaledQuantity();
        cytoplasmDiffusivity = getFeature(OriginalDiffusivity.class).getScaledQuantity();
        double concentration = getFeature(MaximalConcentration.class).getContent().getValue().doubleValue();
        slope = (cortexDiffusivity - cytoplasmDiffusivity) / concentration;

        for (Vesicle vesicle : getSimulation().getVesicleLayer().getVesicles()) {
            if (vesicle.getState().equals(TETHERED)) {
                estimateDiffusivity(vesicle);
            }
        }
        setState(SUCCEEDED_WITH_PENDING_CHANGES);
    }

    public void estimateDiffusivity(Vesicle vesicle) {
        // get entry with largest area (main node)
        AutomatonNode node = vesicle.getAssociatedNodes().entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .get().getKey();

        double concentration = node.getConcentrationContainer().get(INNER, tropomyosin);
        double resultingDiffusivity = slope * concentration + cytoplasmDiffusivity;
        if (resultingDiffusivity < cytoplasmDiffusivity) {
            diffusivity.put(vesicle, Quantities.getQuantity(cytoplasmDiffusivity, UnitRegistry.getDefaultUnit(Diffusivity.SQUARE_METRE_PER_SECOND).asType(Diffusivity.class)));
        } else {
            diffusivity.put(vesicle, Quantities.getQuantity(resultingDiffusivity, UnitRegistry.getDefaultUnit(Diffusivity.SQUARE_METRE_PER_SECOND).asType(Diffusivity.class)));
        }
    }

    @Override
    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        diffusivity.clear();
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Vesicle, Quantity<Diffusivity>> entry : diffusivity.entrySet()) {
            // set diffusivity
            Diffusivity diffusivity = entry.getKey().getFeature(Diffusivity.class);
            diffusivity.setContent(entry.getValue());
        }
    }

}
