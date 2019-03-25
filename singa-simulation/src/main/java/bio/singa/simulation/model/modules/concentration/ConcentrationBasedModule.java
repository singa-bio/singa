package bio.singa.simulation.model.modules.concentration;

import bio.singa.chemistry.entities.ChemicalEntity;
import bio.singa.features.model.Feature;
import bio.singa.features.model.ScalableQuantitativeFeature;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.exceptions.NumericalInstabilityException;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.modules.concentration.functions.AbstractDeltaFunction;
import bio.singa.simulation.model.modules.concentration.scope.UpdateScope;
import bio.singa.simulation.model.modules.concentration.specifity.UpdateSpecificity;
import bio.singa.simulation.model.parameters.FeatureManager;
import bio.singa.simulation.model.simulation.Simulation;
import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

import static bio.singa.simulation.model.modules.concentration.ModuleState.*;

/**
 * Concentration based modules handle updates that are applied to the concentrations in {@link Updatable}s during a
 * simulation. Each module has a {@link UpdateScope} that regulates the dependence of this module to other parts of the
 * simulation. Further, the {@link UpdateSpecificity} defines how fine-grained the updates are calculated. New modules
 * should be created with the {@link ModuleFactory}.
 *
 * @author cl
 */
public abstract class ConcentrationBasedModule<DeltaFunctionType extends AbstractDeltaFunction> implements UpdateModule {

    /**
     * The logger
     */
    private static final Logger logger = LoggerFactory.getLogger(ConcentrationBasedModule.class);

    /**
     * The default value where deltas validated to be effectively zero.
     */
    private static final double DEFAULT_NUMERICAL_CUTOFF = 1e-100;

    /**
     * The default value where numerical errors to be considered irretrievably unstable.
     */
    private static final double DEFAULT_ERROR_CUTOFF = 100;

    /**
     * The identifier of this module.
     */
    private String identifier;

    /**
     * The current state of this module.
     */
    private ModuleState state;

    /**
     * The cutoff where deltas are validated to be effectively zero.
     */
    private double deltaCutoff = DEFAULT_NUMERICAL_CUTOFF;

    /**
     * The cutoff where numerical errors to be considered irretrievably unstable.
     */
    private double errorCutoff = DEFAULT_ERROR_CUTOFF;

    /**
     * The referenced simulation.
     */
    private Simulation simulation;

    /**
     * Frequently required fields.
     */
    protected FieldSupplier supplier;

    /**
     * The scope of this module.
     */
    private UpdateScope scope;

    /**
     * The specificity of this module.
     */
    private UpdateSpecificity<DeltaFunctionType> specificity;

    /**
     * The feature manager of this module
     */
    private FeatureManager featureManager;

    /**
     * Evaluated every time the module is applied to any updatable.
     */
    private Predicate<Updatable> applicationCondition;

    /**
     * All chemical entities that might be accessed by this module.
     */
    private Set<ChemicalEntity> referencedChemicalEntities;

    /**
     * Creates a new concentration based module.
     */
    public ConcentrationBasedModule() {
        supplier = new FieldSupplier();
        featureManager = new FeatureManager();
        referencedChemicalEntities = new HashSet<>();
        state = PENDING;
        applicationCondition = updatable -> true;
        identifier = getClass().getSimpleName();
    }

    @Override
    public void initialize() {

    }

    @Override
    public void run() {
        UpdateScheduler scheduler = getSimulation().getScheduler();
        while (state == PENDING || state == REQUIRING_RECALCULATION) {
            switch (state) {
                case PENDING:
                    // calculate update
                    logger.debug("calculating updates for {}.", Thread.currentThread().getName());
                    calculateUpdates();
                    break;
                case REQUIRING_RECALCULATION:
                    // optimize time step
                    logger.debug("{} requires recalculation.", Thread.currentThread().getName());
                    boolean prioritizedModule = scheduler.interruptAllBut(Thread.currentThread(), this);
                    if (prioritizedModule) {
                        optimizeTimeStep();
                    } else {
                        state = INTERRUPTED;
                    }
                    break;
            }
        }
        scheduler.getCountDownLatch().countDown();
        logger.debug("Module finished {}, latch at {}.", Thread.currentThread().getName(), scheduler.getCountDownLatch().getCount());
    }

    /**
     * Adds a delta function to the module. Delta functions are applied according to {@link UpdateScope} and {@link
     * UpdateSpecificity}.
     *
     * @param deltaFunction The delta function.
     */
    protected void addDeltaFunction(DeltaFunctionType deltaFunction) {
        specificity.addDeltaFunction(deltaFunction);
    }

