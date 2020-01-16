package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class ErrorManager {

    private static final double DEFAULT_LOCAL_NUMERICAL_TOLERANCE = 0.01;
    private static final double DEFAULT_GLOBAL_NUMERICAL_TOLERANCE = 0.01;
    private static final double DEFAULT_LOCAL_DEVIATION_TOLERANCE = 0.2;
    private static final double DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF = 1e-100;
    private static final double DEFAULT_NUMERICAL_INSTABILITY_CUTOFF = 100;

    private NumericalError localNumericalError;
    private double localNumericalTolerance = DEFAULT_LOCAL_NUMERICAL_TOLERANCE;

    private NumericalError globalNumericalError;
    private double globalNumericalTolerance = DEFAULT_GLOBAL_NUMERICAL_TOLERANCE;

    private DisplacementDeviation largestLocalDeviation;
    private double localDisplacementTolerance = DEFAULT_LOCAL_DEVIATION_TOLERANCE;

    private UpdateModule localErrorModule;
    private double localErrorUpdate;

    private double numericalNegligenceCutoff = DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF;
    private double numericalInstabilityCutoff= DEFAULT_NUMERICAL_INSTABILITY_CUTOFF;

    public ErrorManager() {
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        globalNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        largestLocalDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
    }

    public void setLargestLocalError(NumericalError localError, UpdateModule associatedModule, double associatedConcentration) {
        if (localError.getValue() > localNumericalError.getValue()) {
            localNumericalError = localError;
            localErrorModule = associatedModule;
            localErrorUpdate = associatedConcentration;
        }
    }

    public NumericalError getLocalNumericalError() {
        return localNumericalError;
    }

    public void setLocalNumericalError(NumericalError largestNumericalLocalError) {
        this.localNumericalError = largestNumericalLocalError;
    }

    public NumericalError getGlobalNumericalError() {
        return globalNumericalError;
    }

    public void setGlobalNumericalError(NumericalError largestNumericalGlobalError) {
        this.globalNumericalError = largestNumericalGlobalError;
    }

    public void setLocalDisplacementDeviation(DisplacementDeviation largestDisplacementLocalError) {
        this.largestLocalDeviation = largestDisplacementLocalError;
    }

    public UpdateModule getLocalErrorModule() {
        return localErrorModule;
    }

    public void setLocalErrorModule(UpdateModule localErrorModule) {
        this.localErrorModule = localErrorModule;
    }

    public double getLocalErrorUpdate() {
        return localErrorUpdate;
    }

    public void setLocalErrorUpdate(double localErrorUpdate) {
        this.localErrorUpdate = localErrorUpdate;
    }

    public void resetAllErrors() {
        resetNumericalErrors();
        resetDisplacementDeviations();
    }

    public void resetNumericalErrors() {
        resetLocalNumericalError();
        resetGlobalNumericalError();
    }

    public void resetLocalNumericalError() {
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public void resetGlobalNumericalError() {
        globalNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public void resetDisplacementDeviations() {
        resetLocalDisplacementDeviation();
    }

    public void resetLocalDisplacementDeviation() {
        largestLocalDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
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
        return globalNumericalTolerance;
    }

    public void setGlobalNumericalTolerance(double globalNumericalTolerance) {
        this.globalNumericalTolerance = globalNumericalTolerance;
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
        if (globalNumericalError.getValue() != 0.0) {
            // calculate ratio of local and global error
            double errorRatio = localNumericalError.getValue() / globalNumericalError.getValue();
            errorRatioIsValid = errorRatio > 100000;
        }
        // use threshold
        boolean thresholdIsValid = localNumericalError.getValue() < localNumericalTolerance;
        return errorRatioIsValid || thresholdIsValid;
    }

    public boolean allErrorsAreSmall() {
        // global error was close to tolerance
        if (globalNumericalTolerance - globalNumericalError.getValue() <= 0.2 * globalNumericalTolerance) {
            return false;
        }
        // local error was close to tolerance
        if (localNumericalTolerance - localNumericalError.getValue() <= 0.2 * localNumericalTolerance) {
            return false;
        }
        // all error where small
        return true;
    }

}
