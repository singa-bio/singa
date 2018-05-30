package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.quantities.MolarConcentration;
import de.bioforscher.singa.simulation.model.graphs.AutomatonGraph;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import de.bioforscher.singa.simulation.model.newsections.CellSubsection;
import de.bioforscher.singa.simulation.model.newsections.ConcentrationContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Neighbourhood dependent modules compute updates during simulations. The computations performed are dependent on
 * the surrounding nodes. The updates for the whole system have to be calculated at once, rendering neighbourhood
 * dependent modules performance critical if they need to be recalculated in between time steps to reduce the numerical
 * error.
 *
 * @author cl
 */
public abstract class AbstractNeighbourDependentModule extends AbstractModule {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractNeighbourDependentModule.class);

    /**
     * The half concentrations for each node.
     */
    private final Map<Updatable, ConcentrationContainer> halfConcentrations;

    /**
     * The functions that are applied with each epoch and for each node.
     */
    private final Map<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> deltaFunctions;

    /**
     * Creates a new neighbourhood dependent module for the given simulation.
     *
     * @param simulation The simulation.
     */
    public AbstractNeighbourDependentModule(Simulation simulation) {
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
    public void addDeltaFunction(Function<ConcentrationContainer, Delta> deltaFunction, Predicate<ConcentrationContainer> predicate) {
        deltaFunctions.put(deltaFunction, predicate);
    }

    @Override
    public void determineAllDeltas(List<Updatable> updatables) {
        AutomatonGraph graph = simulation.getGraph();
        // determine full deltas
        halfTime = false;
        for (AutomatonNode node : graph.getNodes()) {
            if (conditionalApplication.test(node)) {
                currentUpdatable = node;
                determineFullStepDeltas(node.getConcentrationContainer());
            }
        }
        // half step concentrations
        determineHalfStepConcentration();
        // half step deltas
        halfTime = true;
        for (Map.Entry<Updatable, ConcentrationContainer> entry : halfConcentrations.entrySet()) {
            currentUpdatable = entry.getKey();
            determineHalfStepDeltas(entry.getValue());
        }

        // examine local errors
        largestLocalError = determineLargestLocalError();
        // clear used deltas
        currentFullDeltas.clear();
        currentHalfDeltas.clear();
    }

    @Override
    public LocalError determineDeltas(Updatable node) {
        // using neighbor dependent modules you need to calculate all changes
        determineAllDeltas(simulation.collectUpdatables());
        return largestLocalError;
    }


    /**
     * Determines the full step deltas for a current chemical entity, and current cell section.
     *
     * @param concentrationContainer The container to calculate deltas for.
     */
    private void determineFullStepDeltas(ConcentrationContainer concentrationContainer) {
        logger.trace("Determining full deltas for node {}.", currentUpdatable.getStringIdentifier());
        for (CellSubsection cellSection : currentUpdatable.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : getReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine full step deltas and half step concentrations
                for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
                    if (entry.getValue().test(concentrationContainer)) {
                        Delta fullDelta = entry.getKey().apply(concentrationContainer);
                        if (deltaIsValid(fullDelta)) {
                            logger.trace("Calculated full delta for {} in {}: {}", getCurrentChemicalEntity().getIdentifier(), getCurrentCellSection().getIdentifier(), fullDelta.getQuantity());
                            currentFullDeltas.put(new DeltaIdentifier(currentUpdatable, currentCellSection, currentChemicalEntity), fullDelta);
                        }
                    }
                }
            }
        }
    }

    /**
     * Determines the half step deltas that would be applied for half of the time step.
     *
     * @param concentrationContainer The container to calculate half deltas for.
     */
    private void determineHalfStepDeltas(ConcentrationContainer concentrationContainer) {
        logger.trace("Determining half deltas for node {}.", currentUpdatable.getStringIdentifier());
        for (CellSubsection cellSection : currentUpdatable.getAllReferencedSections()) {
            currentCellSection = cellSection;
            for (ChemicalEntity chemicalEntity : getReferencedEntities()) {
                currentChemicalEntity = chemicalEntity;
                // determine half step deltas and half step concentrations
                for (Map.Entry<Function<ConcentrationContainer, Delta>, Predicate<ConcentrationContainer>> entry : deltaFunctions.entrySet()) {
                    if (entry.getValue().test(concentrationContainer)) {
                        Delta halfDelta = entry.getKey().apply(concentrationContainer);
                        applyHalfStepDelta(halfDelta);
                    }
                }
            }
        }
    }

    @Override
    void applyHalfStepDelta(Delta halfDelta) {
        halfDelta = halfDelta.multiply(2.0);
        if (halfDelta.getQuantity().getValue().doubleValue() > 0.0) {
            logger.trace("Calculated half delta for {} in {}: {}", currentChemicalEntity.getIdentifier(), currentCellSection.getIdentifier(), halfDelta.getQuantity());
        }
        currentHalfDeltas.put(new DeltaIdentifier(currentUpdatable, currentCellSection, currentChemicalEntity), halfDelta);
        currentUpdatable.addPotentialDelta(halfDelta);
    }

    /**
     * Determines all the half step concentrations after all full step deltas have been calculated.
     */
    private void determineHalfStepConcentration() {
        for (Map.Entry<DeltaIdentifier, Delta> entry : currentFullDeltas.entrySet()) {
            DeltaIdentifier key = entry.getKey();
            Delta value = entry.getValue();
            // determine half step deltas
            Quantity<MolarConcentration> fullConcentration = key.getUpdatable().getConcentration(key.getSection(), key.getEntity());
            Quantity<MolarConcentration> halfStepConcentration = fullConcentration.add(value.getQuantity().multiply(0.5));
            ConcentrationContainer halfConcentration;
            if (!halfConcentrations.containsKey(key.getUpdatable())) {
                halfConcentration = key.getUpdatable().getConcentrationContainer().emptyCopy();
                halfConcentration.set(key.getSection(), key.getEntity(), halfStepConcentration);
                halfConcentrations.put(key.getUpdatable(), halfConcentration);
            } else {
                halfConcentration = halfConcentrations.get(key.getUpdatable());
                halfConcentration.set(key.getSection(), key.getEntity(), halfStepConcentration);
            }
        }
    }

}
