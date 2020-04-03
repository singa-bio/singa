package bio.singa.simulation.model.simulation.error;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;
import bio.singa.simulation.model.simulation.UpdateScheduler;

/**
 * @author cl
 */
public class ErrorManager {

    private static final double DEFAULT_LOCAL_NUMERICAL_TOLERANCE = 0.01;

    private static final double DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF = 1e-100;
    private static final double DEFAULT_NUMERICAL_INSTABILITY_CUTOFF = 100;

    private static final double DEFAULT_DISPLACEMENT_REFERENCE_FACTOR = 1.0 / 10.0;

    /**
     * If the current displacement is equal to the displacement reference length the deviation is zero.
     * Deviations above the threshold will result in recalculation.
     */
    private static final double DEFAULT_DISPLACEMENT_UPPER_THRESHOLD = 0;

    /**
     * If the current displacement is half of the displacement reference length the deviation is log(0.5).
     * Deviations below this threshold might result in time step increases (depending on other errors).
     */
    private static final double DEFAULT_DISPLACEMENT_LOWER_THRESHOLD = Math.log10(0.5);

    private GlobalNumericalErrorManager globalErrorManager;
    private GlobalDisplacementDeviationManager globalDeviationManager;

    private double localNumericalTolerance = DEFAULT_LOCAL_NUMERICAL_TOLERANCE;
    private NumericalError localNumericalError;
    private UpdateModule localNumericalErrorModule;
    private double localNumericalErrorUpdate;

    private double numericalNegligenceCutoff = DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF;
    private double numericalInstabilityCutoff = DEFAULT_NUMERICAL_INSTABILITY_CUTOFF;

    /**
     * Deviations above the threshold will result in recalculation.
     */
    private double displacementUpperThreshold = DEFAULT_DISPLACEMENT_UPPER_THRESHOLD;
    /**
     * Deviations below this threshold might result in time step increases (depending on other errors).
     */
    private double displacementLowerThreshold = DEFAULT_DISPLACEMENT_LOWER_THRESHOLD;
    private DisplacementDeviation localDisplacementDeviation;
    private UpdateModule localDisplacementDeviationModule;

    private double displacementReferenceFactor = DEFAULT_DISPLACEMENT_REFERENCE_FACTOR;
    private double displacementReferenceLength;

    private UpdateScheduler scheduler;

    public ErrorManager(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        localDisplacementDeviation = DisplacementDeviation.MAXIMAL_NEGATIVE_DEVIATION;
    }

