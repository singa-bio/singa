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
 * Section specific modules compute updates for whole cellular sections at once. The computations performed are not
 * dependent on the surrounding nodes, additionally all chemical entities change at the same rate. Once a velocity
 * of the delta functions is calculated the updates are the same for each chemical entity. This is the case for
 * most reactions since substrates are consumed at the same rate as products are produced.
 *
 * @author cl
 */
public abstract class AbstractSectionSpecificModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractSectionSpecificModule.class);

    /**
     * The functions that are applied with each epoch and for each node.
     */
    private final Map<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> deltaFunctions;

    /**
     * The concentrations for the half time steps, for explicit calculation.
     */
    private ConcentrationContainer currentHalfConcentrations;

    /**
     * Creates a new section independent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    public AbstractSectionSpecificModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
    }

    /**
     * Adds a delta function and the corresponding predicate. The predicate determines whether the update will be
     * calculated under the current conditions, e.g. for the current chemical entity or cellular compartment.
     *
     * @param deltaFunction The delta function.
     * @param predicate The predicate for the conditional application.
     */
    public void addDeltaFunction(Function<ConcentrationContainer, List<Delta>> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    @Override
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

    @Override
    public LocalError determineDeltasForNode(AutomatonNode node) {
        currentNode = node;
        ConcentrationContainer fullConcentrations = node.getConcentrationContainer();
        currentHalfConcentrations = fullConcentrations.getCopy();
        return determineDeltas(fullConcentrations);
    }

    /**
     * The deltas for all delta functions, cell sections and chemical entities are computed. In neighborhood-independent
     * modules all full deltas are calculated first before calculating all half step deltas. Afterwards the local errors
     * are calculated and the largest error is determined.
     *
     * @param concentrationContainer The container to calculate deltas for.
     * @return The largest local error for the calculation.
     */
    private LocalError determineDeltas(ConcentrationContainer concentrationContainer) {
        // calculate full time step deltas
        halfTime = false;
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            determineFullStepDeltas(concentrationContainer);
        }
        // calculate half time step deltas
        halfTime = true;
        for (CellSection cellSection : currentNode.getAllReferencedSections()) {
            currentCellSection = cellSection;
            determineHalfStepDeltas(concentrationContainer);
        }
        // examine local errors
        largestLocalError = determineLargestLocalError();
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
        // return largest error
        return largestLocalError;
    }

    /**
     * Determines the full step deltas for a current cell section.
     *
     * @param concentrationContainer The container to calculate deltas for.
     */
    private void determineFullStepDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> fullDeltas = entry.getKey().apply(concentrationContainer);
                ArrayList<ChemicalEntity> unchangedEntities = new ArrayList<>(concentrationContainer.getAllReferencedEntities());
                for (Delta fullDelta : fullDeltas) {
                    currentChemicalEntity = fullDelta.getChemicalEntity();
                    if (deltaIsValid(fullDelta)) {
                        setHalfStepConcentration(fullDelta);
                        logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                        currentFullDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), fullDelta);
                        unchangedEntities.remove(fullDelta.getChemicalEntity());
                    }
                }
                for (ChemicalEntity unchangedEntity : unchangedEntities) {
                    currentHalfConcentrations.setAvailableConcentration(currentCellSection, unchangedEntity,
                            currentNode.getAvailableConcentration(unchangedEntity, currentCellSection));
                }
            }
        }
    }

    /**
     * Determines the concentrations if half of the full delta would be applied to the current node.
     *
     * @param fullDelta The full step delta.
     */
    private void setHalfStepConcentration(Delta fullDelta) {
        final double fullConcentration = currentNode.getAvailableConcentration(fullDelta.getChemicalEntity(), fullDelta.getCellSection()).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        currentHalfConcentrations.setAvailableConcentration(fullDelta.getCellSection(), fullDelta.getChemicalEntity(), Quantities.getQuantity(halfStepConcentration, EnvironmentalParameters.getTransformedMolarConcentration()));
    }

    /**
     * Determines the half delta that would be applied for half of the time step.
     *
     * @param concentrationContainer The container to calculate half deltas for.
     */
    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, List<Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                List<Delta> halfDeltas = entry.getKey().apply(currentHalfConcentrations);
                for (Delta halfDelta : halfDeltas) {
                    currentChemicalEntity = halfDelta.getChemicalEntity();
                    applyHalfStepDelta(halfDelta);
                }
            }
        }
    }

}
