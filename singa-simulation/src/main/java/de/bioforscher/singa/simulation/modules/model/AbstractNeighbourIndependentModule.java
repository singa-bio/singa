package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.EnvironmentalParameters;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractNeighbourIndependentModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNeighbourIndependentModule.class);

    private final Map<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> deltaFunctions;
    private ChemicalEntity currentChemicalEntity;
    private ConcentrationContainer currentHalfConcentrations;

    public AbstractNeighbourIndependentModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    public ChemicalEntity getCurrentChemicalEntity() {
        return currentChemicalEntity;
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = simulation.getGraph();
        // determine deltas
        for (AutomatonNode node : graph.getNodes()) {
            if (conditionalApplication.test(node)) {
                logger.trace("Determining delta for node {}.", node.getIdentifier());
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
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                determineFullDeltas(concentrationContainer);
            }
        }

        halfTime = true;
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                determineHalfDeltas(concentrationContainer);

            }
        }
        // examine local errors
        examineLocalError();
        return largestLocalError;
    }

    private void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Delta fullDelta = entry.getKey().apply(concentrationContainer);
                setHalfStepConcentration(fullDelta);
                logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), fullDelta);
            }
        }
    }

    private void determineHalfDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Delta halfDelta = entry.getKey().apply(currentHalfConcentrations).multiply(2.0);
                logger.trace("Calculated half delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), halfDelta.getQuantity());
                currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), halfDelta);
            }
        }
        // and register potential deltas at node
        currentNode.addPotentialDeltas(currentHalfDeltas.values());
    }

    private void examineLocalError() {
        // no deltas mean this module did not change anything in the course of this simulation step
        if (currentFullDeltas.isEmpty()) {
            return;
        }
        double largestLocalError = -Double.MAX_VALUE;
        DeltaIdentifier largestIdentifier = null;
        for (DeltaIdentifier identifier : currentFullDeltas.keySet()) {
            double fullDelta = currentFullDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double halfDelta = currentHalfDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double localError = 0.0;
            // if there is no change, there is no error
            if (fullDelta != 0.0 && halfDelta != 0) {
                // calculate error
                localError = Math.abs(1 - (fullDelta / halfDelta));
            }
            // determine the largest error in the current deltas
            if (largestLocalError < localError) {
                largestIdentifier = identifier;
                largestLocalError = localError;
            }
        }
        Objects.requireNonNull(largestIdentifier);
        // set local error and return local error
        this.largestLocalError = new LocalError(largestIdentifier.getNode(), largestIdentifier.getEntity(), largestLocalError);
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
    }

    private void setHalfStepConcentration(Delta fullDelta) {
        final double fullConcentration = currentNode.getAvailableConcentration(currentChemicalEntity, currentCellSection).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        currentHalfConcentrations.setAvailableConcentration(currentCellSection, currentChemicalEntity, Quantities.getQuantity(halfStepConcentration, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

}