    public void initialize() {
        globalErrorManager = new GlobalNumericalErrorManager(scheduler);
        globalDeviationManager = new GlobalDisplacementDeviationManager(scheduler);
        displacementReferenceLength = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace().multiply(displacementReferenceFactor));
        // register error manager that are interested time step changes
        TimeStepManager.addListener(globalErrorManager);
//        TimeStepManager.addListener(globalDeviationManager);
    }

    public void setLargestLocalNumericalError(NumericalError localError, UpdateModule associatedModule, double associatedConcentration) {
        if (localError.getValue() > localNumericalError.getValue()) {
            localNumericalError = localError;
            localNumericalErrorModule = associatedModule;
            localNumericalErrorUpdate = associatedConcentration;
        }
    }

    public void setLargestLocalDisplacementDeviation(DisplacementDeviation localDeviation, UpdateModule associatedModule) {
        if (localDisplacementDeviation.getValue() < localDeviation.getValue()) {
            localDisplacementDeviation = localDeviation;
            localDisplacementDeviationModule = associatedModule;
        }
    }

    public NumericalError getLocalNumericalError() {
        return localNumericalError;
    }

    public NumericalError getGlobalNumericalError() {
        return globalErrorManager.getError();
    }

    public DisplacementDeviation getGlobalDisplacementDeviation() {
        return globalDeviationManager.getDeviation();
    }

    public double getDisplacementReferenceLength() {
        return displacementReferenceLength;
    }

    public double getDisplacementUpperThreshold() {
        return displacementUpperThreshold;
    }

    public void setDisplacementUpperThreshold(double displacementUpperThreshold) {
        this.displacementUpperThreshold = displacementUpperThreshold;
    }

    public double getDisplacementLowerThreshold() {
        return displacementLowerThreshold;
    }

    public void setDisplacementLowerThreshold(double displacementLowerThreshold) {
        this.displacementLowerThreshold = displacementLowerThreshold;
    }

    public void setDisplacementReferenceLength(double displacementReferenceLength) {
        this.displacementReferenceLength = displacementReferenceLength;
    }

    public double getDisplacementReferenceFactor() {
        return displacementReferenceFactor;
    }

    public void setDisplacementReferenceFactor(double displacementReferenceFactor) {
        displacementReferenceLength = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace().multiply(displacementReferenceFactor));
        this.displacementReferenceFactor = displacementReferenceFactor;
    }

    public UpdateModule getLocalNumericalErrorModule() {
        return localNumericalErrorModule;
    }

    public void setLocalErrorModule(UpdateModule localErrorModule) {
        this.localNumericalErrorModule = localErrorModule;
    }

    public double getLocalNumericalErrorUpdate() {
        return localNumericalErrorUpdate;
    }

    public void setLocalNumericalErrorUpdate(double localErrorUpdate) {
        this.localNumericalErrorUpdate = localErrorUpdate;
    }

    public UpdateModule getLocalDisplacementDeviationModule() {
        return localDisplacementDeviationModule;
    }

    public void resetAllErrors() {
        resetNumericalErrors();
        resetDisplacementDeviations();
    }

    public void resetNumericalErrors() {
        resetLocalNumericalError();
        resetGlobalNumericalError();
    }

    public void resetDisplacementDeviations() {
        resetLocalDisplacementDeviation();
        resetGlobalDisplacementDeviation();
    }

    public void resetLocalErrors() {
        resetLocalNumericalError();
        resetLocalDisplacementDeviation();
    }

    public void resetLocalNumericalError() {
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public void resetGlobalNumericalError() {
        globalErrorManager.reset();
    }

    public void resetLocalDisplacementDeviation() {
        localDisplacementDeviation = DisplacementDeviation.MAXIMAL_NEGATIVE_DEVIATION;
    }

    public void resetGlobalDisplacementDeviation() {
        globalDeviationManager.reset();
    }

    public double getLocalNumericalTolerance() {
        return localNumericalTolerance;
    }

    public void setLocalNumericalTolerance(double localNumericalTolerance) {
        this.localNumericalTolerance = localNumericalTolerance;
    }

    public double getGlobalNumericalTolerance() {
        return globalErrorManager.getTolerance();
    }

    public void setGlobalNumericalTolerance(double globalNumericalTolerance) {
        globalErrorManager.setTolerance(globalNumericalTolerance);
    }

    public double getNumericalNegligenceCutoff() {
        return numericalNegligenceCutoff;
    }

    public void setNumericalNegligenceCutoff(double numericalNegligenceCutoff) {
        this.numericalNegligenceCutoff = numericalNegligenceCutoff;
    }

    public double getNumericalInstabilityCutoff() {
        return numericalInstabilityCutoff;
    }

    public void setNumericalInstabilityCutoff(double numericalInstabilityCutoff) {
        this.numericalInstabilityCutoff = numericalInstabilityCutoff;
    }

    public boolean localErrorIsAcceptable(NumericalError localNumericalError) {
        boolean errorRatioIsValid = false;
        if (globalErrorManager.getError().getValue() != 0.0) {
            // calculate ratio of local and global error
            double errorRatio = localNumericalError.getValue() / globalErrorManager.getError().getValue();
            errorRatioIsValid = errorRatio > 100000;
        }
        // use threshold
        boolean thresholdIsValid = localNumericalError.getValue() < localNumericalTolerance;
        return errorRatioIsValid || thresholdIsValid;
    }

    public void evaluateGlobalError() {
        globalErrorManager.evaluateError();
    }

    public boolean globalErrorIsAcceptable() {
        return globalErrorManager.errorIsAcceptable();
    }

    public void evaluateGlobalDeviation() {
        globalDeviationManager.evaluateDeviation();
    }

    public boolean globalDeviationIsAcceptable() {
        return globalDeviationManager.deviationIsAcceptable();
    }

    public void resolveGlobalDeviationProblem() {
        globalDeviationManager.resolveProblem();
    }

    public void resolveGlobalErrorProblem() {
        globalErrorManager.resolveProblem();
    }

    public boolean allErrorsAreSafe() {
        if (globalErrorManager.errorIsCritical()) {
            return false;
        }
        if (globalDeviationManager.deviationIsCritical()) {
            return false;
        }
        // local numerical error was close to tolerance
        if (localNumericalTolerance - localNumericalError.getValue() <= 0.25 * localNumericalTolerance) {
            return false;
        }
        // local displacement deviation above lower threshold
        if (localDisplacementDeviation.getValue() > displacementLowerThreshold) {
            return false;
        }
        // all errors where small
        return true;
    }

    public enum CalculationStage {
        SETUP_STAGE, EVALUATION_STAGE, TIME_STEP_RESCALED, SKIP;
    }

    public enum Reason {
        LOCAL_ERROR, NEGATIVE_CONCENTRATIONS, GLOBAL_ERROR, LOCAL_DEVIATION, GLOBAL_DEVIATION, INCREASE;
    }

}
