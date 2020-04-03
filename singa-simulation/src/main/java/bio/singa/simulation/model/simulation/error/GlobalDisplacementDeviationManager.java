package bio.singa.simulation.model.simulation.error;

import bio.singa.core.events.UpdateEventListener;
import bio.singa.mathematics.vectors.Vector2D;
import bio.singa.simulation.model.agents.pointlike.Vesicle;
import bio.singa.simulation.model.agents.pointlike.VesicleLayer;
import bio.singa.simulation.model.simulation.UpdateScheduler;

import static bio.singa.simulation.model.simulation.error.DisplacementDeviation.MAXIMAL_NEGATIVE_DEVIATION;
import static bio.singa.simulation.model.simulation.error.ErrorManager.CalculationStage;
import static bio.singa.simulation.model.simulation.error.ErrorManager.CalculationStage.EVALUATION_STAGE;
import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason;
import static bio.singa.simulation.model.simulation.error.ErrorManager.Reason.GLOBAL_DEVIATION;

/**
 * @author cl
 */
public class GlobalDisplacementDeviationManager implements UpdateEventListener<Reason> {

    private UpdateScheduler updateScheduler;
    private CalculationStage currentStage;
    private DisplacementDeviation deviation;
    private boolean errorAcceptable;
    private VesicleLayer vesicleLayer;

    public GlobalDisplacementDeviationManager(UpdateScheduler updateScheduler) {
        this.updateScheduler = updateScheduler;
        vesicleLayer = updateScheduler.getSimulation().getVesicleLayer();
        deviation = DisplacementDeviation.MINIMAL_DEVIATION;
        currentStage = EVALUATION_STAGE;
        reset();
    }

    public void evaluateDeviation() {
        switch (currentStage) {
            case EVALUATION_STAGE:
                processEvaluationStage();
                break;
            case TIME_STEP_RESCALED:
                errorAcceptable = false;
                break;
            case SKIP:
                errorAcceptable = true;
                break;
        }

    }

    public void resolveProblem() {
        switch (currentStage) {
            case EVALUATION_STAGE:
                TimeStepManager.decreaseTimeStep(GLOBAL_DEVIATION);
                break;
            case TIME_STEP_RESCALED:
                currentStage = EVALUATION_STAGE;
                break;
            case SKIP:
                break;
        }

    }

    private void processEvaluationStage() {
        if (vesicleLayer.getVesicles().isEmpty()) {
            errorAcceptable = true;
            deviation = MAXIMAL_NEGATIVE_DEVIATION;
            return;
        }
        deviation = determineGlobalDeviation();
        errorAcceptable = deviation.getValue() < updateScheduler.getErrorManager().getDisplacementUpperThreshold();
    }

    private DisplacementDeviation determineGlobalDeviation() {
        DisplacementDeviation largestDeviation = MAXIMAL_NEGATIVE_DEVIATION;
        for (Vesicle vesicle : vesicleLayer.getVesicles()) {
            Vector2D totalDisplacement = vesicle.calculateTotalDisplacement();
            // skip if total displacement is zero
            if (totalDisplacement.equals(Vector2D.ZERO)) {
                continue;
            }
            double length = totalDisplacement.getMagnitude();
            // determine fraction of maximal allowed error
            double deviation = Math.log10(totalDisplacement.getMagnitude() /  updateScheduler.getErrorManager().getDisplacementReferenceLength());
            if (deviation > largestDeviation.getValue()) {
                largestDeviation = new DisplacementDeviation(vesicle, deviation);
            }
        }
        return largestDeviation;
    }

    @Override
    public void onEventReceived(Reason reason) {
    }

    public DisplacementDeviation getDeviation() {
        return deviation;
    }

    public boolean deviationIsCritical() {
        return deviation.getValue() > updateScheduler.getErrorManager().getDisplacementLowerThreshold();
    }

    public boolean deviationIsAcceptable() {
        return errorAcceptable;
    }

    public void reset() {
        errorAcceptable = false;
    }

}
