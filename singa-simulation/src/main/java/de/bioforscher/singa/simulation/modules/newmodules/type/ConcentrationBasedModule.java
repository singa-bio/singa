package de.bioforscher.singa.simulation.modules.newmodules.type;

import de.bioforscher.singa.chemistry.descriptive.entities.ChemicalEntity;
import de.bioforscher.singa.features.model.Feature;
import de.bioforscher.singa.features.model.ScalableFeature;
import de.bioforscher.singa.features.parameters.Environment;
import de.bioforscher.singa.simulation.exceptions.NumericalInstabilityException;
import de.bioforscher.singa.simulation.modules.model.*;
import de.bioforscher.singa.simulation.modules.newmodules.FieldSupplier;
import de.bioforscher.singa.simulation.modules.newmodules.UpdateScheduler;
import de.bioforscher.singa.simulation.modules.newmodules.scope.UpdateScope;
import de.bioforscher.singa.simulation.modules.newmodules.specifity.UpdateSpecificity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static de.bioforscher.singa.simulation.modules.newmodules.type.ModuleState.*;

/**
 * @author cl
 */
public class ConcentrationBasedModule implements UpdateModule {

    private static final Logger logger = LoggerFactory.getLogger(ConcentrationBasedModule.class);

    /**
     * The default value where deltas validated to be effectively zero.
     */
    private static final double DEFAULT_NUMERICAL_CUTOFF = 1e-100;

    /**
     * The default value where numerical errors to be considered irretrievably unstable
     */
    private static final double DEFAULT_ERROR_CUTOFF = 100;

    /**
     * The default value where errors are considered too large and the time step is reduced.
     */
    private static final double DEFALUT_RECALCULATION_CUTOFF = 0.01;

    private Simulation simulation;
    private FieldSupplier supplier;
    private UpdateScope scope;
    private UpdateSpecificity specificity;
    private UpdateScheduler updateScheduler;
    private ModuleFeatureManager featureManager;
    private Predicate<Updatable> applicationCondition;
    private Set<ChemicalEntity> referencedChemicalEntities;

    private ModuleState state;
    private double deltaCutoff = DEFAULT_NUMERICAL_CUTOFF;
    private double errorCutoff = DEFAULT_ERROR_CUTOFF;
    private double recalculationCutoff = DEFALUT_RECALCULATION_CUTOFF;

