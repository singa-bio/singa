package bio.singa.simulation.model.simulation.error;

import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bio.singa.simulation.model.simulation.error.GlobalNumericalErrorManager.CalculationStage.SETUP_STAGE;
import static bio.singa.simulation.model.simulation.error.GlobalNumericalErrorManager.CalculationStage.EVALUATION_STAGE;

/**
 * @author cl
 */
public class GlobalNumericalErrorManager {

    private static final Logger logger = LoggerFactory.getLogger(UpdateScheduler.class);
    private static final double DEFAULT_GLOBAL_NUMERICAL_TOLERANCE = 0.01;
    private UpdateScheduler updateScheduler;
    private CalculationStage currentStage;
    private double tolerance = DEFAULT_GLOBAL_NUMERICAL_TOLERANCE;
    private NumericalError error;
    private boolean errorAcceptable;

    public GlobalNumericalErrorManager(UpdateScheduler updateScheduler) {
        this.updateScheduler = updateScheduler;
        error = NumericalError.MINIMAL_EMPTY_ERROR;
        reset();
    }

    public void evaluateError() {
        switch (currentStage) {
            case SETUP_STAGE:
                processSetupStage();
                break;
            case EVALUATION_STAGE:
                processEvaluationStage();
                break;
        }
    }

    private void processSetupStage() {
        // calculate half step concentrations for subsequent evaluation
        // for each node
        for (Updatable updatable : updateScheduler.getUpdatables()) {
            // calculate interim container (added current updates with checked local error)
            // set half step concentrations y(t+1/2dt) for interim containers
            // backup current concentrations and set current concentration to interim concentrations
            updatable.getConcentrationManager().setInterimAndUpdateCurrentConcentrations();
        }
        errorAcceptable = false;
    }

    private void processEvaluationStage() {
        error = determineGlobalError();
        if (error.getValue() > tolerance) {
            errorAcceptable = false;
        } else {
            errorAcceptable = true;
        }
    }

    private NumericalError determineGlobalError() {
        // for each node
        NumericalError largestError = NumericalError.MINIMAL_EMPTY_ERROR;
        for (Updatable updatable : updateScheduler.getUpdatables()) {
            // determine full concentrations with full update and 2 * half update
            updatable.getConcentrationManager().determineComparisionConcentrations();
            // determine error between both
            NumericalError currentError = updatable.getConcentrationManager().determineGlobalNumericalError();
            if (largestError.isSmallerThan(currentError)) {
                currentError.setUpdatable(updatable);
                largestError = currentError;
            }
        }
        return largestError;
    }

    public void resolveProblem() {
        switch (currentStage) {
            case SETUP_STAGE:
                currentStage = EVALUATION_STAGE;
                break;
            case EVALUATION_STAGE:
                updateScheduler.decreaseTimeStep("global error exceeded ");
                currentStage = SETUP_STAGE;
                break;
        }
    }

    public void reset() {
        currentStage = SETUP_STAGE;
        errorAcceptable = true;
        error = NumericalError.MINIMAL_EMPTY_ERROR;
    }

    public NumericalError getError() {
        return error;
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    public boolean errorIsCritical() {
        // global numerical error was not close to tolerance but sufficiently small
        return tolerance - error.getValue() <= 0.2 * tolerance;
    }

    public boolean errorIsAcceptable() {
        return errorAcceptable;
    }

    public enum CalculationStage {
        SETUP_STAGE, EVALUATION_STAGE, SKIP;
    }

}
