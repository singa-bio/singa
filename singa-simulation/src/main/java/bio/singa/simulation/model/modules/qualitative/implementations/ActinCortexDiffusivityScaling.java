package bio.singa.simulation.model.modules.qualitative.implementations;

import bio.singa.chemistry.entities.Protein;
import bio.singa.chemistry.features.diffusivity.Diffusivity;
import bio.singa.features.identifiers.UniProtIdentifier;
import bio.singa.features.model.Evidence;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.features.CortexDiffusivity;
import bio.singa.simulation.features.CytoplasmDiffusivity;
import bio.singa.simulation.features.MaximalConcentration;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleStateRegistry;
import bio.singa.simulation.model.graphs.AutomatonNode;
import bio.singa.simulation.model.modules.concentration.ModuleState;
import bio.singa.simulation.model.modules.qualitative.QualitativeModule;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;

import static bio.singa.simulation.features.DefaultFeatureSources.LANG2000;
import static bio.singa.simulation.model.sections.CellTopology.INNER;
import static tec.uom.se.unit.MetricPrefix.MICRO;
import static tec.uom.se.unit.MetricPrefix.NANO;
import static tec.uom.se.unit.Units.METRE;
import static tec.uom.se.unit.Units.SECOND;

/**
 * @author cl
 */
public class ActinCortexDiffusivityScaling extends QualitativeModule {

    public static final Diffusivity DEFAULT_CORTEX_DIFFUSIVITY = new Diffusivity(Quantities.getQuantity(2.45E-4, MICRO(METRE).pow(2).divide(SECOND)).asType(Diffusivity.class), LANG2000);
    public static final Diffusivity DEFAULT_CYTOPLASM_DIFFUSIFITY = Diffusivity.calculate(Quantities.getQuantity(50.0, NANO(METRE)));

    private static final Evidence origin = new Evidence(Evidence.OriginType.PREDICTION, "Cortex Diffusivity Estimation", "");

    private Quantity<Diffusivity> slope;

    private Protein tropomyosin = new Protein.Builder("TRP")
            .additionalIdentifier(new UniProtIdentifier("P09493"))
            .build();


    private Map<Vesicle, Diffusivity> diffusivities;
    private Quantity<Diffusivity> cortexDiffusivity;
    private Quantity<Diffusivity> cytoplasmDiffusivity;

    public ActinCortexDiffusivityScaling() {
        diffusivities = new HashMap<>();
        // features
        getRequiredFeatures().add(MaximalConcentration.class);
        getRequiredFeatures().add(CortexDiffusivity.class);
        getRequiredFeatures().add(CytoplasmDiffusivity.class);
    }

    @Override
    public void calculateUpdates() {
        cortexDiffusivity = getFeature(CortexDiffusivity.class).to(MICRO(METRE).pow(2).divide(SECOND).asType(Diffusivity.class));
        cytoplasmDiffusivity = getFeature(CytoplasmDiffusivity.class).to(MICRO(METRE).pow(2).divide(SECOND).asType(Diffusivity.class));
        double concentration = UnitRegistry.convert(getFeature(MaximalConcentration.class).getFeatureContent()).getValue().doubleValue();
        slope = Quantities.getQuantity((cortexDiffusivity.getValue().doubleValue() - cytoplasmDiffusivity.getValue().doubleValue()) / concentration, MICRO(METRE).pow(2).divide(SECOND).asType(Diffusivity.class));

        for (Vesicle vesicle : simulation.getVesicleLayer().getVesicles()) {
            if (vesicle.getVesicleState().equals(VesicleStateRegistry.ACTIN_TETHERED)) {
                estimateDiffusivity(vesicle);
            }
        }
        state = ModuleState.SUCCEEDED_WITH_PENDING_CHANGES;
    }

    public void estimateDiffusivity(Vesicle vesicle) {
        // get entry with largest area (main node)
        AutomatonNode node = vesicle.getAssociatedNodes().entrySet().stream().max(Map.Entry.comparingByValue()).get().getKey();
        double concentration = node.getConcentrationContainer().get(INNER, tropomyosin).getValue().doubleValue();
        ComparableQuantity<Diffusivity> quantity = (ComparableQuantity<Diffusivity>) slope.multiply(concentration).add(cytoplasmDiffusivity);
        if (quantity.isLessThan(cytoplasmDiffusivity)) {
            diffusivities.put(vesicle, new Diffusivity(cytoplasmDiffusivity, origin));
        } else {
            diffusivities.put(vesicle, new Diffusivity(quantity, origin));
        }
    }

    @Override
    public void optimizeTimeStep() {

    }

    @Override
    public void onReset() {
        diffusivities.clear();
    }

    @Override
    public void onCompletion() {
        for (Map.Entry<Vesicle, Diffusivity> entry : diffusivities.entrySet()) {
            // set diffusivity
            entry.getValue().scale();
            entry.getKey().setFeature(entry.getValue());
        }
    }

}
