package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class ErrorManager {

    private static final double DEFAULT_LOCAL_NUMERICAL_TOLERANCE = 0.01;
    private static final double DEFAULT_GLOBAL_NUMERICAL_TOLERANCE = 0.01;
    private static final double DEFAULT_LOCAL_DEVIATION_TOLERANCE = 0.2;
    private static final double DEFAULT_NUMERICAL_CUTOFF = 1e-100;

    private NumericalError largestNumericalLocalError;
    private double localNumericalTolerance = DEFAULT_LOCAL_NUMERICAL_TOLERANCE;

    private NumericalError largestNumericalGlobalError;
    private double globalNumericalTolerance = DEFAULT_GLOBAL_NUMERICAL_TOLERANCE;

    private DisplacementDeviation largestLocalDeviation;
    private double localDisplacementTolerance = DEFAULT_LOCAL_DEVIATION_TOLERANCE;

    private UpdateModule localErrorModule;
    private double localErrorUpdate;

    private double numericalCutoff = DEFAULT_NUMERICAL_CUTOFF;

    public ErrorManager() {
        largestNumericalLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
        largestNumericalGlobalError = NumericalError.MINIMAL_EMPTY_ERROR;
        largestLocalDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
    }

    public void setLargestLocalError(NumericalError localError, UpdateModule associatedModule, double associatedConcentration) {
        if (localError.getValue() > largestNumericalLocalError.getValue()) {
            largestNumericalLocalError = localError;
            localErrorModule = associatedModule;
            localErrorUpdate = associatedConcentration;
        }
    }

    public NumericalError getLocalNumericalError() {
        return largestNumericalLocalError;
    }

    public void setLocalNumericalError(NumericalError largestNumericalLocalError) {
        this.largestNumericalLocalError = largestNumericalLocalError;
    }

    public NumericalError getGlobalNumericalError() {
        return largestNumericalGlobalError;
    }

    public void setGlobalNumericalError(NumericalError largestNumericalGlobalError) {
        this.largestNumericalGlobalError = largestNumericalGlobalError;
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
        largestNumericalLocalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public void resetGlobalNumericalError() {
        largestNumericalGlobalError = NumericalError.MINIMAL_EMPTY_ERROR;
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

    public double getNumericalCutoff() {
        return numericalCutoff;
    }

    public void setNumericalCutoff(double numericalCutoff) {
        this.numericalCutoff = numericalCutoff;
    }
}
