package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.features.parameters.Environment;
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
 * @author cl
 */
public abstract class AbstractNodeSpecificModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNodeSpecificModule.class);

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
    public AbstractNodeSpecificModule(Simulation simulation) {
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
    public void determineAllDeltas(List<Updatable> updatables) {
        // determine deltas
        for (Updatable updatable : updatables) {
            if (conditionalApplication.test(updatable)) {
                logger.trace("Determining delta for {}.", updatable.getStringIdentifier());
                determineDeltas(updatable);
            }
        }
    }

    @Override
    public LocalError determineDeltas(Updatable updatable) {
        currentUpdatable = updatable;
        ConcentrationContainer fullConcentrations = updatable.getConcentrationContainer();
        currentHalfConcentrations = fullConcentrations.fullCopy();
        // calculate full time step deltas
        halfTime = false;
        determineFullStepDeltas(fullConcentrations);

        // calculate half time step deltas
        halfTime = true;
        determineHalfStepDeltas(fullConcentrations);

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
                for (Delta fullDelta : fullDeltas) {
                    currentChemicalEntity = fullDelta.getChemicalEntity();
                    currentCellSection = fullDelta.getCellSubsection();
                    if (deltaIsValid(fullDelta)) {
                        setHalfStepConcentration(fullDelta);
                        logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getName(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                        currentFullDeltas.put(new DeltaIdentifier(currentUpdatable, currentCellSection, currentChemicalEntity), fullDelta);
                    }
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
        final double fullConcentration = currentUpdatable.getConcentration(fullDelta.getCellSubsection(), fullDelta.getChemicalEntity()).getValue().doubleValue();
        final double halfStepConcentration = fullConcentration + 0.5 * fullDelta.getQuantity().getValue().doubleValue();
        currentHalfConcentrations.set(fullDelta.getCellSubsection(), fullDelta.getChemicalEntity(), Quantities.getQuantity(halfStepConcentration, Environment.getTransformedMolarConcentration()));
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