    /**
     * Sets the application condition for this module. The module is only evaluated if the updatable fulfills the given
     * predicate.
     *
     * @param applicationCondition The application condition.
     */
    protected void setApplicationCondition(Predicate<Updatable> applicationCondition) {
        this.applicationCondition = applicationCondition;
    }

    /**
     * Returns the application condition for this module.The module is only evaluated if the updatable fulfills the
     * given predicate.
     *
     * @return The application condition.
     */
    public Predicate<Updatable> getApplicationCondition() {
        return applicationCondition;
    }

    /**
     * Returns all chemical entities that might be accessed by this module.
     *
     * @return All chemical entities that might be accessed by this module.
     */
    public Set<ChemicalEntity> getReferencedEntities() {
        return referencedChemicalEntities;
    }

    /**
     * Adds a referenced chemical entity.
     *
     * @param chemicalEntity The chemical entity.
     */
    protected void addReferencedEntity(ChemicalEntity chemicalEntity) {
        referencedChemicalEntities.add(chemicalEntity);
    }

    /**
     * Adds multiple referenced chemical entities.
     *
     * @param chemicalEntities The chemical entities.
     */
    protected void addReferencedEntities(Collection<? extends ChemicalEntity> chemicalEntities) {
        referencedChemicalEntities.addAll(chemicalEntities);
    }

    /**
     * Returns the cutoff where deltas are validated to be effectively zero.
     *
     * @return The delta cutoff.
     */
    public double getDeltaCutoff() {
        return deltaCutoff;
    }

    /**
     * Sets the cutoff where deltas are validated to be effectively zero.
     *
     * @param deltaCutoff The delta cutoff.
     */
    public void setDeltaCutoff(double deltaCutoff) {
        this.deltaCutoff = deltaCutoff;
    }

    /**
     * Returns the cutoff where numerical errors to be considered irretrievably unstable.
     *
     * @return The error cutoff.
     */
    public double getErrorCutoff() {
        return errorCutoff;
    }

    /**
     * Sets the cutoff where numerical errors to be considered irretrievably unstable.
     *
     * @param errorCutoff The error cutoff.
     */
    public void setErrorCutoff(double errorCutoff) {
        this.errorCutoff = errorCutoff;
    }

    /**
     * Returns the referenced simulation.
     *
     * @return The referenced simulation.
     */
    public Simulation getSimulation() {
        return simulation;
    }

    /**
     * References the simulation to this module.
     *
     * @param simulation The simulation.
     */
    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    /**
     * Returns the identifier of this module.
     *
     * @return The identifier.
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * Sets the identifier of this module.
     *
     * @param identifier The identifier.
     */
    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    /**
     * Returns the field supplier.
     *
     * @return The field supplier.
     */
    public FieldSupplier getSupplier() {
        return supplier;
    }

    /**
     * Returns the scope of this module.
     *
     * @return The scope of this module.
     */
    public UpdateScope getScope() {
        return scope;
    }

    /**
     * Sets the scope of this module.
     *
     * @param scope The scope of this module.
     */
    void setScope(UpdateScope scope) {
        this.scope = scope;
    }

    /**
     * Returns the specificity of this module.
     *
     * @return The specificity of this module.
     */
    public UpdateSpecificity getSpecificity() {
        return specificity;
    }

    /**
     * Sets the specificity of this module.
     *
     * @param specificity The specificity of this module.
     */
    void setSpecificity(UpdateSpecificity<DeltaFunctionType> specificity) {
        this.specificity = specificity;
    }

    @Override
    public ModuleState getState() {
        return state;
    }

    /**
     * Handles a delta based on the current state of the calculation.
     *
     * @param deltaIdentifier The unique identifier of the delta.
     * @param delta The delta itself.
     */
    public void handleDelta(ConcentrationDeltaIdentifier deltaIdentifier, ConcentrationDelta delta) {
        logDelta(deltaIdentifier, delta);
        if (supplier.isStrutCalculation()) {
            delta = delta.multiply(2.0);
            supplier.getCurrentHalfDeltas().put(deltaIdentifier, delta);
            deltaIdentifier.getUpdatable().addPotentialDelta(delta);
        } else {
            supplier.getCurrentFullDeltas().put(deltaIdentifier, delta);
        }
    }

