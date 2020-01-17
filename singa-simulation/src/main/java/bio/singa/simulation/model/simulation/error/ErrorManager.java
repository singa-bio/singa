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

    private static final double DEFAULT_DISPLACEMENT_CUTOFF_FACTOR = 1.0 / 10.0;
    private static final double DEFAULT_LOCAL_DEVIATION_TOLERANCE = 0.8;
    private static final double DEFAULT_GLOBAL_DEVIATION_TOLERANCE = 0.8;

    private GlobalNumericalErrorManager globalErrorManager;
    private GlobalDisplacementDeviationManager globalDeviationManager;

    private double localNumericalTolerance = DEFAULT_LOCAL_NUMERICAL_TOLERANCE;
    private NumericalError localNumericalError;
    private UpdateModule localNumericalErrorModule;
    private double localNumericalErrorUpdate;

    private double numericalNegligenceCutoff = DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF;
    private double numericalInstabilityCutoff = DEFAULT_NUMERICAL_INSTABILITY_CUTOFF;

    private double localDisplacementTolerance = DEFAULT_LOCAL_DEVIATION_TOLERANCE;
    private DisplacementDeviation localDisplacementDeviation;
    private UpdateModule localDisplacementDeviationModule;

    private double displacementCutoffFactor = DEFAULT_DISPLACEMENT_CUTOFF_FACTOR;
    private double displacementCutoff;

    private UpdateScheduler scheduler;

    public ErrorManager(UpdateScheduler scheduler) {
        this.scheduler = scheduler;
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        localDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
    }

    public void initialize() {
        globalErrorManager = new GlobalNumericalErrorManager(scheduler);
        globalDeviationManager = new GlobalDisplacementDeviationManager(scheduler);
        displacementCutoff = Environment.convertSystemToSimulationScale(UnitRegistry.getSpace().multiply(displacementCutoffFactor));
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

    public double getDisplacementCutoff() {
        return displacementCutoff;
    }

    public void setDisplacementCutoff(double displacementCutoff) {
        this.displacementCutoff = displacementCutoff;
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
        localDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
    }

    public void resetGlobalDisplacementDeviation() {
        globalDeviationManager.reset();
    }

    public double getLocalDisplacementTolerance() {
        return localDisplacementTolerance;
    }

    public void setLocalDisplacementTolerance(double localDisplacementTolerance) {
        this.localDisplacementTolerance = localDisplacementTolerance;
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

    public void resolveDeviationProblem() {
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
        if (localNumericalTolerance - localNumericalError.getValue() <= 0.2 * localNumericalTolerance) {
            return false;
        }
        // local displacement deviation was close to tolerance
        if (localDisplacementDeviation.getValue() >= localDisplacementTolerance) {
            return false;
        }
        // all errors where small
        return true;
    }

}
