package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author cl
 */
public class GlobalNumericalErrorManager {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);

    private UpdateScheduler updateScheduler;
    private CalculationStage currentStage;

    private boolean calculateGlobalError;
    private boolean globalErrorAcceptable;

    public GlobalNumericalErrorManager(UpdateScheduler updateScheduler) {
        this.updateScheduler = updateScheduler;
    }

    public void evaluateGlobalNumericalAccuracy() {
        if (calculateGlobalError) {
            // calculate half step concentrations for subsequent evaluation
            // for each node
            for (Updatable updatable : updateScheduler.getUpdatables()) {
                // calculate interim container (added current updates with checked local error)
                // set half step concentrations y(t+1/2dt) for interim containers
                // backup current concentrations and set current concentration to interim concentrations
                updatable.getConcentrationManager().setInterimAndUpdateCurrentConcentrations();
            }
            globalErrorAcceptable = false;
            calculateGlobalError = false;
        } else {
            // evaluate global numerical accuracy
            // for each node
            NumericalError largestGlobalError = NumericalError.MINIMAL_EMPTY_ERROR;
            for (Updatable updatable : updateScheduler.getUpdatables()) {
                // determine full concentrations with full update and 2 * half update
                updatable.getConcentrationManager().determineComparisionConcentrations();
                // determine error between both
                NumericalError globalError = updatable.getConcentrationManager().determineGlobalNumericalError();
                if (largestGlobalError.isSmallerThan(globalError)) {
                    globalError.setUpdatable(updatable);
                    largestGlobalError = globalError;
                }
            }
            // set interim check false if global error is to large and true if you can continue
            if (largestGlobalError.getValue() > updateScheduler.getErrorManager().getGlobalNumericalTolerance()) {
                // System.out.println("rejected global error: "+largestGlobalError+" @ "+TimeFormatter.formatTime(UnitRegistry.getTime()));
                updateScheduler.decreaseTimeStep(String.format("global error exceeded %s", largestGlobalError.toString()));
                globalErrorAcceptable = false;
                calculateGlobalError = true;
            } else {
                // System.out.println("accepted global error: "+largestGlobalError+ " @ "+TimeFormatter.formatTime(UnitRegistry.getTime()));
                globalErrorAcceptable = true;
            }
            if (updateScheduler.getErrorManager().getLocalNumericalError().getValue() != NumericalError.MINIMAL_EMPTY_ERROR.getValue()) {
                logger.debug("Largest local error : {} ({}, {}, {})", updateScheduler.getErrorManager().getLocalNumericalError().getValue(), updateScheduler.getErrorManager().getLocalNumericalError().getChemicalEntity(), updateScheduler.getErrorManager().getLocalNumericalError().getUpdatable().getStringIdentifier(), updateScheduler.getErrorManager().getLocalNumericalErrorModule());
            } else {
                logger.debug("Largest local error : minimal");
            }
            logger.debug("Largest global error: {} ({}, {})", largestGlobalError.getValue(), largestGlobalError.getChemicalEntity(), largestGlobalError.getUpdatable().getStringIdentifier());
            updateScheduler.getErrorManager().setGlobalNumericalError(largestGlobalError);
        }

    }

    public enum CalculationStage {

        FIRST, SECOND;

    }

    public boolean isCalculateGlobalError() {
        return calculateGlobalError;
    }

    public void setCalculateGlobalError(boolean calculateGlobalError) {
        this.calculateGlobalError = calculateGlobalError;
    }

    public boolean isGlobalErrorAcceptable() {
        return globalErrorAcceptable;
    }

    public void setGlobalErrorAcceptable(boolean globalErrorAcceptable) {
        this.globalErrorAcceptable = globalErrorAcceptable;
    }
}
