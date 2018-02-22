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

    public AbstractNeighbourDependentModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
        halfConcentrations = new HashMap<>();
    }

    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
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
        largestLocalError = determineLargestLocalError();
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
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
                        if (deltaIsValid(fullDelta)) {
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
                        Delta halfDelta = entry.getKey().apply(concentrationContainer);
                        applyHalfDelta(halfDelta);
                    }
                }
            }
        }
    }

    @Override
    void applyHalfDelta(Delta halfDelta) {
        halfDelta = halfDelta.multiply(2.0);
        logger.trace("Calculated half delta for {} in {}: {}", currentChemicalEntity.getName(), currentCellSection.getIdentifier(), halfDelta.getQuantity());
        currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), halfDelta);
        currentNode.addPotentialDelta(halfDelta);
    }

    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : currentFullDeltas.entrySet()) {
            DeltaIdentifier key = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            Quantity<MolarConcentration> fullConcentration = key.getNode().getAvailableConcentration(key.getEntity(), key.getSection());
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(value.getQuantity().multiply(0.5));
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

}
