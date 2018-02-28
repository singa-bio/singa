package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractSectionSpecificModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractSectionSpecificModule.class);

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
        largestLocalError = determineLargestLocalError();
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
        // return largest error
        return largestLocalError;
    }

    private void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> fullDeltas = entry.getKey().apply(concentrationContainer);
                ArrayList<ChemicalEntity<?>> unchangedEntities = new ArrayList<>(concentrationContainer.getAllReferencedEntities());
                for (Delta fullDelta : fullDeltas) {
                    currentChemicalEntity = fullDelta.getChemicalEntity();
                    if (deltaIsValid(fullDelta)) {
                        setHalfStepConcentration(fullDelta);
                        currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), fullDelta);
                        unchangedEntities.remove(fullDelta.getChemicalEntity());
                    }
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
        currentHalfConcentrations.setAvailableConcentration(fullDelta.getCellSection(), fullDelta.getChemicalEntity(), Quantities.getQuantity(halfStepConcentration, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    private void determineHalfDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> halfDeltas = entry.getKey().apply(currentHalfConcentrations);
                for (Delta halfDelta : halfDeltas) {
                    currentChemicalEntity = halfDelta.getChemicalEntity();
                    applyHalfDelta(halfDelta);
                }
            }
        }
    }

}
