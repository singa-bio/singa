package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
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

    private Predicate<AutomatonNode> conditionalApplication;

    private LocalError largestLocalError;

    private AutomatonNode currentNode;
    private CellSection currentCellSection;
    private List<Delta> currentFullDeltas;
    private List<Delta> currentHalfDeltas;
    private ConcentrationContainer currentHalfConcentrations;

    public AbstractSectionSpecificModule() {
        deltaFunctions = new HashMap<>();
        currentFullDeltas = new ArrayList<>();
        currentHalfDeltas = new ArrayList<>();
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
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
        deltaFunctions.put(deltaFunction, predicate);
    }

    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    public CellSection getCurrentCellSection() {
        return currentCellSection;
    }

    public void onlyApplyIf(Predicate<AutomatonNode> predicate) {
        conditionalApplication = predicate;
    }

    public void applyAlways() {
        conditionalApplication = bioNode -> true;
    }

    protected <FeatureContent> FeatureContent getFeature(ChemicalEntity<?> entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = simulation.getGraph();
        // determine deltas
        for (AutomatonNode node : graph.getNodes()) {
            if (conditionalApplication.test(node)) {
                determineDeltasForNode(node);
            }
        }
    }

    public LocalError determineDeltasForNode(AutomatonNode node) {
        currentNode = node;
        ConcentrationContainer fullConcentrations = node.getConcentrationContainer();
        currentHalfConcentrations = fullConcentrations.getCopy();
        return determineDeltas(fullConcentrations);
    }

    public LocalError determineDeltas(ConcentrationContainer concentrationContainer) {
        halfTime = false;
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            determineFullDeltas(concentrationContainer);
        }

        halfTime = true;
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            determineHalfDeltas(concentrationContainer);
        }
        // examine local errors
        examineLocalError();
        return largestLocalError;
    }

    private void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> fullDeltas = entry.getKey().apply(concentrationContainer);
                ArrayList<ChemicalEntity<?>> unchangedEntities = new ArrayList<>(concentrationContainer.getAllReferencedEntities());
                for (Delta fullDelta : fullDeltas) {
                    setHalfStepConcentration(fullDelta);
                    currentFullDeltas.add(fullDelta);
                    unchangedEntities.remove(fullDelta.getChemicalEntity());
                }
                for (ChemicalEntity<?> unchangedEntity : unchangedEntities) {
                    currentHalfConcentrations.setAvailableConcentration(currentCellSection, unchangedEntity,
                            currentNode.getAvailableConcentration(unchangedEntity, currentCellSection));
                }
            }
        }
    }

    private void setHalfStepConcentration(Delta fullDelta) {
        final double fullConcentration = currentNode.getAvailableConcentration(fullDelta.getChemicalEntity(), fullDelta.getCellSection()).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        currentHalfConcentrations.setAvailableConcentration(fullDelta.getCellSection(), fullDelta.getChemicalEntity(), Quantities.getQuantity(halfStepConcentration, MOLE_PER_LITRE));
    }

    private void determineHalfDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> halfDeltas = entry.getKey().apply(currentHalfConcentrations);
                for (Delta halfDelta : halfDeltas) {
                    currentHalfDeltas.add(halfDelta.multiply(2.0));
                }
            }
        }
        // and register potential deltas at node
        currentNode.addPotentialDeltas(currentHalfDeltas);
    }

    private void examineLocalError() {
        // only if there is any change there can be a local error
        // careful we rely on putting the deltas in the same order as they are referenced in the list of delta functions
        LocalError temporaryLargestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (int i = 0; i < currentFullDeltas.size(); i++) {
            Delta fullDelta = currentFullDeltas.get(i);
            double fullDeltaValue = fullDelta.getQuantity().getValue().doubleValue();
            double halfDeltaValue = currentHalfDeltas.get(i).getQuantity().getValue().doubleValue();
            double localErrorValue = 0.0;
            // if there is no change, there is no error
            if (fullDeltaValue != 0.0 && halfDeltaValue != 0) {
                // calculate error
                localErrorValue = Math.abs(1 - (fullDeltaValue / halfDeltaValue));
            }
            // determine the largest error in the current deltas
            if (temporaryLargestLocalError.getValue() < localErrorValue) {
                temporaryLargestLocalError = new LocalError(currentNode, fullDelta.getChemicalEntity(), localErrorValue);
            }
        }
        // compare to current maximum
        if (largestLocalError.getValue() < temporaryLargestLocalError.getValue()) {
            // set if this is larger
            largestLocalError = temporaryLargestLocalError;
        }
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
    }

    @Override
    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

}
