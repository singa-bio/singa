package bio.singa.simulation.model.simulation.error;

import bio.singa.features.parameters.Environment;
import bio.singa.features.units.UnitRegistry;
import bio.singa.simulation.model.modules.UpdateModule;

/**
 * @author cl
 */
public class ErrorManager {

    private static final double DEFAULT_LOCAL_NUMERICAL_TOLERANCE = 0.01;
    private static final double DEFAULT_GLOBAL_NUMERICAL_TOLERANCE = 0.01;

    private static final double DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF = 1e-100;
    private static final double DEFAULT_NUMERICAL_INSTABILITY_CUTOFF = 100;

    private static final double DEFAULT_DISPLACEMENT_CUTOFF_FACTOR = 1.0 / 10.0;
    private static final double DEFAULT_LOCAL_DEVIATION_TOLERANCE = 0.8;
    private static final double DEFAULT_GLOBAL_DEVIATION_TOLERANCE = 0.8;

    private double localNumericalTolerance = DEFAULT_LOCAL_NUMERICAL_TOLERANCE;
    private NumericalError localNumericalError;
    private UpdateModule localNumericalErrorModule;
    private double localNumericalErrorUpdate;

    private double globalNumericalTolerance = DEFAULT_GLOBAL_NUMERICAL_TOLERANCE;
    private NumericalError globalNumericalError;

    private double numericalNegligenceCutoff = DEFAULT_NUMERICAL_NEGLIGENCE_CUTOFF;
    private double numericalInstabilityCutoff = DEFAULT_NUMERICAL_INSTABILITY_CUTOFF;

    private double localDisplacementTolerance = DEFAULT_LOCAL_DEVIATION_TOLERANCE;
    private DisplacementDeviation localDisplacementDeviation;
    private UpdateModule localDisplacementDeviationModule;

    private double globalDisplacementTolerance = DEFAULT_GLOBAL_DEVIATION_TOLERANCE;
    private DisplacementDeviation globalDisplacementDeviation;

    private double displacementCutoffFactor = DEFAULT_DISPLACEMENT_CUTOFF_FACTOR;
    private double displacementCutoff;

    public ErrorManager() {
        localNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        globalNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
        localDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
        globalDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
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

    public void setLocalNumericalError(NumericalError largestLocalNumericalError) {
        this.localNumericalError = largestLocalNumericalError;
    }

    public NumericalError getGlobalNumericalError() {
        return globalNumericalError;
    }

    public void setGlobalNumericalError(NumericalError largestGlobalNumericalError) {
        this.globalNumericalError = largestGlobalNumericalError;
    }

    public void setLocalDisplacementDeviation(DisplacementDeviation largestDisplacementDeviation) {
        this.localDisplacementDeviation = largestDisplacementDeviation;
    }

    public DisplacementDeviation getGlobalDisplacementDeviation() {
        return globalDisplacementDeviation;
    }

    public void setGlobalDisplacementDeviation(DisplacementDeviation globalDisplacementDeviation) {
        this.globalDisplacementDeviation = globalDisplacementDeviation;
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
        globalNumericalError = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public void resetLocalDisplacementDeviation() {
        localDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
    }

    public void resetGlobalDisplacementDeviation() {
        globalDisplacementDeviation = DisplacementDeviation.MINIMAL_DEVIATION;
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
        // global numerical error was close to tolerance
        if (globalNumericalTolerance - globalNumericalError.getValue() <= 0.2 * globalNumericalTolerance) {
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