    ConcentrationBasedModule(Simulation simulation, FieldSupplier supplier, UpdateScope scope, UpdateSpecificity specificity, UpdateScheduler updateScheduler) {
        this.simulation = simulation;
        this.supplier = supplier;
        this.scope = scope;
        this.specificity = specificity;
        this.updateScheduler = updateScheduler;
        referencedChemicalEntities = new HashSet<>();
        state = PENDING;
        applicationCondition = updatable -> true;
    }

    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }

    public void addReferencedEntity(ChemicalEntity chemicalEntity) {
        referencedChemicalEntities.add(chemicalEntity);
    }

    public void addReferencedEntities(Collection<? extends ChemicalEntity> chemicalEntities) {
        referencedChemicalEntities.addAll(chemicalEntities);
    }

    public double getDeltaCutoff() {
        return deltaCutoff;
    }

    public void setDeltaCutoff(double deltaCutoff) {
        this.deltaCutoff = deltaCutoff;
    }

    public double getErrorCutoff() {
        return errorCutoff;
    }

    public void setErrorCutoff(double errorCutoff) {
        this.errorCutoff = errorCutoff;
    }

    public double getRecalculationCutoff() {
        return recalculationCutoff;
    }

    public void setRecalculationCutoff(double recalculationCutoff) {
        this.recalculationCutoff = recalculationCutoff;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public Predicate<Updatable> getApplicationCondition() {
        return applicationCondition;
    }

    public FieldSupplier getSupplier() {
        return supplier;
    }

    public UpdateScope getScope() {
        return scope;
    }

    public UpdateSpecificity getSpecificity() {
        return specificity;
    }

    public void handleDelta(DeltaIdentifier deltaIdentifier, Delta delta) {
        logDelta(delta);
        if (supplier.isStrutCalculation()) {
            delta = delta.multiply(2.0);
            supplier.getCurrentHalfDeltas().put(deltaIdentifier, delta);
            supplier.getCurrentUpdatable().addPotentialDelta(delta);
        } else {
            supplier.getCurrentFullDeltas().put(deltaIdentifier, delta);
        }
    }

    private void logDelta(Delta delta) {
        logger.trace("{} delta for {} in {}:{} = {}",
                supplier.isStrutCalculation() ? "Full" : "Half",
                supplier.getCurrentEntity().getIdentifier(),
                supplier.getCurrentUpdatable().getStringIdentifier(),
                supplier.getCurrentSubsection().getIdentifier(),
                delta.getQuantity());
    }

    /**
     * Returns true if the delta is valid, i.e. it is not zero and nor below the numerical threshold.
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is valid, i.e. it is not zero and nor below the numerical threshold.
     */
    public boolean deltaIsValid(Delta delta) {
        return deltaIsNotZero(delta) && deltaIsAboveNumericCutoff(delta);
    }

    /**
     * Returns true if the delta is not zero.
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is not zero.
     */
    private boolean deltaIsNotZero(Delta delta) {
        return delta.getQuantity().getValue().doubleValue() != 0.0;
    }

    /**
     * Returns true if the delta is above the numerical cutoff (not effectivley zero).
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is above the numerical cutoff (not effectivley zero).
     */
    private boolean deltaIsAboveNumericCutoff(Delta delta) {
        return Math.abs(delta.getQuantity().getValue().doubleValue()) > deltaCutoff;
    }

    private void checkErrorStability(double fullDelta, double halfDelta, double error) {
        if (error > errorCutoff) {
            throw new NumericalInstabilityException("The simulation experiences numerical instabilities. The local " +
                    "error between the full step delta (" + fullDelta + ") and half step delta (" + halfDelta + ") is "
                    + error + ". This can be an result of time steps that have been initially chosen too large" +
                    " or an implementation error in module that calculated the delta.");
        }
    }

    /**
     * The local error is calculated and the largest local error of the current epoch resulting from the executing
     * module is returned. The local error is calculated according to the midpoint method
     * E = abs(1 - (fullDelta / 2.0 * halfDelta)). Intuitively, applying the the delta for the current time step once
     * results in the same result as if the delta for half the time step would be applied twice. This method calculates
     * the difference between the full delta and twice the half delta. If the difference is large the error is large and
     * vice versa.
     *
     * @return The calculated local error.
     * @throws NumericalInstabilityException if any of the encountered errors is the result of an numerical instability.
     */
    public LocalError determineLargestLocalError() {
        // no deltas mean this module did not change anything in the course of this simulation step
        if (supplier.getCurrentFullDeltas().isEmpty()) {
            return LocalError.MINIMAL_EMPTY_ERROR;
        }
        // compare full and half deltas
        double largestLocalError = -Double.MAX_VALUE;
        DeltaIdentifier largestIdentifier = null;
        for (DeltaIdentifier identifier : supplier.getCurrentFullDeltas().keySet()) {
            double fullDelta = supplier.getCurrentFullDeltas().get(identifier).getQuantity().getValue().doubleValue();
            double halfDelta = supplier.getCurrentHalfDeltas().get(identifier).getQuantity().getValue().doubleValue();
            // calculate error
            double localError = Math.abs(1 - (fullDelta / halfDelta));
            // check for numerical instabilities
            checkErrorStability(fullDelta, halfDelta, localError);
            // determine the largest error in the current deltas
            if (largestLocalError < localError) {
                largestIdentifier = identifier;
                largestLocalError = localError;
            }
        }
        // safety check
        Objects.requireNonNull(largestIdentifier);
        // set local error and return local error
        return new LocalError(largestIdentifier.getUpdatable(), largestIdentifier.getEntity(), largestLocalError);
    }

    @Override
    public void calculateUpdates() {
        scope.processAllUpdatables(simulation.getUpdatables());
    }

    @Override
    public void optimizeTimeStep() {
        Updatable updatable = supplier.getLargestLocalError().getUpdatable();
        while (state == RECALCULATION_REQUIRED) {
            // determine new local error with decreased time step
            supplier.resetError();
            updateScheduler.decreaseTimeStep();
            scope.processUpdatable(updatable);
            evaluateModuleState();
        }
        logger.debug("Optimized local error for {} was {} with time step of {}.", this, supplier.getLargestLocalError().getValue(), Environment.getTimeStep());
        supplier.resetError();
    }

    private void evaluateModuleState() {
        if (supplier.getLargestLocalError().getValue() < recalculationCutoff) {
            state = SUCCEEDED;
        } else {
            state = RECALCULATION_REQUIRED;
        }
    }

    @Override
    public ModuleState getState() {
        return state;
    }

    @Override
    public void setState(ModuleState state) {
        this.state = state;
    }

    @Override
    public void rescaleParameters() {
        featureManager.scaleScalableFeatures();
    }

    @Override
    public <FeatureContentType extends Quantity<FeatureContentType>> Quantity<FeatureContentType> getScaledFeature(Class<? extends ScalableFeature<FeatureContentType>> featureClass) {
        return featureManager.getScaledFeature(featureClass);
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        // TODO IMPLEMENT ME
        return null;
    }
}
