package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import de.bioforscher.singa.simulation.modules.newmodules.Delta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractNeighbourDependentNodeSpecificModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNeighbourDependentNodeSpecificModule.class);

    /**
     * The half concentrations for each node.
     */
    protected final Map<Updatable, ConcentrationContainer> halfConcentrations;

    /**
     * The functions that are applied with each epoch and for each node.
     */
    private final Map<Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>>, Predicate<ConcentrationContainer>> deltaFunctions;

    /**
     * Creates a new neighbourhood dependent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    public AbstractNeighbourDependentNodeSpecificModule(Simulation simulation) {
        super(simulation);
        deltaFunctions = new HashMap<>();
        halfConcentrations = new HashMap<>();
    }

    /**
     * Adds a delta function and the corresponding predicate. The predicate determines whether the update will be
     * calculated under the current conditions, e.g. for the current chemical entity or cellular compartment.
     *
     * @param deltaFunction The delta function.
     * @param predicate The predicate for the conditional application.
     */
    public void addDeltaFunction(Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>> deltaFunction, Predicate<ConcentrationContainer> predicate) {
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
        // determine full deltas
        currentUpdatable = updatable;
        ConcentrationContainer fullConcentrations = updatable.getConcentrationContainer();
        // calculate full time step deltas
        halfTime = false;
        determineFullStepDeltas(fullConcentrations);
        determineHalfStepConcentration();
        // half step deltas
        halfTime = true;
        determineHalfStepDeltas(halfConcentrations.get(updatable));
        // examine local errors
        largestLocalError = determineLargestLocalError();
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
        return largestLocalError;
    }

    /**
     * Determines the full step deltas for a current cell section.
     *
     * @param concentrationContainer The container to calculate deltas for.
     */
    private void determineFullStepDeltas(ConcentrationContainer concentrationContainer) {
        // determine full step deltas and half step concentrations
        for (Map.Entry<Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Map<DeltaIdentifier, Delta> fullDeltas = entry.getKey().apply(concentrationContainer);
                for (Map.Entry<DeltaIdentifier, Delta> delta : fullDeltas.entrySet()) {
                    Delta fullDelta = delta.getValue();
                    DeltaIdentifier deltaIdentifier = delta.getKey();
                    currentChemicalEntity = deltaIdentifier.getEntity();
                    currentCellSection = deltaIdentifier.getSubsection();
                    if (deltaIsValid(fullDelta)) {
                        logger.trace("Full delta for {} in {}:{} = {}", currentChemicalEntity.getName(), deltaIdentifier.getUpdatable().getStringIdentifier(), currentCellSection.getIdentifier(), fullDelta.getQuantity());
                        currentFullDeltas.put(deltaIdentifier, fullDelta);
                    }
                }
            }
        }
    }

    /**
     * Determines all the half step concentrations after all full step deltas have been calculated.
     */
    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : currentFullDeltas.entrySet()) {
            DeltaIdentifier identifier = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            Quantity<MolarConcentration> fullConcentration = identifier.getUpdatable().getConcentration(identifier.getSubsection(), identifier.getEntity());
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(value.getQuantity().multiply(0.5));
            ConcentrationContainer halfConcentration;
            if (!halfConcentrations.containsKey(identifier.getUpdatable())) {
                halfConcentration = identifier.getUpdatable().getConcentrationContainer().emptyCopy();
                halfConcentration.set(identifier.getSubsection(), identifier.getEntity(), halfStepConcentration);
                halfConcentrations.put(identifier.getUpdatable(), halfConcentration);
            } else {
                halfConcentration = halfConcentrations.get(identifier.getUpdatable());
                halfConcentration.set(identifier.getSubsection(), identifier.getEntity(), halfStepConcentration);
            }
        }
    }

    /**
     * Determines the half delta that would be applied for half of the time step.
     *
     * @param concentrationContainer The container to calculate half deltas for.
     */
    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        if (currentFullDeltas.isEmpty()) {
            return;
        }
        // determine half step deltas
        for (Map.Entry<Function<ConcentrationContainer, Map<DeltaIdentifier, Delta>>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
            if (entry.getValue().test(concentrationContainer)) {
                Map<DeltaIdentifier, Delta> halfDeltas = entry.getKey().apply(concentrationContainer);
                for (Map.Entry<DeltaIdentifier, Delta> deltaEntry : halfDeltas.entrySet()) {
                    DeltaIdentifier deltaIdentifier = deltaEntry.getKey();
                    currentChemicalEntity = deltaIdentifier.getEntity();
                    currentCellSection = deltaIdentifier.getSubsection();
                    Delta halfDelta = deltaEntry.getValue();
                    if (deltaIsValid(halfDelta)) {
                        halfDelta = halfDelta.multiply(2.0);
                        logger.trace("Half delta for {} in {}:{} = {}", currentChemicalEntity.getName(), deltaIdentifier.getUpdatable().getStringIdentifier(), currentCellSection.getIdentifier(), halfDelta.getQuantity());
                        currentHalfDeltas.put(deltaIdentifier, halfDelta);
                        currentUpdatable.addPotentialDelta(halfDelta);
                    }
                }
            }
        }
    }


}