    /**
     * Produces a log massage for the given update.
     *
     * @param deltaIdentifier THe delta identifier.
     * @param delta The delta.
     */
    private void logDelta(ConcentrationDeltaIdentifier deltaIdentifier, ConcentrationDelta delta) {
        logger.trace("{} delta for {} in {}:{} = {}",
                supplier.isStrutCalculation() ? "Half" : "Full",
                deltaIdentifier.getEntity().getIdentifier(),
                deltaIdentifier.getUpdatable().getStringIdentifier(),
                deltaIdentifier.getSubsection().getIdentifier(),
                delta.getValue());
    }

    /**
     * Returns true if the delta is valid, i.e. it is not zero and nor below the numerical threshold.
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is valid, i.e. it is not zero and nor below the numerical threshold.
     */
    public boolean deltaIsValid(ConcentrationDelta delta) {
        return deltaIsNotZero(delta) && deltaIsAboveNumericCutoff(delta);
    }

    /**
     * Returns true if the delta is not zero.
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is not zero.
     */
    private boolean deltaIsNotZero(ConcentrationDelta delta) {
        return delta.getValue() != 0.0;
    }

    /**
     * Returns true if the delta is above the numerical cutoff (not effectively zero).
     *
     * @param delta The delta to be evaluated.
     * @return true if the delta is above the numerical cutoff (not effectively zero).
     */
    private boolean deltaIsAboveNumericCutoff(ConcentrationDelta delta) {
        return Math.abs(delta.getValue()) > deltaCutoff;
    }

    /**
     * The local error is calculated and the largest local error of the current epoch resulting from the executing
     * module is returned. The local error is calculated according to the midpoint method E = abs(1 - (fullDelta / 2.0 *
     * halfDelta)). Intuitively, applying the the delta for the current time step once results in the same result as if
     * the delta for half the time step would be applied twice. This method calculates the difference between the full
     * delta and twice the half delta. If the difference is large the error is large and vice versa.
     *
     * @return The calculated local error.
     * @throws NumericalInstabilityException if any of the encountered errors is the result of an numerical
     * instability.
     */
    public LocalError determineLargestLocalError() {
        // no deltas mean this module did not change anything in the course of this simulation step
        if (supplier.getCurrentFullDeltas().isEmpty()) {
            return LocalError.MINIMAL_EMPTY_ERROR;
        }
        if (supplier.getCurrentFullDeltas().size() != supplier.getCurrentHalfDeltas().size()) {
            logger.trace("The deltas that should be applied have fallen below " +
                    "the threshold of " + deltaCutoff + ". (Module: " + getIdentifier() + ")");
            return LocalError.MINIMAL_EMPTY_ERROR;
        }

        // compare full and half deltas
        double largestLocalError = -Double.MAX_VALUE;
        ConcentrationDeltaIdentifier largestIdentifier = null;
        double associatedDelta = 0.0;
        for (ConcentrationDeltaIdentifier identifier : supplier.getCurrentFullDeltas().keySet()) {
            double fullDelta = supplier.getCurrentFullDeltas().get(identifier).getValue();
            double halfDelta = supplier.getCurrentHalfDeltas().get(identifier).getValue();
            // calculate error
            double localError = Math.abs(1 - (fullDelta / halfDelta));
            // check for numerical instabilities
            checkErrorStability(fullDelta, halfDelta, localError);
            // determine the largest error in the current deltas
            if (largestLocalError < localError) {
                largestIdentifier = identifier;
                largestLocalError = localError;
                associatedDelta = fullDelta;
            }
        }
        // safety check
        Objects.requireNonNull(largestIdentifier);
        LocalError localError = new LocalError(largestIdentifier.getUpdatable(), largestIdentifier.getEntity(), largestLocalError);
        logger.debug("The largest error for {} was {} at {}", Thread.currentThread().getName(), localError.getValue(), localError.getUpdatable().getStringIdentifier());
        // set local error and return local error
        simulation.getScheduler().setLargestLocalError(localError, this, associatedDelta);
        return localError;
    }

    /**
     * Determines if the current error can be considered stable (is above the error cutoff).
     *
     * @param fullDelta The full delta (only for logging purposes).
     * @param halfDelta The half delta (only for logging purposes).
     * @param error The error.
     */
    private void checkErrorStability(double fullDelta, double halfDelta, double error) {
        if (error > errorCutoff) {
            throw new NumericalInstabilityException("The module " + toString() + " experiences numerical instabilities. " +
                    "The local error between the full step delta (" + fullDelta + ") and half step delta (" + halfDelta +
                    ") is " + error + ". This can be an result of time steps that have been initially chosen too large" +
                    " or an implementation error in module that calculated the delta.");
        }
    }


