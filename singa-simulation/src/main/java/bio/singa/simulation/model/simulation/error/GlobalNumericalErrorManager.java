package bio.singa.simulation.model.simulation.error;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.simulation.model.simulation.Updatable;
import bio.singa.simulation.model.simulation.UpdateScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static bio.singa.simulation.model.simulation.error.ErrorManager.CalculationStage;
import static bio.singa.simulation.model.simulation.error.ErrorManager.CalculationStage.*;
import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason;

/**
 * @author cl
 */
public class GlobalNumericalErrorManager implements UpdateEventListener<Reason> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalNumericalErrorManager.class);
    private static final double DEFAULT_GLOBAL_NUMERICAL_TOLERANCE = 1e-2;
    private static final double DEFAULT_GLOBAL_NEGLIGIBILITY_THRESHOLD = 1e-6;

    private UpdateScheduler updateScheduler;
    private CalculationStage currentStage;
    private double tolerance = DEFAULT_GLOBAL_NUMERICAL_TOLERANCE;
    private double negligibility = DEFAULT_GLOBAL_NEGLIGIBILITY_THRESHOLD;
    private NumericalError error;
    private boolean errorAcceptable;

    private int skips = 0;

    public GlobalNumericalErrorManager(UpdateScheduler updateScheduler) {
        this.updateScheduler = updateScheduler;
        currentStage = SETUP_STAGE;
        reset();
    }

    public void evaluateError() {
        // check skipping
        if (currentStage.equals(SKIP)) {
            // if number of updatables increased or more than 100 skips occured
            if (skips >= 100) {
                currentStage = SETUP_STAGE;
                skips = 0;
            } else {
                skips++;
            }
        }
        switch (currentStage) {
            case SETUP_STAGE:
                processSetupStage();
                break;
            case EVALUATION_STAGE:
                processEvaluationStage();
                checkSkipping();
                break;
            case TIME_STEP_RESCALED:
                errorAcceptable = false;
                break;
            case SKIP:
        }
    }

    public void resolveProblem() {
        //System.out.println(" resolve " + currentStage);
        switch (currentStage) {
            case SETUP_STAGE:
                currentStage = EVALUATION_STAGE;
                break;
            case EVALUATION_STAGE:
                updateScheduler.getTimeStepManager().decreaseTimeStep(Reason.GLOBAL_ERROR);
                currentStage = SETUP_STAGE;
                break;
            case TIME_STEP_RESCALED:
                currentStage = SETUP_STAGE;
                break;
            default:
                currentStage = SETUP_STAGE;
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
            updateScheduler.getUpdatables().forEach(updatable -> updatable.getConcentrationManager().revertToOriginalConcentrations());
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

    @Override
    public void onEventReceived(Reason reason) {
        // if we are currently skipping
        if (currentStage.equals(SKIP)) {
            // and this was not an time step increase
            if (!reason.equals(Reason.INCREASE)) {
                // keep skipping
                return;
            }
        }
        currentStage = TIME_STEP_RESCALED;
    }

    public void reset() {
        if (!currentStage.equals(SKIP)) {
            currentStage = SETUP_STAGE;
        }
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

    public double getNegligibility() {
        return negligibility;
    }

    public void setNegligibility(double negligibility) {
        this.negligibility = negligibility;
    }

    public boolean errorIsCritical() {
        // global numerical error was not close to tolerance but sufficiently small
        return tolerance - error.getValue() <= 0.2 * tolerance;
    }

    public void checkSkipping() {
        //System.out.println(" global error " + error.getValue());
        if (errorIsNegligible()) {
            currentStage = SKIP;
        } else {
            currentStage = EVALUATION_STAGE;
        }
    }

    public boolean errorIsAcceptable() {
        return errorAcceptable;
    }

    public boolean errorIsNegligible() {
        return error.getValue() < negligibility;
    }

}
