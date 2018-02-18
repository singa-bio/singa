package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.concentrations.ConcentrationContainer;
import de.bioforscher.singa.simulation.model.concentrations.Delta;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractNeighbourDependentModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNeighbourDependentModule.class);

    private final Map<AutomatonNode, ConcentrationContainer> halfConcentrations;
    private final Map<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> deltaFunctions;
    private ChemicalEntity currentChemicalEntity;

    public AbstractNeighbourDependentModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
        halfConcentrations = new HashMap<>();
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    public ChemicalEntity getCurrentChemicalEntity() {
        return currentChemicalEntity;
    }

    public void determineAllDeltas() {
        AutomatonGraph graph = simulation.getGraph();
        // determine full deltas
        halfTime = false;
        for (AutomatonNode node : graph.getNodes()) {
            if (conditionalApplication.test(node)) {
                currentNode = node;
                determineFullDeltas(node.getConcentrationContainer());
            }
        }
        // half step concentrations
        determineHalfStepConcentration();
        // half step deltas
        halfTime = true;
        for (Map.Entry<AutomatonNode, ConcentrationContainer> entry : halfConcentrations.entrySet()) {
           currentNode = entry.getKey();
           determineHalfStepDeltas(entry.getValue());
        }

        // examine local errors
        examineLocalError();

    }

    public LocalError determineDeltasForNode(AutomatonNode node) {
        // using neighbor dependent modules you need to calculate all changes
        determineAllDeltas();
        return largestLocalError;
    }

    public void determineFullDeltas(ConcentrationContainer concentrationContainer) {
        logger.trace("Determining full deltas for node {}.", currentNode.getIdentifier());
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine full step deltas and half step concentrations
                for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
                    if (entry.getValue().test(concentrationContainer)) {
                        Delta fullDelta = entry.getKey().apply(concentrationContainer);
                        if (fullDelta.getQuantity().getValue().doubleValue() != 0.0) {
                            logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                            currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), fullDelta);
                        }
                    }
                }
            }
        }
    }

    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        logger.trace("Determining half deltas for node {}.", currentNode.getIdentifier());
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : currentNode.getAllReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine half step deltas and half step concentrations
                for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
                    if (entry.getValue().test(concentrationContainer)) {
                        Delta halfDelta = entry.getKey().apply(concentrationContainer).multiply(2.0);
                        if (halfDelta.getQuantity().getValue().doubleValue() != 0.0) {
                            logger.trace("Calculated half delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), halfDelta.getQuantity());
                            currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), halfDelta);
                        }
                        // and register potential deltas at node
                        currentNode.addPotentialDelta(halfDelta);
                    }
                }
            }
        }
    }

    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : currentFullDeltas.entrySet()) {
            DeltaIdentifier key = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            Quantity<MolarConcentration> fullConcentration = key.getNode().getAvailableConcentration(key.getEntity(), key.getSection());
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(value.getQuantity().multiply(2.0));
            ConcentrationContainer halfConcentration;
            if (!halfConcentrations.containsKey(key.getNode())) {
                halfConcentration = key.getNode().getConcentrationContainer().getCopy();
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), halfStepConcentration);
                halfConcentrations.put(key.getNode(), halfConcentration);
            } else {
                halfConcentration = halfConcentrations.get(key.getNode());
                halfConcentration.setAvailableConcentration(key.getSection(), key.getEntity(), halfStepConcentration);
            }
        }
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
    }

}
