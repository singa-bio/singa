package de.bioforscher.singa.simulation.modules.model;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Featureable;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.simulation.exceptions.NumericalInstabilityException;
import de.bioforscher.singa.simulation.model.compartments.CellSection;
import de.bioforscher.singa.simulation.model.graphs.AutomatonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author cl
 */
public abstract class AbstractModule implements Module {

    /**
     * The logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(AbstractModule.class);

    /**
     * The default value where deltas validated to be effectively zero.
     */
    public static final double DEFAULT_NUMERICAL_CUTOFF = 1e-100;

    /**
     * The simulation that the module is applied to.
     */
    protected Simulation simulation;

    /**
     * The deltas for applying full time steps.
     */
    protected final Map<DeltaIdentifier, Delta> currentFullDeltas;

    /**
     * The strut deltas for the half time steps.
     */
    protected final Map<DeltaIdentifier, Delta> currentHalfDeltas;

    /**
     * The predicate is evaluated before applying the module to a node. If the predicate returns true the module is
     * evaluated for the node.
     */
    protected Predicate<AutomatonNode> conditionalApplication = automatonNode -> true;

    /**
     * The numerical cutoff is applied before adding any delta to the system. If the absolute delta value is smaller
     * than the cutoff value its influence is assumed to be void (effectively zero).
     */
    private double numericalCutoff = DEFAULT_NUMERICAL_CUTOFF;

    /**
     * The largest error that occurred while calculating the deltas for this module.
     */
    protected LocalError largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    ;

    /**
     * If this flag is true, half time steps are calculated.
     */
    protected boolean halfTime;

    /**
     * The automaton node that is currently processed.
     */
    protected AutomatonNode currentNode;

    /**
     * The cell section that is currently processed.
     */
    protected CellSection currentCellSection;

    /**
     * The chemical entity that is currently processed.
     */
    protected ChemicalEntity<?> currentChemicalEntity;

    /**
     * Creates a new module.
     *
     * @param simulation The simulation that the module is applied to.
     */
    public AbstractModule(Simulation simulation) {
        this.simulation = simulation;
        // initialize maps
        currentFullDeltas = new HashMap<>();
        currentHalfDeltas = new HashMap<>();
    }

    /**
     * Returns the simulation that the module is applied to.
     *
     * @return The simulation that the module is applied to.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * Sets the simulation this module is applied to.
     *
     * @param simulation The simulation that the module is applied to.
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Returns the automaton node that is currently processed.
     *
     * @return The automaton node that is currently processed.
     */
    public AutomatonNode getCurrentNode() {
        return currentNode;
    }

    /**
     * Returns the cell section that is currently processed.
     * @return The cell section that is currently processed.
     */
    public CellSection getCurrentCellSection() {
        return currentCellSection;
    }

    /**
     * Returns the chemical entity that is currently processed.
     * @return The chemical entity that is currently processed.
     */
    public ChemicalEntity<?> getCurrentChemicalEntity() {
        return currentChemicalEntity;
    }

    /**
     * The passed predicate is evaluated before applying the module to a node. If the predicate returns true the module
     * is evaluated for the node.
     * @param predicate The predicate to be evaluated.
     */
    public void onlyApplyIf(Predicate<AutomatonNode> predicate) {
        conditionalApplication = predicate;
    }

    /**
     * Return the numerical cutoff that is applied before adding any delta to the system. If the absolute delta value is
     * smaller than the cutoff value its influence is assumed to be void (effectively zero).
     * @return The numerical cutoff.
     */
    public double getNumericalCutoff() {
        return numericalCutoff;
    }

    /**
     * Sets the numerical cutoff that is applied before adding any delta to the system. If the absolute delta value is
     * smaller than the cutoff value its influence is assumed to be void (effectively zero).
     * @param numericalCutoff The numerical cutoff.
     */
    public void setNumericalCutoff(double numericalCutoff) {
        this.numericalCutoff = numericalCutoff;
    }

    /**
     * Returns the feature for the entity if possible.
     * @param entity
     * @param featureClass
     * @param <FeatureContent>
     * @return
     */
    protected <FeatureContent> FeatureContent getFeature(Featureable entity, Class<? extends ScalableFeature<FeatureContent>> featureClass) {
        ScalableFeature<FeatureContent> feature = entity.getFeature(featureClass);
        if (halfTime) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    @Override
    public LocalError getLargestLocalError() {
        return largestLocalError;
    }

    @Override
    public void resetLargestLocalError() {
        largestLocalError = LocalError.MINIMAL_EMPTY_ERROR;
    }

    void applyHalfDelta(Delta halfDelta) {
        if (deltaIsValid(halfDelta)) {
            halfDelta = halfDelta.multiply(2.0);
            logger.trace("Calculated half delta for {} in {}: {}", currentChemicalEntity.getName(), currentCellSection.getIdentifier(), halfDelta.getQuantity());
            currentHalfDeltas.put(new DeltaIdentifier(currentNode, currentCellSection, currentChemicalEntity), halfDelta);
            currentNode.addPotentialDelta(halfDelta);
        }
    }

    boolean deltaIsValid(Delta delta) {
        return deltaIsNotZero(delta) && deltaIsAboveNumericCutoff(delta);
    }

    private boolean deltaIsNotZero(Delta delta) {
        return delta.getQuantity().getValue().doubleValue() != 0.0;
    }

    private boolean deltaIsAboveNumericCutoff(Delta delta) {
        return Math.abs(delta.getQuantity().getValue().doubleValue()) > numericalCutoff;
    }

    LocalError determineLargestLocalError() {
        // no deltas mean this module did not change anything in the course of this simulation step
        if (currentFullDeltas.isEmpty()) {
            return LocalError.MINIMAL_EMPTY_ERROR;
        }
        double largestLocalError = -Double.MAX_VALUE;
        DeltaIdentifier largestIdentifier = null;
        for (DeltaIdentifier identifier : currentFullDeltas.keySet()) {
            double fullDelta = currentFullDeltas.get(identifier).getQuantity().getValue().doubleValue();
            double halfDelta = currentHalfDeltas.get(identifier).getQuantity().getValue().doubleValue();
            if (halfDelta == 0.0) {
                continue;
            }

            // calculate error
            double localError = Math.abs(1 - (fullDelta / halfDelta));
            // check for numerical instabilities
            if (localError > 100.0) {
                throw new NumericalInstabilityException("The simulation experiences numerical instabilities. The local error between the full step delta (" + fullDelta + ") and half step delta (" + halfDelta + ") is " + localError + ". This can be an result of time steps that have been initially chosen too large or an implementation error in module that calculated the delta.");
            }
            // determine the largest error in the current deltas
            if (largestLocalError < localError) {
                largestIdentifier = identifier;
                largestLocalError = localError;
            }
        }
        // largest identifier being null is a result of half deltas being blocked by numerical validity check
        if (largestIdentifier == null) {
            return LocalError.MINIMAL_EMPTY_ERROR;
        }
        // Objects.requireNonNull(largestIdentifier);
        // set local error and return local error
        return new LocalError(largestIdentifier.getNode(), largestIdentifier.getEntity(), largestLocalError);

    }

}
