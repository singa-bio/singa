package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class ErrorManager {

    private NumericalError largestNumericalLocalError;
    private UpdateModule localErrorModule;
    private double localErrorUpdate;

    private NumericalError largestNumericalGlobalError;

    private DisplacementDeviation largestLocalDeviation;

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

    public void setLocalNumericalError(NumericalError largestNumericalLocalError) {
        this.largestNumericalLocalError = largestNumericalLocalError;
    }

    public NumericalError getLocalNumericalError() {
        return largestNumericalLocalError;
    }

    public void setGlobalNumericalError(NumericalError largestNumericalGlobalError) {
        this.largestNumericalGlobalError = largestNumericalGlobalError;
    }

    public NumericalError getGlobalNumericalError() {
        return largestNumericalGlobalError;
    }

    public void setLocalDisplacementDeviation(DisplacementDeviation largestDisplacementLocalError) {
        this.largestLocalDeviation = largestDisplacementLocalError;
    }

    public UpdateModule getLocalErrorModule() {
        return localErrorModule;
    }

    public double getLocalErrorUpdate() {
        return localErrorUpdate;
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


}