    @Override
    public void calculateUpdates() {
        scope.processAllUpdatables(simulation.getUpdatables());
        evaluateModuleState();
    }

    @Override
    public void optimizeTimeStep() {
        Updatable updatable = supplier.getLargestLocalError().getUpdatable();
        while (state == REQUIRING_RECALCULATION) {
            // reset previous error
            supplier.resetError();
            // determine new local error with decreased time step
            simulation.getScheduler().decreaseTimeStep();
            scope.processUpdatable(updatable);
            // evaluate module state by error
            evaluateModuleState();
        }
        logger.debug("Optimized local error for {} was {} with time step of {}.", this, supplier.getLargestLocalError().getValue(), UnitRegistry.getTime());
    }

    /**
     * Evaluates the current state of the module. This includes evaluating the local error and if necessary scheduling
     * a recalculation.
     */
    private void evaluateModuleState() {
        // calculate ration of local and global error
        if (localErrorIsAcceptable()) {
            state = SUCCEEDED;
        } else {
            logger.trace("Recalculation required for error {}.", supplier.getLargestLocalError().getValue());
            state = REQUIRING_RECALCULATION;
            supplier.clearDeltas();
            scope.clearPotentialDeltas();
        }
    }

    private boolean localErrorIsAcceptable() {
        boolean errorRatioIsValid = false;
        if (simulation.getScheduler().getLargestGlobalError() != 0.0) {
            // calculate ratio of local and global error
            double errorRatio = supplier.getLargestLocalError().getValue() / simulation.getScheduler().getLargestGlobalError();
            errorRatioIsValid = errorRatio > 100000;
        }
        // use threshold
        boolean thresholdIsValid = supplier.getLargestLocalError().getValue() < simulation.getScheduler().getRecalculationCutoff();
        return errorRatioIsValid || thresholdIsValid;
    }

    @Override
    public void resetState() {
        state = PENDING;
        supplier.resetError();
    }

    @Override
    public Set<Class<? extends Feature>> getRequiredFeatures() {
        return featureManager.getRequiredFeatures();
    }

    @Override
    public double getScaledFeature(Class<? extends ScalableQuantitativeFeature<?>> featureClass) {
        // feature from the module (like reaction rates)
        return choseScaling(featureManager.getFeature(featureClass));
    }

    /**
     * Returns the requested feature of the given chemical entities, scaled according to the current time step and
     * spatial scaling.
     *
     * @param entity The chemical entity.
     * @param featureClass The requested feature.
     * @return The scaled feature.
     */
    protected double getScaledFeature(ChemicalEntity entity, Class<? extends ScalableQuantitativeFeature<?>> featureClass) {
        // feature from any entity (like molar mass)
        return choseScaling(entity.getFeature(featureClass));
    }

    /**
     * Determines the correct scaling based on the state of the strut calculation.
     *
     * @param feature The requested feature.
     * @return The scaled feature.
     */
    private double choseScaling(ScalableQuantitativeFeature<?> feature) {
        if (supplier.isStrutCalculation()) {
            return feature.getHalfScaledQuantity();
        }
        return feature.getScaledQuantity();
    }

    /**
     * Returns the requested feature.
     *
     * @param featureTypeClass The requested feature.
     * @param <FeatureType> The resulting content type.
     * @return The requested feature.
     */
    public <FeatureType extends Feature> FeatureType getFeature(Class<FeatureType> featureTypeClass) {
        return featureManager.getFeature(featureTypeClass);
    }

    /**
     * Sets a feature.
     *
     * @param feature The feature.
     */
    public void setFeature(Feature<?> feature) {
        featureManager.setFeature(feature);
    }

    /**
     * Returns all features of this module (not its entities).
     *
     * @return all features of this module.
     */
    public Collection<Feature<?>> getFeatures() {
        return featureManager.getAllFeatures();
    }

    @Override
    public void onReset() {

    }

    @Override
    public void onCompletion() {

    }

    @Override
    public void checkFeatures() {
        outer:
        for (Class<? extends Feature> featureClass : getRequiredFeatures()) {
            for (Feature<?> feature : featureManager.getFeatures()) {
                if (featureClass.isInstance(feature)) {
                    // logger.info("Required feature {}: {} will be used and is set to {}.", featureClass.getSimpleName(), feature.getClass().getSimpleName(), feature.getContent());
                    continue outer;
                }
            }
            logger.warn("Required feature {} has not been set.", featureClass.getSimpleName());
        }
    }

    @Override
    public String toString() {
        return getIdentifier();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcentrationBasedModule<?> that = (ConcentrationBasedModule<?>) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

}
