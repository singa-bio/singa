package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tec.uom.se.quantity.Quantities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Neighbourhood independent modules compute updates during simulations. The computations performed are not dependent on
 * the surrounding nodes. The updates for each subsection is computed individually.
 *
 * @author cl
 */
public abstract class AbstractNeighbourIndependentModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNeighbourIndependentModule.class);

    /**
     * The functions that are applied with each epoch and for each node.
     */
    private final Map<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> deltaFunctions;

    /**
     * The concentrations for the half time steps, for explicit calculation.
     */
    private ConcentrationContainer currentHalfConcentrations;

    /**
     * Creates a new neighbourhood independent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    public AbstractNeighbourIndependentModule(Simulation simulation) {
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
    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    @Override
    public void determineAllDeltas(List<Updatable> updatables) {
        // determine deltas
        for (Updatable node : updatables) {
            if (conditionalApplication.test(node)) {
                logger.trace("Determining delta for {}.", node.getStringIdentifier());
                determineDeltas(node);
            }
        }
    }

    @Override
    public LocalError determineDeltas(Updatable updatable) {
        currentUpdatable = updatable;
        ConcentrationContainer fullConcentrations = updatable.getConcentrationContainer();
        currentHalfConcentrations = fullConcentrations.emptyCopy();
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
        for (CellSubsection cellSection : currentUpdatable.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : getReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                determineFullStepDeltas(concentrationContainer);
            }
        }
        // calculate half time step deltas
        halfTime = true;
        for (CellSubsection cellSection : currentUpdatable.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : getReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                determineHalfStepDeltas(concentrationContainer);
            }
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
     * Determines the full step deltas for a current chemical entity, and current cell section.
     *
     * @param concentrationContainer The container to calculate deltas for.
     */
    private void determineFullStepDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Delta fullDelta = entry.getKey().apply(concentrationContainer);
                if (deltaIsValid(fullDelta)) {
                    setHalfStepConcentration(fullDelta);
                    logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                    currentFullDeltas.put(new DeltaIdentifier(currentUpdatable, currentCellSection, currentChemicalEntity), fullDelta);
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
        final double fullConcentration = currentUpdatable.getConcentration(currentCellSection, currentChemicalEntity).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        currentHalfConcentrations.set(currentCellSection, currentChemicalEntity, Quantities.getQuantity(halfStepConcentration, Environment.getConcentrationUnit()));
    }

    /**
     * Determines the half delta that would be applied for half of the time step.
     *
     * @param concentrationContainer The container to calculate half deltas for.
     */
    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Delta halfDelta = entry.getKey().apply(currentHalfConcentrations);
                applyHalfStepDelta(halfDelta);
            }
        }
    }

}
