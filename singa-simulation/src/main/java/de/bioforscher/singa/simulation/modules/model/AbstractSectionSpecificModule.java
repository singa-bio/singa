package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.BioNode;
import tec.units.ri.quantity.Quantities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import static de.bioforscher.singa.features.units.UnitProvider.MOLE_PER_LITRE;

/**
 * @author cl
 */
public class AbstractSectionSpecificModule implements Module {

    protected boolean halfTime;

    private Simulation simulation;
    private Map<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> deltaFunctions;

    private Predicate<BioNode> conditionalApplication;

    private LocalError largestLocalError;

    private BioNode currentNode;
    private CellSection currentCellSection;
    private List<Delta> currentFullDeltas;
    private List<Delta> currentHalfDeltas;
    private ConcentrationContainer currentHalfConcentrations;

    public AbstractSectionSpecificModule() {
        this.deltaFunctions = new HashMap<>();
        this.currentFullDeltas = new ArrayList<>();
        this.currentHalfDeltas = new ArrayList<>();
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    public AbstractSectionSpecificModule(Simulation simulation) {
        this();
        this.simulation = simulation;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void addDeltaFunction(Function<ConcentrationContainer, List<Delta>> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        this.deltaFunctions.put(deltaFunction, predicate);
    }

    public BioNode getCurrentNode() {
        return this.currentNode;
    }

    public CellSection getCurrentCellSection() {
        return this.currentCellSection;
    }

    public void onlyApplyIf(Predicate<BioNode> predicate) {
        this.conditionalApplication = predicate;
    }

    public void applyAlways() {
        this.conditionalApplication = bioNode -> true;
    }

    protected <FeatureContent> FeatureContent getFeature(ChemicalEntity<?> entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (this.halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = this.simulation.getGraph();
        // determine deltas
        for (BioNode node : graph.getNodes()) {
            if (this.conditionalApplication.test(node)) {
                determineDeltasForNode(node);
            }
        }
    }

    public LocalError determineDeltasForNode(BioNode node) {
        this.currentNode = node;
        ConcentrationContainer fullConcentrations = node.getConcentrations();
        this.currentHalfConcentrations = fullConcentrations.copy();
        return determineDeltas(fullConcentrations);
    }

    public LocalError determineDeltas(ConcentrationContainer concentrationContainer) {
        this.halfTime = false;
        for (CellSection cellSection : this.currentNode.getAllReferencedSections()) {
            this.currentCellSection = cellSection;
            determineFullDeltas(concentrationContainer);
        }

        this.halfTime = true;
        for (CellSection cellSection : this.currentNode.getAllReferencedSections()) {
            this.currentCellSection = cellSection;
            determineHalfDeltas(concentrationContainer);
        }
        // examine local errors
        examineLocalError();
        return this.largestLocalError;
    }

    private void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : this.deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> fullDeltas = entry.getKey().apply(concentrationContainer);
                ArrayList<ChemicalEntity<?>> unchangedEntities = new ArrayList<>(concentrationContainer.getAllReferencedEntities());
                for (Delta fullDelta : fullDeltas) {
                    setHalfStepConcentration(fullDelta);
                    this.currentFullDeltas.add(fullDelta);
                    unchangedEntities.remove(fullDelta.getEntity());
                }
                for (ChemicalEntity<?> unchangedEntity : unchangedEntities) {
                    this.currentHalfConcentrations.setAvailableConcentration(currentCellSection, unchangedEntity,
                            this.currentNode.getAvailableConcentration(unchangedEntity, currentCellSection));
                }
            }
        }
    }

    private void setHalfStepConcentration(Delta fullDelta) {
        final double fullConcentration = this.currentNode.getAvailableConcentration(fullDelta.getEntity(), fullDelta.getCellSection()).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        this.currentHalfConcentrations.setAvailableConcentration(fullDelta.getCellSection(), fullDelta.getEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
    }

    private void determineHalfDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : this.deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> halfDeltas = entry.getKey().apply(this.currentHalfConcentrations);
                for (Delta halfDelta : halfDeltas) {
                    this.currentHalfDeltas.add(halfDelta.multiply(2.0));
                }
            }
        }
        // and register potential deltas at node
        this.currentNode.addPotentialDeltas(this.currentHalfDeltas);
    }

    private void examineLocalError() {
        // only if there is any change there can be a local error
        // careful we rely on putting the deltas in the same order as they are referenced in the list of delta functions
        LocalError temporaryLargestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (int i = 0; i < this.currentFullDeltas.size(); i++) {
            Delta fullDelta = this.currentFullDeltas.get(i);
            double fullDeltaValue = fullDelta.getQuantity().getValue().doubleValue();
            double halfDeltaValue = this.currentHalfDeltas.get(i).getQuantity().getValue().doubleValue();
            double localErrorValue = 0.0;
            // if there is no change, there is no error
            if (fullDeltaValue != 0.0 && halfDeltaValue != 0) {
                // calculate error
                localErrorValue = Math.abs(1 - (fullDeltaValue / halfDeltaValue));
            }
            // determine the largest error in the current deltas
            if (temporaryLargestLocalError.getValue() < localErrorValue) {
                temporaryLargestLocalError = new LocalError(this.currentNode, fullDelta.getEntity(), localErrorValue);
            }
        }
        // compare to current maximum
        if (this.largestLocalError.getValue() < temporaryLargestLocalError.getValue()) {
            // set if this is larger
            this.largestLocalError = temporaryLargestLocalError;
        }
        // clear used deltas
        this.currentFullDeltas.clear();
        this.currentHalfDeltas.clear();
    }

    @Override
    public LocalError getLargestLocalError() {
        return this.largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        this.largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

}
