package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
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
public abstract class AbstractSectionSpecificModule extends AbstractModule {

    private final Map<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> deltaFunctions;
    private ConcentrationContainer currentHalfConcentrations;

    public AbstractSectionSpecificModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
    }

    public void addDeltaFunction(Function<ConcentrationContainer, List<Delta>> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
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
                    currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, fullDelta.getChemicalEntity()), fullDelta);
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
                    currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, halfDelta.getChemicalEntity()), halfDelta.multiply(2.0));
                }
            }
        }
        // and register potential deltas at node
        currentNode.addPotentialDeltas(currentHalfDeltas.values());
    }

    private void examineLocalError() {
        // only if there is any change there can be a local error
        // careful we rely on putting the deltas in the same order as they are referenced in the list of delta functions
        LocalError temporaryLargestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
        for (DeltaIdentifier deltaIdentifier : currentFullDeltas.keySet()) {
            Delta fullDelta = currentFullDeltas.get(deltaIdentifier);
            double fullDeltaValue = fullDelta.getQuantity().getValue().doubleValue();
            double halfDeltaValue = currentHalfDeltas.get(deltaIdentifier).getQuantity().getValue().doubleValue();
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

}
